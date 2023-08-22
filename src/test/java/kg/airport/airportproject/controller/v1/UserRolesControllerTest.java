package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.entity.UserRolesTestEntityProvider;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
import kg.airport.airportproject.security.TestAuthorizationHeaderProvider;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class UserRolesControllerTest {
    @MockBean
    private UserRolesEntityRepository userRolesEntityRepository;

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
            System.out.println(response);

            Assertions.assertEquals(
                    "В системе не было создано ни одной роли!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}