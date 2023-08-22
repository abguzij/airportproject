package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.exception.UserPositionNotExistsException;
import kg.airport.airportproject.exception.UserRolesNotAssignedException;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.security.JwtTokenHandler;
import kg.airport.airportproject.service.impl.AuthenticationServiceImpl;
import kg.airport.airportproject.validator.ApplicationUserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(value = MockitoExtension.class)
public class AuthenticationServiceTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Mock
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Mock
    private UserRolesEntityRepository userRolesEntityRepository;
    @Mock
    private UserPositionsEntityRepository userPositionsEntityRepository;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private ApplicationUserValidator applicationUserValidator;

    @Spy
    private JwtTokenHandler jwtTokenHandler;

    private AuthenticationService authenticationService;

    @BeforeEach
    private void beforeEach() {
        this.authenticationService = new AuthenticationServiceImpl(
                this.passwordEncoder,
                this.applicationUsersEntityRepository,
                this.userRolesEntityRepository,
                this.userPositionsEntityRepository,
                this.applicationUserValidator,
                this.userDetailsService,
                this.jwtTokenHandler
        );
    }

    @Test
    public void testRegisterNewClient_OK() {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID);

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityByPositionTitle(
                        Mockito.any(String.class)
                ))
                .thenAnswer(
                        invocationOnMock -> Optional.of(
                                UserPositionsTestEntityProvider.getTestUserPositionsEntity(requestDto.getPositionId())
                        )
                );

        Mockito
                .when(this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(
                        Mockito.any(UserPositionsEntity.class)
                ))
                .thenAnswer(invocationOnMock -> UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                        UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
                ));

        Mockito
                .when(this.applicationUsersEntityRepository.save(Mockito.any(ApplicationUsersEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    ApplicationUsersEntity applicationUser =
                            (ApplicationUsersEntity) invocationOnMock.getArguments()[0];
                    applicationUser.setRegisteredAt(LocalDateTime.now());
                    return applicationUser.setId(ApplicationUsersTestEntityProvider.TEST_CLIENT_USER_ID);
                });

        try {
            Mockito
                    .doNothing()
                    .when(this.applicationUserValidator)
                    .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

            ApplicationUserResponseDto responseDto = this.authenticationService.registerNewClient(requestDto);

            Assertions.assertEquals(requestDto.getUsername(), responseDto.getUsername());
            Assertions.assertEquals(requestDto.getFullName(), responseDto.getFullName());
            Assertions.assertEquals(
                    UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_TITLE, responseDto.getPositionTitle()
            );
            Assertions.assertEquals(Boolean.TRUE, responseDto.getEnabled());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewClient_UserPositionNotExists() throws Exception {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID);
        Mockito
                .doNothing()
                .when(this.applicationUserValidator)
                .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityByPositionTitle(
                        Mockito.any(String.class)
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                UserPositionNotExistsException.class,
                () -> this.authenticationService.registerNewClient(requestDto)
        );
        Assertions.assertEquals(
                "Введенной позиции пользователя не существует в системе!",
                exception.getMessage()
        );
    }

    @Test
    public void testRegisterNewClient_UserRolesNotAssigned() throws Exception {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID);
        Mockito
                .doNothing()
                .when(this.applicationUserValidator)
                .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityByPositionTitle(
                        Mockito.any(String.class)
                ))
                .thenAnswer(
                        invocationOnMock -> Optional.of(
                                UserPositionsTestEntityProvider.getTestUserPositionsEntity(requestDto.getPositionId())
                        )
                );

        Mockito
                .when(this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(
                        Mockito.any(UserPositionsEntity.class)
                ))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                UserRolesNotAssignedException.class,
                () -> this.authenticationService.registerNewClient(requestDto)
        );
        Assertions.assertEquals(
                String.format(
                        "Для позиции пользователя %s не задано ни одной роли",
                        UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_TITLE
                ),
                exception.getMessage()
        );
    }

    @Test
    public void testRegisterNewEmployee_OK() {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(UserPositionsTestEntityProvider.TEST_STEWARD_POSITION_ID);

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityById(
                        Mockito.eq(requestDto.getPositionId())
                ))
                .thenAnswer(
                        invocationOnMock -> Optional.of(
                                UserPositionsTestEntityProvider.getTestUserPositionsEntity(requestDto.getPositionId())
                        )
                );

        Mockito
                .when(this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(
                        Mockito.any(UserPositionsEntity.class)
                ))
                .thenAnswer(invocationOnMock -> UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                        UserRolesTestEntityProvider.TEST_STEWARD_ROLE_TITLE
                ));

        Mockito
                .when(this.applicationUsersEntityRepository.save(Mockito.any(ApplicationUsersEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    ApplicationUsersEntity applicationUser =
                            (ApplicationUsersEntity) invocationOnMock.getArguments()[0];
                    applicationUser.setRegisteredAt(LocalDateTime.now());
                    return applicationUser.setId(ApplicationUsersTestEntityProvider.TEST_CLIENT_USER_ID);
                });

        try {
            Mockito
                    .doNothing()
                    .when(this.applicationUserValidator)
                    .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

            ApplicationUserResponseDto responseDto = this.authenticationService.registerNewEmployee(requestDto);

            Assertions.assertEquals(requestDto.getUsername(), responseDto.getUsername());
            Assertions.assertEquals(requestDto.getFullName(), responseDto.getFullName());
            Assertions.assertEquals(
                    UserPositionsTestEntityProvider.TEST_STEWARD_POSITION_TITLE, responseDto.getPositionTitle()
            );
            Assertions.assertEquals(Boolean.TRUE, responseDto.getEnabled());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewEmployee_UserPositionNotExists() throws Exception {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(ApplicationUsersTestEntityProvider.TEST_CLIENT_USER_ID);
        Mockito
                .doNothing()
                .when(this.applicationUserValidator)
                .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityById(
                        Mockito.eq(requestDto.getPositionId())
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                UserPositionNotExistsException.class,
                () -> this.authenticationService.registerNewEmployee(requestDto)
        );
        Assertions.assertEquals(
                "Введенной позиции пользователя не существует в системе!",
                exception.getMessage()
        );
    }

    @Test
    public void testRegisterNewEmployee_UserRolesNotAssigned() throws Exception {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider
                .getTestApplicationUserRequestDto(UserPositionsTestEntityProvider.TEST_STEWARD_POSITION_ID);
        Mockito
                .doNothing()
                .when(this.applicationUserValidator)
                .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntityById(
                        Mockito.eq(requestDto.getPositionId())
                ))
                .thenAnswer(
                        invocationOnMock -> Optional.of(
                                UserPositionsTestEntityProvider.getTestUserPositionsEntity(requestDto.getPositionId())
                        )
                );

        Mockito
                .when(this.userRolesEntityRepository.getUserRolesEntitiesByUserPositions(
                        Mockito.any(UserPositionsEntity.class)
                ))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                UserRolesNotAssignedException.class,
                () -> this.authenticationService.registerNewEmployee(requestDto)
        );
        Assertions.assertEquals(
                String.format(
                        "Для позиции пользователя %s не задано ни одной роли",
                        UserPositionsTestEntityProvider.TEST_STEWARD_POSITION_TITLE
                ),
                exception.getMessage()
        );
    }
}