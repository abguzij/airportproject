package kg.airport.airportproject.validator.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.QUserRolesEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.validator.ApplicationUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ApplicationUserValidatorImpl implements ApplicationUserValidator {
    private final ApplicationUsersEntityRepository applicationUsersEntityRepository;

    @Autowired
    public ApplicationUserValidatorImpl(
            ApplicationUsersEntityRepository applicationUsersEntityRepository
    ) {
        this.applicationUsersEntityRepository = applicationUsersEntityRepository;
    }

    @Override
    public void validateUserRequestDto(ApplicationUserRequestDto requestDto)
            throws InvalidCredentialsException,
            InvalidUserInfoException,
            UsernameAlreadyExistsException,
            InvalidIdException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемый пользователь не может быть null!");
        }
        if(Objects.isNull(requestDto.getUsername()) || requestDto.getUsername().isEmpty()) {
            throw new InvalidCredentialsException("Имя пользователя не может быть null или пустым!");
        }
        this.checkUsernameForDuplicates(requestDto.getUsername());
        if(Objects.isNull(requestDto.getPassword()) || requestDto.getPassword().isEmpty()) {
            throw new InvalidCredentialsException("Пароль пользователя не может быть null или пустым!");
        }
        if(Objects.isNull(requestDto.getFullName()) || requestDto.getFullName().isEmpty()) {
            throw new InvalidUserInfoException("ФИО пользователя не может быть null или пустым!");
        }
        if(Objects.isNull(requestDto.getPositionId())) {
            throw new InvalidUserInfoException("ID позиции пользователя не может быть null!");
        }
        if(requestDto.getPositionId() < 1L) {
            throw new InvalidIdException("ID позиции пользователя не может быть меньше 1!");
        }
    }

    @Override
    public void checkUsernameForDuplicates(String username)
            throws UsernameAlreadyExistsException
    {
        Optional<ApplicationUsersEntity> possibleDuplicate =
                this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(username);
        if(possibleDuplicate.isPresent()) {
            throw new UsernameAlreadyExistsException("Пользователь с таким именем уже существует в системе!");
        }
    }
}
