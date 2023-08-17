package kg.airport.airportproject.controller.v1;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.dto.AircraftResponseDto;
import kg.airport.airportproject.dto.AircraftTypesResponseDto;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = {UserDetailsConfigurationTest.class, SecurityConfigurationTest.class})
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