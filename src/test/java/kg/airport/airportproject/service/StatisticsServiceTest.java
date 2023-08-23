package kg.airport.airportproject.service;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.dto.AircraftsRepairsStatisticsResponseDto;
import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.repository.FlightsEntityRepository;
import kg.airport.airportproject.repository.PartInspectionsEntityRepository;
import kg.airport.airportproject.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(value = MockitoExtension.class)
public class StatisticsServiceTest {
    @Mock
    private FlightsEntityRepository flightsEntityRepository;
    @Mock
    private PartInspectionsEntityRepository partInspectionsEntityRepository;

    private StatisticsService statisticsService;

    @BeforeEach
    private void beforeEach() {
        this.statisticsService = new StatisticsServiceImpl(
                this.flightsEntityRepository,
                this.partInspectionsEntityRepository
        );
    }

    @Test
    public void testGetDestinationStatistics_OK() {
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
            List<DestinationStatisticsResponseDto> resultList =
                    this.statisticsService.getDestinationStatistics(
                            RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER,
                            RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER
                    );

            Assertions.assertEquals(2, resultList.size());

            Assertions.assertEquals(distinctDestinations.get(0), resultList.get(0).getDestination());
            Assertions.assertEquals(flightsNumbers.get(0), resultList.get(0).getNumberOfFlights());

            Assertions.assertEquals(distinctDestinations.get(1), resultList.get(1).getDestination());
            Assertions.assertEquals(flightsNumbers.get(1), resultList.get(1).getNumberOfFlights());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetDestinationStatistics_IncorrectDateFilters() {
        List<String> distinctDestinations = List.of("first", "second");
        Mockito
                .when(this.flightsEntityRepository.getDistinctDestinationValues())
                .thenReturn(distinctDestinations);

        Exception exception = Assertions.assertThrows(
                IncorrectDateFiltersException.class,
                () -> this.statisticsService.getDestinationStatistics(
                        RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER,
                        RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER
                )
        );
        Assertions.assertEquals(
                "Дата начального фильтра не может быть позже даты конечного фильра!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetDestinationStatistics_FlightsNotFound() {
        Mockito
                .when(this.flightsEntityRepository.getDistinctDestinationValues())
                .thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                FlightsNotFoundException.class,
                () -> this.statisticsService.getDestinationStatistics(
                        RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER,
                        RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER
                )
        );
        Assertions.assertEquals(
                "В системе не было зарегистрировано ни одного рейса!",
                exception.getMessage()
        );
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
            List<AircraftsRepairsStatisticsResponseDto> resultList =
                    this.statisticsService.getAircraftRepairsStatistics();

            Assertions.assertEquals(2, resultList.size());

            Assertions.assertEquals(foundDistinctTitles.get(0), resultList.get(0).getAircraftTitle());
            Assertions.assertEquals(foundRepairedPartsNumbers.get(0), resultList.get(0).getNumberOfRepairedParts());

            Assertions.assertEquals(foundDistinctTitles.get(1), resultList.get(1).getAircraftTitle());
            Assertions.assertEquals(foundRepairedPartsNumbers.get(1), resultList.get(1).getNumberOfRepairedParts());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftRepairsStatistics_PartInspectionsNotFound() {
        Mockito
                .when(this.partInspectionsEntityRepository.getDistinctServicedAircraftsTitles())
                .thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                PartInspectionsNotFoundException.class,
                () -> this.statisticsService.getAircraftRepairsStatistics()
        );

        Assertions.assertEquals(
                "Самолетов проходивших ремонт не найдено!",
                exception.getMessage()
        );
    }
}