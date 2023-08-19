package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.exception.UserPositionNotExistsException;
import kg.airport.airportproject.exception.UserRolesNotAssignedException;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenHandler;
import kg.airport.airportproject.validator.ApplicationUserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = {UserDetailsConfigurationTest.class, SecurityConfigurationTest.class})
@TestPropertySource(value = "classpath:test.properties")
public class AuthenticationsControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @MockBean
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @MockBean
    private UserRolesEntityRepository userRolesEntityRepository;
    @MockBean
    private UserPositionsEntityRepository userPositionsEntityRepository;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private ApplicationUserValidator applicationUserValidator;

    @LocalServerPort
    private int port;

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
                .thenAnswer(invocationOnMock -> UserRolesTestEntityProvider.getTestClientRoleEntity(
                        UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
                ));

        Mockito
                .when(this.applicationUsersEntityRepository.save(Mockito.any(ApplicationUsersEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    ApplicationUsersEntity applicationUser =
                            (ApplicationUsersEntity) invocationOnMock.getArguments()[0];
                    applicationUser.setRegisteredAt(LocalDateTime.now());
                    return applicationUser.setId(ApplicationUsersTestEntityProvider.TEST_USER_ID);
                });

        try {
            Mockito
                    .doNothing()
                    .when(this.applicationUserValidator)
                    .validateUserRequestDto(Mockito.any(ApplicationUserRequestDto.class));

            URI uri = new URI( "http://localhost:" + port + "/v1/auth/register");

            ApplicationUserResponseDto responseDto = this.testRestTemplate.postForObject(
                    uri,
                    requestDto,
                    ApplicationUserResponseDto.class
            );

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

        URI uri = new URI( "http://localhost:" + port + "/v1/auth/register");

        ErrorResponse response = this.testRestTemplate.postForObject(
                uri,
                requestDto,
                ErrorResponse.class
        );

        Assertions.assertEquals(
                "Введенной позиции пользователя не существует в системе!",
                response.getMessage()
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

        URI uri = new URI( "http://localhost:" + port + "/v1/auth/register");

        ErrorResponse response = this.testRestTemplate.postForObject(
                uri,
                requestDto,
                ErrorResponse.class
        );
        Assertions.assertEquals(
                String.format(
                        "Для позиции пользователя %s не задано ни одной роли",
                        UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_TITLE
                ),
                response.getMessage()
        );
    }

    @Test
    public void testLogin_OK() {
        ApplicationUserCredentialsRequestDto requestDto = new ApplicationUserCredentialsRequestDto();
        requestDto.setPassword(ApplicationUsersTestEntityProvider.TEST_RAW_PASSWORD);
        requestDto.setUsername(ApplicationUsersTestEntityProvider.TEST_USERNAME);

        Mockito
                .when(this.userDetailsService.loadUserByUsername(ApplicationUsersTestEntityProvider.TEST_USERNAME))
                .thenAnswer(invocationOnMock -> ApplicationUsersTestEntityProvider.getTestClientEntity());

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/auth/login");

            JwtTokenResponseDto response = this.testRestTemplate.postForObject(
                    uri,
                    requestDto,
                    JwtTokenResponseDto.class
            );

            String usernameFromJwtToken = this.jwtTokenHandler.getUsernameFromToken(response.getJwtToken());
            Assertions.assertEquals(ApplicationUsersTestEntityProvider.TEST_USERNAME, usernameFromJwtToken);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testLogin_UsernameNotFound() {
        ApplicationUserCredentialsRequestDto requestDto = new ApplicationUserCredentialsRequestDto();
        requestDto.setPassword(ApplicationUsersTestEntityProvider.TEST_RAW_PASSWORD);
        requestDto.setUsername(ApplicationUsersTestEntityProvider.TEST_USERNAME);

        Mockito
                .when(this.userDetailsService.loadUserByUsername(ApplicationUsersTestEntityProvider.TEST_USERNAME))
                .thenThrow(new UsernameNotFoundException("Пользователь с таким именем не найден или был удален!"));

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/auth/login");

            ErrorResponse response = this.testRestTemplate.postForObject(
                    uri,
                    requestDto,
                    ErrorResponse.class
            );

            Assertions.assertEquals(
                    "Пользователь с таким именем не найден или был удален!",
                    response.getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}