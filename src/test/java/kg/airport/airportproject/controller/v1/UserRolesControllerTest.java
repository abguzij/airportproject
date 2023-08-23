package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.entity.UserRolesTestEntityProvider;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
import kg.airport.airportproject.security.TestAuthorizationHeaderProvider;
import kg.airport.airportproject.security.TestCredentialsProvider;
import kg.airport.airportproject.service.ApplicationUserService;
import kg.airport.airportproject.service.UserRolesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class UserRolesControllerTest {
    @MockBean
    private UserRolesEntityRepository userRolesEntityRepository;
    @MockBean
    private ApplicationUserService applicationUserService;

    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testGetAllUserRoles_OK() {
        try {
            List<UserRolesEntity> foundUserRolesEntities = UserRolesTestEntityProvider.getAllUserRolesTestEntities();
            Mockito
                    .when(this.userRolesEntityRepository.findAll())
                    .thenReturn(foundUserRolesEntities);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/all");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<UserRoleResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<UserRoleResponseDto>>() {}
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(foundUserRolesEntities.size(), response.getBody().size());
            for (int i = 0; i < foundUserRolesEntities.size(); i++) {
                Assertions.assertEquals(foundUserRolesEntities.get(i).getId(), response.getBody().get(i).getId());
                Assertions.assertEquals(
                        foundUserRolesEntities.get(i).getRoleTitle(),
                        response.getBody().get(i).getRoleTitle()
                );
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllUserRoles_UserRolesNotFound() {
        try {
            Mockito
                    .when(this.userRolesEntityRepository.findAll())
                    .thenAnswer(invocationOnMock -> new ArrayList<>());

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/all");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "В системе не было создано ни одной роли!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetUserRoles_OK() {
        try {
            List<UserRolesEntity> foundRoles = UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                    UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
            );
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();

            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/of-user");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("userId", client.getId());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<List<UserRoleResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<UserRoleResponseDto>>() {}
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(foundRoles.size(), response.getBody().size());

            for (int i = 0; i < foundRoles.size(); i++) {
                Assertions.assertEquals(foundRoles.get(i).getId(), response.getBody().get(i).getId());
                Assertions.assertEquals(foundRoles.get(i).getRoleTitle(), response.getBody().get(i).getRoleTitle());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetUserRoles_UserRolesNotFound() {
        try {
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenAnswer(invocationOnMock -> client.setUserRolesEntityList(new ArrayList<>()));

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/of-user");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("userId", client.getId());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    String.format("Для пользователя с ID[%d] не задано ни одной роли!", client.getId()),
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersRoles_OK() {
        try {
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            List<Long> testIdList = List.of(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_ID);
            List<UserRolesEntity> foundRolesList = UserRolesTestEntityProvider
                    .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_TITLE);
            Mockito
                    .when(this.userRolesEntityRepository.getUserRolesEntitiesByIdIn(Mockito.eq(testIdList)))
                    .thenReturn(foundRolesList);
            Mockito
                    .when(this.userRolesEntityRepository.saveAll(Mockito.eq(foundRolesList)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/update");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("userId", client.getId());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<List<UserRolesEntity>> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(testIdList, httpHeaders),
                            new ParameterizedTypeReference<List<UserRolesEntity>>() {}
                    );
            System.out.println(response);

            List<UserRolesEntity> requiredRolesList = new ArrayList<>();
            requiredRolesList.addAll(
                    UserRolesTestEntityProvider
                            .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE)
            );
            requiredRolesList.addAll(foundRolesList);

            Assertions.assertEquals(requiredRolesList.size(), response.getBody().size());
            for (int i = 0; i < response.getBody().size(); i++) {
                Assertions.assertEquals(requiredRolesList.get(i).getId(), response.getBody().get(i).getId());
                Assertions.assertEquals(
                        requiredRolesList.get(i).getRoleTitle(),
                        response.getBody().get(i).getRoleTitle()
                );
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersRoles_UserRolesNotFound() {
        try {
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            List<Long> testIdList = List.of(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_ID);
            List<UserRolesEntity> foundRolesList = UserRolesTestEntityProvider
                    .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_TITLE);
            Mockito
                    .when(this.userRolesEntityRepository.getUserRolesEntitiesByIdIn(Mockito.eq(testIdList)))
                    .thenAnswer(invocationOnMock -> new ArrayList<>());

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/update");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("userId", client.getId());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(testIdList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "По заданным ID не найдено ни одной роли!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersRoles_EmptyIdList() {
        try {
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            List<Long> testIdList = new ArrayList<Long>();

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/roles/update");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("userId", client.getId());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(testIdList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Список ID добавляемых пользователю ролей не может быть null или пустым!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}