package kg.airport.airportproject.service.impl;

import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.dto.JwtTokenResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.ApplicationUsersMapper;
import kg.airport.airportproject.mapper.AuthenticationMapper;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.security.JwtTokenHandler;
import kg.airport.airportproject.service.AuthenticationService;
import kg.airport.airportproject.validator.ApplicationUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUsersEntityRepository applicationUsersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;
    private final UserPositionsEntityRepository userPositionsEntityRepository;
    private final ApplicationUserValidator applicationUserValidator;
    private final UserDetailsService userDetailsService;
    private final JwtTokenHandler jwtTokenHandler;

    @Autowired
    public AuthenticationServiceImpl(
            PasswordEncoder passwordEncoder,
            ApplicationUsersEntityRepository applicationUsersEntityRepository,
            UserRolesEntityRepository userRolesEntityRepository,
            UserPositionsEntityRepository userPositionsEntityRepository,
            ApplicationUserValidator applicationUserValidator,
            UserDetailsService userDetailsService,
            JwtTokenHandler jwtTokenHandler
    ) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUsersEntityRepository = applicationUsersEntityRepository;
        this.userRolesEntityRepository = userRolesEntityRepository;
        this.userPositionsEntityRepository = userPositionsEntityRepository;
        this.applicationUserValidator = applicationUserValidator;
        this.userDetailsService = userDetailsService;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    public ApplicationUserResponseDto registerNewClient(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExists,
            UsernameAlreadyExistsException,
            InvalidUserInfoException,
            InvalidCredentialsException,
            InvalidIdException
    {
        this.applicationUserValidator.validateUserRequestDto(requestDto);
        ApplicationUsersEntity applicationUsersEntity =
                ApplicationUsersMapper.mapApplicationUserRequestDtoToEntity(requestDto);

        Optional<UserPositionsEntity> userPositionsEntityOptional =
                this.userPositionsEntityRepository.getUserPositionsEntityByPositionTitle("CLIENT");
        if(userPositionsEntityOptional.isEmpty()) {
            throw new UserPositionNotExists("Введенной позиции пользователя не существует в системе!");
        }
        UserPositionsEntity userPosition = userPositionsEntityOptional.get();
        applicationUsersEntity.setUserPosition(userPosition);

        List<UserRolesEntity> userRolesEntityList =
                this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(userPosition);
        if(userRolesEntityList.isEmpty()) {
            throw new UserRolesNotAssignedException(
                    String.format(
                            "Для позиции пользователя %s не задано ни одной роли",
                            userPosition.getPositionTitle()
                    )
            );
        }
        applicationUsersEntity.setUserRolesEntityList(userRolesEntityList);
        applicationUsersEntity.setPassword(this.passwordEncoder.encode(applicationUsersEntity.getPassword()));

        applicationUsersEntity = this.applicationUsersEntityRepository.save(applicationUsersEntity);
        return ApplicationUsersMapper.mapToApplicationUserResponseDto(applicationUsersEntity);
    }

    @Override
    public ApplicationUserResponseDto registerNewEmployee(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExists,
            UsernameAlreadyExistsException,
            InvalidUserInfoException,
            InvalidCredentialsException,
            InvalidIdException
    {
        this.applicationUserValidator.validateUserRequestDto(requestDto);
        ApplicationUsersEntity applicationUsersEntity =
                ApplicationUsersMapper.mapApplicationUserRequestDtoToEntity(requestDto);

        Optional<UserPositionsEntity> userPositionsEntityOptional =
                this.userPositionsEntityRepository.getUserPositionsEntityById(requestDto.getPositionId());
        if(userPositionsEntityOptional.isEmpty()) {
            throw new UserPositionNotExists("Введенной позиции пользователя не существует в системе!");
        }
        UserPositionsEntity userPosition = userPositionsEntityOptional.get();
        applicationUsersEntity.setUserPosition(userPosition);

        List<UserRolesEntity> userRolesEntityList =
                this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(userPosition);
        if(userRolesEntityList.isEmpty()) {
            throw new UserRolesNotAssignedException(
                    String.format(
                            "Для позиции пользователя %s не задано ни одной роли",
                            userPosition.getPositionTitle()
                    )
            );
        }
        applicationUsersEntity.setUserRolesEntityList(userRolesEntityList);
        applicationUsersEntity.setPassword(this.passwordEncoder.encode(applicationUsersEntity.getPassword()));

        applicationUsersEntity = this.applicationUsersEntityRepository.save(applicationUsersEntity);
        return ApplicationUsersMapper.mapToApplicationUserResponseDto(applicationUsersEntity);
    }

    @Override
    public JwtTokenResponseDto login(ApplicationUserCredentialsRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        UserDetails authenticatedUser = this.userDetailsService.loadUserByUsername(username);
        if(this.passwordEncoder.matches(password, authenticatedUser.getPassword())){
            Authentication authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            authenticatedUser,
                            null,
                            authenticatedUser.getAuthorities()
                    );
            return AuthenticationMapper.mapToJwtTokenResponseDto(this.jwtTokenHandler.generateToken(authentication));
        }
        return AuthenticationMapper.mapToJwtTokenResponseDto("Incorrect Password or Username");
    }
}
