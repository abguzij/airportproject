package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.PartsTestEntityProvider;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.repository.PartsEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
import kg.airport.airportproject.service.PartsService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = UserDetailsConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
class PartsControllerTest {
    @MockBean
    private PartsEntityRepository partsEntityRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;
    @Autowired
    private PartsService partsService;

    @LocalServerPort
    private int port;

    @Test
    void testRegisterNewPart_OK() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            PartsEntity partsEntity = PartsTestEntityProvider.getTestPartsEntity();

            Mockito
                    .when(this.partsEntityRepository.save(Mockito.eq(partsEntity)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<PartResponseDto> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDto, httpHeaders),
                            PartResponseDto.class
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(requestDto.getTitle(), response.getBody().getTitle());
            Assertions.assertEquals(requestDto.getAircraftType(), response.getBody().getAircraftType());
            Assertions.assertEquals(requestDto.getPartType(), response.getBody().getPartType());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewPart_InvalidAircraftType() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            requestDto.setAircraftType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDto, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемого самолета не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewPart_InvalidPartType() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            requestDto.setPartType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(new PartRequestDto(), httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемой детали не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewParts_OK() {
        try {
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetAllParts_OK() {
        try {
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetPartTypes_OK() {
        try {
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}