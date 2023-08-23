package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.dto.AircraftsRepairsStatisticsResponseDto;
import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.repository.FlightsEntityRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class StatisticsControllerTest {
    @MockBean
    private FlightsEntityRepository flightsEntityRepository;
    @MockBean
    private PartInspectionsEntityRepository partInspectionsEntityRepository;

    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testGetDestinationsStatistics_OK() {
        List<String> distinctDestinations = List.of("first", "second");
        Mockito
                .when(this.flightsEntityRepository.getDistinctDestinationValues())
                .thenReturn(distinctDestinations);

        List<Integer> flightsNumbers = List.of(2, 4);
        Mockito
                .when(this.flightsEntityRepository.getDestinationsFlightsNumbersByDateFiltersAndDestinationIn(
                        Mockito.eq(distinctDestinations),
                        Mockito.eq(RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER),
                        Mockito.eq(RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER)
                ))
                .thenReturn(flightsNumbers);
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/statistics/destinations");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("startDate", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER)
                    .queryParam("endDate", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<List<DestinationStatisticsResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<DestinationStatisticsResponseDto>>() {}
                    );

            Assertions.assertEquals(2, response.getBody().size());

            Assertions.assertEquals(distinctDestinations.get(0), response.getBody().get(0).getDestination());
            Assertions.assertEquals(flightsNumbers.get(0), response.getBody().get(0).getNumberOfFlights());

            Assertions.assertEquals(distinctDestinations.get(1), response.getBody().get(1).getDestination());
            Assertions.assertEquals(flightsNumbers.get(1), response.getBody().get(1).getNumberOfFlights());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetDestinationsStatistics_IncorrectDateFilters() {
        try {
            List<String> distinctDestinations = List.of("first", "second");
            Mockito
                    .when(this.flightsEntityRepository.getDistinctDestinationValues())
                    .thenReturn(distinctDestinations);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/statistics/destinations");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("startDate", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER)
                    .queryParam("endDate", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER);

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
                    "Дата начального фильтра не может быть позже даты конечного фильра!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetDestinationsStatistics_FlightsNotFound() {
        try {
            Mockito
                    .when(this.flightsEntityRepository.getDistinctDestinationValues())
                    .thenReturn(new ArrayList<>());

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/statistics/destinations");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("startDate", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER)
                    .queryParam("endDate", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER);

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
                    "В системе не было зарегистрировано ни одного рейса!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftRepairsStatistics_OK() {
        List<String> foundDistinctTitles = List.of("first", "second");
        Mockito
                .when(this.partInspectionsEntityRepository.getDistinctServicedAircraftsTitles())
                .thenReturn(foundDistinctTitles);

        List<Integer> foundRepairedPartsNumbers = List.of(2, 1);
        Mockito
                .when(this.partInspectionsEntityRepository.getNumbersOfRepairedPartsPerAircraft())
                .thenReturn(foundRepairedPartsNumbers);
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/statistics/repaired-aircrafts");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);

            ResponseEntity<List<AircraftsRepairsStatisticsResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<AircraftsRepairsStatisticsResponseDto>>() {}
                    );

            Assertions.assertEquals(2, response.getBody().size());

            Assertions.assertEquals(foundDistinctTitles.get(0), response.getBody().get(0).getAircraftTitle());
            Assertions.assertEquals(
                    foundRepairedPartsNumbers.get(0),
                    response.getBody().get(0).getNumberOfRepairedParts()
            );

            Assertions.assertEquals(foundDistinctTitles.get(1), response.getBody().get(1).getAircraftTitle());
            Assertions.assertEquals(
                    foundRepairedPartsNumbers.get(1),
                    response.getBody().get(1).getNumberOfRepairedParts()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftRepairsStatistics_PartInspectionsNotFound() {
        try {
            Mockito
                    .when(this.partInspectionsEntityRepository.getDistinctServicedAircraftsTitles())
                    .thenReturn(new ArrayList<>());

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "ADMIN"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/statistics/repaired-aircrafts");

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
                    "Самолетов проходивших ремонт не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}