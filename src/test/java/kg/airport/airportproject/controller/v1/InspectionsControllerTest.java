package kg.airport.airportproject.controller.v1;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.dto.PartStatesResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.repository.PartInspectionsEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class InspectionsControllerTest {
    private static final Long AIRCRAFT_ID = 1L;
    private static final Long INSPECTION_CODE = 1L;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;

    @MockBean
    private PartInspectionsEntityRepository partInspectionsEntityRepository;

    @LocalServerPort
    private int port;

    @Test
    public void testGetPartInspectionsHistory_OK() {
        Mockito
                .when(this.partInspectionsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new PartInspectionsEntity()
                                        .setInspectionCode(INSPECTION_CODE)
                                        .setAircraftsEntity(new AircraftsEntity().setId(AIRCRAFT_ID))
                                        .setPartsEntity(new PartsEntity().setId(1L))
                                        .setRegisteredAt(LocalDateTime.now()),
                                new PartInspectionsEntity()
                                        .setInspectionCode(INSPECTION_CODE + 1)
                                        .setAircraftsEntity(new AircraftsEntity().setId(AIRCRAFT_ID))
                                        .setPartsEntity(new PartsEntity().setId(2L))
                                        .setRegisteredAt(LocalDateTime.now())
                        )
                );
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", AIRCRAFT_ID)
                    .queryParam("inspectionCode", INSPECTION_CODE);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<PartInspectionsResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<PartInspectionsResponseDto>>() {}
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PartInspectionsResponseDto> partInspectionsResponseDtoList = response.getBody();
            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(AIRCRAFT_ID, partInspectionsResponseDtoList.get(0).getAircraftId()),
                    () -> Assertions.assertEquals(AIRCRAFT_ID, partInspectionsResponseDtoList.get(1).getAircraftId())
            );
            Assertions.assertAll(
                    "Проверка кода осмотра",
                    () -> Assertions.assertEquals(
                            INSPECTION_CODE + 1,
                            partInspectionsResponseDtoList.get(0).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            INSPECTION_CODE,
                            partInspectionsResponseDtoList.get(1).getInspectionCode()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(2L, partInspectionsResponseDtoList.get(0).getPartId()),
                    () -> Assertions.assertEquals(1L, partInspectionsResponseDtoList.get(1).getPartId())
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartInspectionsHistory_InvalidAircraftId() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", 0L)
                    .queryParam("inspectionCode", INSPECTION_CODE);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "ID самолета не может быть меньше 1!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartInspectionsHistory_InspectionsNotFound() {
        Mockito
                .when(this.partInspectionsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", AIRCRAFT_ID)
                    .queryParam("inspectionCode", INSPECTION_CODE);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    String.format(
                            "Для самолета с ID[%d] по заданным параметрам не найдено ни одного техосмотра!",
                            AIRCRAFT_ID
                    ),
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartLastAircraftInspection_OK() {
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(AIRCRAFT_ID))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(AIRCRAFT_ID))
                                        .setPartsEntity(new PartsEntity().setId(1L))
                                        .setInspectionCode(INSPECTION_CODE),
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(AIRCRAFT_ID))
                                        .setPartsEntity(new PartsEntity().setId(2L))
                                        .setInspectionCode(INSPECTION_CODE)
                        )
                );

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history/last-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", AIRCRAFT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<PartInspectionsResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<PartInspectionsResponseDto>>() {}
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PartInspectionsResponseDto> partInspectionsResponseDtoList = response.getBody();
            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(AIRCRAFT_ID, partInspectionsResponseDtoList.get(0).getAircraftId()),
                    () -> Assertions.assertEquals(AIRCRAFT_ID, partInspectionsResponseDtoList.get(1).getAircraftId())
            );
            Assertions.assertAll(
                    "Проверка кода осмотра",
                    () -> Assertions.assertEquals(
                            INSPECTION_CODE,
                            partInspectionsResponseDtoList.get(0).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            INSPECTION_CODE,
                            partInspectionsResponseDtoList.get(1).getInspectionCode()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(1L, partInspectionsResponseDtoList.get(0).getPartId()),
                    () -> Assertions.assertEquals(2L, partInspectionsResponseDtoList.get(1).getPartId())
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartLastAircraftInspection_InvalidAircraftId() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history/last-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", 0L);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "ID самолета не может быть меньше 1!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartLastAircraftInspection_InspectionsNotFound() {
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(AIRCRAFT_ID))
                .thenAnswer(invocationOnMock -> new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/history/last-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftId", AIRCRAFT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    String.format(
                            "Для самолета с ID[%d] по заданным параметрам не найдено ни одного техосмотра!",
                            AIRCRAFT_ID
                    ),
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartStates_OK() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/inspections/part-states");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<PartStatesResponseDto> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            PartStatesResponseDto.class
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(List.of(PartState.values()), response.getBody().getPartStates());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}