package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.UserPositionNotExists;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationUserService {
    @Transactional
    ApplicationUserResponseDto deleteCurrentAccount();

    @Transactional
    ApplicationUserResponseDto deleteAccountById(Long userId)
            throws ApplicationUserNotFoundException,
            InvalidIdException;

    @Transactional
    ApplicationUserResponseDto updateCurrentUsersInformation(ApplicationUserRequestDto applicationUserRequestDto);

    @Transactional
    ApplicationUserResponseDto updateUsersInformation(
            ApplicationUserRequestDto applicationUserRequestDto,
            Long userId
    )
            throws ApplicationUserNotFoundException,
            UserPositionNotExists,
            InvalidIdException;

    List<ApplicationUserResponseDto> getAllClients(
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter,
            Boolean isDeleted
    )
            throws ApplicationUserNotFoundException;

    List<ApplicationUserResponseDto> getAllEmployees(
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter,
            Boolean isFired,
            List<String> userPositions
    )
            throws ApplicationUserNotFoundException;

    List<ApplicationUserResponseDto> getAllFreeCrewMembers() throws ApplicationUserNotFoundException;

    List<ApplicationUserResponseDto> getAllFreeEngineers() throws ApplicationUserNotFoundException;

    List<ApplicationUsersEntity> getUserEntitiesByIdList(List<Long> userIdList)
            throws ApplicationUserNotFoundException,
            InvalidIdException;

    ApplicationUsersEntity getEngineerEntityById(Long engineerId)
            throws InvalidIdException,
            ApplicationUserNotFoundException;
}
