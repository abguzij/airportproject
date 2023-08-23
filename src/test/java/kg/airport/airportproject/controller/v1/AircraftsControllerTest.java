package kg.airport.airportproject.controller.v1;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.adapter.InMemoryUserDetailsManagerAdapter;
import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.mock.matcher.AircraftsStatusChangedMatcher;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.security.TestCredentialsProvider;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
import kg.airport.airportproject.security.TestApplicationUsersFactory;
import kg.airport.airportproject.mock.AuthenticationMockingUtils;
import kg.airport.airportproject.service.AircraftSeatsService;
import kg.airport.airportproject.service.ApplicationUserService;
import kg.airport.airportproject.service.PartInspectionService;
import kg.airport.airportproject.service.PartsService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = {SecurityConfigurationTest.class, SecurityConfigurationTest.class})
@TestPropertySource(value = "classpath:test.properties")
public class AircraftsControllerTest {
    private static final Long aircraftId = 1L;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final AircraftType searchedAircraftType = AircraftType.PLANE;
    private static final AircraftStatus searchedAircraftStatus = AircraftStatus.SERVICEABLE;
    private static final LocalDateTime startDate = LocalDateTime.parse("2020-02-12T23:40:00", formatter);
    private static final LocalDateTime endDate = LocalDateTime.parse("2021-02-12T23:40:00", formatter);

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;
    @Autowired
    private TestApplicationUsersFactory applicationUsersFactory;
    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private AircraftsEntityRepository aircraftsEntityRepository;
    @MockBean
    private PartsService partsService;
    @MockBean
    private AircraftSeatsService aircraftSeatsService;
    @MockBean
    private ApplicationUserService applicationUserService;
    @MockBean
    private PartInspectionService partInspectionService;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    @LocalServerPort
    private int port;

    @Test
    public void testRegisterNewAircraft_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            Mockito
                    .when(this.aircraftSeatsService.generateAircraftSeats(Mockito.eq(1), Mockito.eq(1)))
                    .thenReturn(aircraft.getAircraftSeatsEntityList());
            Mockito
                    .when(
                            this.partsService.getPartEntitiesByPartsIdListAndAircraftType(
                                    Mockito.eq(List.of(1L, 2L, 3L)),
                                    Mockito.eq(AircraftType.PLANE)
                            )
                    )
                    .thenReturn(aircraft.getPartsEntities());
            Mockito
                    .when(this.aircraftsEntityRepository.save(Mockito.eq(aircraft)))
                    .thenReturn(aircraft.setStatus(AircraftStatus.NEEDS_INSPECTION));

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            AircraftRequestDto aircraftRequestDto = new AircraftRequestDto();
            aircraftRequestDto
                    .setAircraftType(AircraftType.PLANE)
                    .setPartIdList(List.of(1L, 2L, 3L))
                    .setNumberOfSeatsInRow(1)
                    .setNumberOfRows(1)
                    .setTitle("aircraft");

            ResponseEntity<AircraftResponseDto> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(aircraftRequestDto, httpHeaders),
                            AircraftResponseDto.class
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            AircraftResponseDto responseDto = response.getBody();
            Assertions.assertEquals(1L, responseDto.getId());
            Assertions.assertEquals(aircraftRequestDto.getTitle(), responseDto.getTitle());
            Assertions.assertEquals(aircraftRequestDto.getAircraftType(), responseDto.getAircraftType());
            Assertions.assertEquals(AircraftStatus.NEEDS_INSPECTION, responseDto.getStatus());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewAircraft_EmptyTitle() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            AircraftRequestDto aircraftRequestDto = new AircraftRequestDto();
            aircraftRequestDto
                    .setAircraftType(AircraftType.PLANE)
                    .setPartIdList(List.of(1L, 2L, 3L))
                    .setNumberOfSeatsInRow(1)
                    .setNumberOfRows(1)
                    .setTitle("");

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(aircraftRequestDto, httpHeaders),
                            ErrorResponse.class
                    );
            Assertions.assertEquals(
                    "Название создаваемого самолета не может быть null или пустым!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewAircraft_NullAircraftType() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            AircraftRequestDto aircraftRequestDto = new AircraftRequestDto();
            aircraftRequestDto
                    .setAircraftType(null)
                    .setPartIdList(List.of(1L, 2L, 3L))
                    .setNumberOfSeatsInRow(1)
                    .setNumberOfRows(1)
                    .setTitle("aircraft");

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(aircraftRequestDto, httpHeaders),
                            ErrorResponse.class
                    );
            Assertions.assertEquals(
                    "Тип создаваемого ссамолета не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftInspection_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.NEEDS_INSPECTION);
            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
            Mockito
                    .when(this.applicationUserService.getEngineerEntityById(
                            TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                    ))
                    .thenReturn(engineer);

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.ON_INSPECTION).setServicedBy(engineer)
                    );
            Mockito
                    .lenient()
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-aircraft-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format(
                            "[%s]",
                            AircraftStatus.ON_INSPECTION)
                    ));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftInspection_StatusChanged() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.of(aircraft));

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-aircraft-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(
                            "Для назначения техосмотра самолет должен быть передан на техосмотр диспетчером!"
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftInspection_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-aircraft-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(response.getBody().getMessage().endsWith("Самолета с ID[1] не найдено!"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRefueling_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.AVAILABLE);
            aircraft.getFlightsEntities().add(new FlightsEntity().setStatus(FlightStatus.DEPARTURE_INITIATED));

            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
            Mockito
                    .when(this.applicationUserService.getEngineerEntityById(
                            TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                    ))
                    .thenReturn(engineer);

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.ON_REFUELING).setServicedBy(engineer)
                    );
            Mockito
                    .lenient()
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign_refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineerId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format("[%s]", AircraftStatus.ON_REFUELING))
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRefueling_FlightsNotAssigned() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.of(aircraft));

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign_refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineerId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith("Данный самолет не был назначен ни на один рейс!")
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRefueling_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign_refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineerId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(response.getBody().getMessage().endsWith("Самолета с ID[1] не найдено!"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRepairs_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.INSPECTED);
            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
            Mockito
                    .when(this.applicationUserService.getEngineerEntityById(
                            TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                    ))
                    .thenReturn(engineer);

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.ON_REPAIRS).setServicedBy(engineer)
                    );
            Mockito
                    .when(this.partInspectionService.getLastAircraftInspectionResult(1L))
                    .thenReturn(PartState.NEEDS_FIXING);
            Mockito
                    .lenient()
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-repairs");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format(
                            "[%s]",
                            AircraftStatus.ON_REPAIRS)
                    ));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRepairs_StatusChanged() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.of(aircraft));
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-repairs");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(
                            "Чтобы отправить самолет на ремонт самолет должен быть осмотрен инженером!"
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignAircraftRepairs_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "CHIEF_ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/assign-aircraft-inspection");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("engineersId", TestCredentialsProvider.ENGINEERS_DEFAULT_ID);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertTrue(response.getBody().getMessage().endsWith("Самолета с ID[1] не найдено!"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testInspectAircraft_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.ON_INSPECTION);

            ((InMemoryUserDetailsManagerAdapter) this.userDetailsService).updateUsersServicedAircraftByUsername(
                    TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME,
                    aircraft
            );
            ApplicationUsersEntity engineer = (ApplicationUsersEntity) this.userDetailsService.loadUserByUsername(
                    TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME
            );
            aircraft.setServicedBy(engineer);

            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenReturn(Optional.of(aircraft));

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.INSPECTED).setServicedBy(null)
                    );
            Mockito
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            List<PartInspectionsRequestDto> requestDtoList = List.of(
                    new PartInspectionsRequestDto()
                            .setAircraftId(aircraft.getId())
                            .setPartState(PartState.CORRECT)
                            .setPartId(1L)
            );

            Mockito
                    .when(this.partInspectionService.registerPartInspections(Mockito.eq(aircraft), Mockito.anyList()))
                    .thenAnswer(
                            invocationOnMock -> List.of(
                                    new PartInspectionsResponseDto()
                                            .setAircraftId(aircraft.getId())
                                            .setInspectionCode(1L)
                                            .setAircraftTitle(aircraft.getTitle())
                                            .setPartState(PartState.CORRECT)
                                            .setPartId(1L)
                            )
                    );

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/inspect");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<PartInspectionsResponseDto>> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            new ParameterizedTypeReference<List<PartInspectionsResponseDto>>() {}
                    );

            List<PartInspectionsResponseDto> result = response.getBody();
            Assertions.assertEquals(1L, result.get(0).getAircraftId());
            Assertions.assertEquals(1L, result.get(0).getInspectionCode());
            Assertions.assertEquals(aircraft.getTitle(), result.get(0).getAircraftTitle());
            Assertions.assertEquals(PartState.CORRECT, result.get(0).getPartState());
            Assertions.assertEquals(1L, result.get(0).getPartId());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testInspectAircraft_AircraftNotFound() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.ON_INSPECTION);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        List<PartInspectionsRequestDto> requestDtoList = List.of(
                new PartInspectionsRequestDto()
        );

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/inspect");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Самолета с ID[1] не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testInspectAircraft_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);

        ((InMemoryUserDetailsManagerAdapter) this.userDetailsService).updateUsersServicedAircraftByUsername(
                TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME,
                aircraft
        );
        ApplicationUsersEntity engineer = (ApplicationUsersEntity) this.userDetailsService.loadUserByUsername(
                TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME
        );
        aircraft.setServicedBy(engineer);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        List<PartInspectionsRequestDto> requestDtoList = List.of(
                new PartInspectionsRequestDto()
        );

        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/inspect");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Для проведения техосмотра самолета он должен быть назначен главным инжененром!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRefuelAircraft_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.ON_REFUELING);

            ((InMemoryUserDetailsManagerAdapter) this.userDetailsService).updateUsersServicedAircraftByUsername(
                    TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME,
                    aircraft
            );
            ApplicationUsersEntity engineer = (ApplicationUsersEntity) this.userDetailsService.loadUserByUsername(
                    TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME
            );
            aircraft.setServicedBy(engineer);

            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.REFUELED).setServicedBy(null)
                    );
            Mockito
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/refuel-aircraft");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format("[%s]", AircraftStatus.REFUELED))
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRefuelAircraft_StatusChangeException() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.NEEDS_INSPECTION);

            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.REFUELED).setServicedBy(null)
                    );
            Mockito
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/refuel-aircraft");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Ошибка! Заправка для самолета с ID[1] еще не была назначена!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRefuelAircraft_WrongEngineer() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.ON_REFUELING);

            aircraft.setServicedBy(new ApplicationUsersEntity());

            Mockito
                    .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                    .thenAnswer(invocationOnMock -> Optional.of(aircraft));

            AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                    new AircraftsStatusChangedMatcher(
                            new AircraftsEntity().setStatus(AircraftStatus.REFUELED).setServicedBy(null)
                    );
            Mockito
                    .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/refuel-aircraft");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Ошибка! Заправка самолета с ID[1] была назначена другому инженеру!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftServiceability_OK() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.INSPECTED);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                new AircraftsStatusChangedMatcher(
                        new AircraftsEntity().setStatus(AircraftStatus.SERVICEABLE).setServicedBy(null)
                );
        Mockito
                .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_ENGINEER"
        );
        try {
            Mockito
                    .when(this.partInspectionService.getLastAircraftInspectionResult(1L))
                    .thenReturn(PartState.CORRECT);

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-serviceability");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format(
                            "[%s]",
                            AircraftStatus.SERVICEABLE)
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftServiceability_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_ENGINEER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-serviceability");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Самолета с ID[1] не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftServiceability_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_ENGINEER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-serviceability");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Чтобы подтвердить исправность самолета самолет должен быть осмотрен инженером!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSendAircraftToRegistrationConfirmation_OK() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                new AircraftsStatusChangedMatcher(
                        new AircraftsEntity().setStatus(AircraftStatus.REGISTRATION_PENDING_CONFIRMATION)
                                .setServicedBy(null)
                );
        Mockito
                .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/send-to-confirmation");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format(
                            "[%s]",
                            AircraftStatus.REGISTRATION_PENDING_CONFIRMATION)
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSendAircraftToRegistrationConfirmation_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/send-to-confirmation");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Самолета с ID[1] не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSendAircraftToRegistrationConfirmation_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/send-to-confirmation");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Чтобы отправить самолет на подверждение регистрации его техосмотр" +
                            " должен быть подтвержден главным инженером!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftRegistration_OK() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.REGISTRATION_PENDING_CONFIRMATION);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                new AircraftsStatusChangedMatcher(
                        new AircraftsEntity().setStatus(AircraftStatus.AVAILABLE)
                                .setServicedBy(null)
                );
        Mockito
                .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-registration");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<StatusChangedResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            StatusChangedResponse.class
                    );

            Assertions.assertTrue(
                    response.getBody().getMessage().endsWith(String.format(
                            "[%s]",
                            AircraftStatus.AVAILABLE)
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftRegistration_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-registration");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Самолета с ID[1] не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmAircraftRegistration_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));
        String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                "CHIEF_DISPATCHER"
        );

        try {
            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/1/confirm-registration");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.PUT,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Для подтверждения регистрации самолета он должен быть направлен" +
                            " главному диспетчеру диспетчером",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllAircrafts_OK() {
        LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new AircraftsEntity()
                                        .setId(1L)
                                        .setTitle("test1")
                                        .setAircraftType(searchedAircraftType)
                                        .setStatus(searchedAircraftStatus)
                                        .setRegisteredAt(requiredRegistrationDate)
                        )
                );
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<AircraftResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<AircraftResponseDto>>() {}
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            List<AircraftResponseDto> aircraftResponseDtoList = response.getBody();
            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(1L, aircraftResponseDtoList.get(0).getId());
            Assertions.assertEquals("test1", aircraftResponseDtoList.get(0).getTitle());
            Assertions.assertEquals(searchedAircraftStatus, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertEquals(requiredRegistrationDate, aircraftResponseDtoList.get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllAircrafts_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenReturn(new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

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
                    "Самолетов по заданным параметрам не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllAircrafts_IncorrectDateFilters() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ENGINEER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", endDate)
                    .queryParam("registeredBefore", startDate);

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
                    "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNewAircrafts_OK() {
        try {
            AuthenticationMockingUtils.mockAuthenticationBeans(
                    this.authentication,
                    this.securityContext,
                    this.applicationUsersFactory.getApplicationUserByRequiredRole("MANAGER")
            );

            LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
            AircraftsEntity aircraft = new AircraftsEntity()
                    .setId(1L)
                    .setTitle("test1")
                    .setAircraftType(searchedAircraftType)
                    .setStatus(AircraftStatus.NEEDS_INSPECTION)
                    .setRegisteredAt(requiredRegistrationDate);

            Mockito
                    .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                    .thenAnswer(invocationOnMock -> List.of(aircraft));

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/new");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<AircraftResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<AircraftResponseDto>>() {}
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            List<AircraftResponseDto> aircraftResponseDtoList = response.getBody();
            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(1L, aircraftResponseDtoList.get(0).getId());
            Assertions.assertEquals("test1", aircraftResponseDtoList.get(0).getTitle());
            Assertions.assertEquals(AircraftStatus.NEEDS_INSPECTION, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertEquals(requiredRegistrationDate, aircraftResponseDtoList.get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNewAircrafts_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenReturn(new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/new");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

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
                    "Самолетов по заданным параметрам не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNewAircrafts_IncorrectDateFilters() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/new");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", endDate)
                    .queryParam("registeredBefore", startDate);

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
                    "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRepairs_OK() {
        try {
            AuthenticationMockingUtils.mockAuthenticationBeans(
                    this.authentication,
                    this.securityContext,
                    this.applicationUsersFactory.getApplicationUserByRequiredRole("MANAGER")
            );

            LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
            AircraftsEntity aircraft = new AircraftsEntity()
                    .setId(1L)
                    .setTitle("test1")
                    .setAircraftType(searchedAircraftType)
                    .setStatus(AircraftStatus.ON_REPAIRS)
                    .setRegisteredAt(requiredRegistrationDate);

            Mockito
                    .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                    .thenAnswer(invocationOnMock -> List.of(aircraft));

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-repairs");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<AircraftResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<AircraftResponseDto>>() {}
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            List<AircraftResponseDto> aircraftResponseDtoList = response.getBody();
            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(1L, aircraftResponseDtoList.get(0).getId());
            Assertions.assertEquals("test1", aircraftResponseDtoList.get(0).getTitle());
            Assertions.assertEquals(AircraftStatus.ON_REPAIRS, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertEquals(requiredRegistrationDate, aircraftResponseDtoList.get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRepairs_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenReturn(new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-repairs");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

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
                    "Самолетов по заданным параметрам не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRepairs_IncorrectDateFilters() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-repairs");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", endDate)
                    .queryParam("registeredBefore", startDate);

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
                    "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRefueling_OK() {
        try {
            AuthenticationMockingUtils.mockAuthenticationBeans(
                    this.authentication,
                    this.securityContext,
                    this.applicationUsersFactory.getApplicationUserByRequiredRole("MANAGER")
            );

            LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
            AircraftsEntity aircraft = new AircraftsEntity()
                    .setId(1L)
                    .setTitle("test1")
                    .setAircraftType(searchedAircraftType)
                    .setStatus(AircraftStatus.ON_REFUELING)
                    .setRegisteredAt(requiredRegistrationDate);

            Mockito
                    .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                    .thenAnswer(invocationOnMock -> List.of(aircraft));

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<AircraftResponseDto>> response =
                    testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<AircraftResponseDto>>() {}
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            List<AircraftResponseDto> aircraftResponseDtoList = response.getBody();
            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(1L, aircraftResponseDtoList.get(0).getId());
            Assertions.assertEquals("test1", aircraftResponseDtoList.get(0).getTitle());
            Assertions.assertEquals(AircraftStatus.ON_REFUELING, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertEquals(requiredRegistrationDate, aircraftResponseDtoList.get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRefueling_IncorrectDateFilters() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("aircraftStatus", searchedAircraftStatus)
                    .queryParam("registeredAfter", endDate)
                    .queryParam("registeredBefore", startDate);

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
                    "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRefueling_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenReturn(new ArrayList<>());
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/for-refueling");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", searchedAircraftType)
                    .queryParam("registeredAfter", startDate)
                    .queryParam("registeredBefore", endDate);

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
                    "Самолетов по заданным параметрам не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftTypes_OK() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "MANAGER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/aircrafts/aircraft-types");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<AircraftTypesResponseDto> response =
                    testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            AircraftTypesResponseDto.class
                    );
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

            AircraftTypesResponseDto aircraftResponseDtoList = response.getBody();
            Assertions.assertEquals(List.of(AircraftType.values()), aircraftResponseDtoList.getAircraftTypes());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private List<PartsEntity> createPartsList(AircraftsEntity aircraftsEntity) {
        List<PartsEntity> partsEntities = List.of(
                new PartsEntity()
                        .setId(1L)
                        .setTitle("part1")
                        .setAircraftType(AircraftType.PLANE)
                        .setRegisteredAt(LocalDateTime.now()),
                new PartsEntity()
                        .setId(2L)
                        .setTitle("part2")
                        .setAircraftType(AircraftType.PLANE)
                        .setRegisteredAt(LocalDateTime.now()),
                new PartsEntity()
                        .setId(3L)
                        .setTitle("part3")
                        .setAircraftType(AircraftType.PLANE)
                        .setRegisteredAt(LocalDateTime.now())
        );

        return partsEntities
                .stream()
                .peek(partsEntity -> partsEntity.getAircraftsEntities().add(aircraftsEntity))
                .collect(Collectors.toList());
    }

    private List<AircraftSeatsEntity> createAircraftSeats(AircraftsEntity aircraftsEntity) {
        return List.of(
                new AircraftSeatsEntity()
                        .setRowNumber(1)
                        .setNumberInRow(1)
                        .setReserved(Boolean.FALSE)
                        .setAircraftsEntity(aircraftsEntity)
        );
    }

    private AircraftsEntity createAircraft() {
        AircraftsEntity aircraft = new AircraftsEntity();
        return aircraft
                .setId(1L)
                .setTitle("aircraft")
                .setPartsEntities(this.createPartsList(aircraft))
                .setAircraftSeatsEntityList(this.createAircraftSeats(aircraft))
                .setAircraftType(searchedAircraftType)
                .setRegisteredAt(LocalDateTime.now());
    }
}