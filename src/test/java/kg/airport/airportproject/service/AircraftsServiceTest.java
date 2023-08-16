package kg.airport.airportproject.service;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.dto.AircraftResponseDto;
import kg.airport.airportproject.dto.AircraftTypesResponseDto;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.exception.AircraftNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.security.mock.AuthenticationMockingUtils;
import kg.airport.airportproject.service.impl.AircraftsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(value = MockitoExtension.class)
public class AircraftsServiceTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final AircraftType searchedAircraftType = AircraftType.PLANE;
    private static final AircraftStatus searchedAircraftStatus = AircraftStatus.SERVICEABLE;
    private static final LocalDateTime startDate = LocalDateTime.parse("2020-02-12T23:40:00", formatter);
    private static final LocalDateTime endDate = LocalDateTime.parse("2021-02-12T23:40:00", formatter);

    @Spy
    private PartsService partsService;
    @Spy
    private AircraftSeatsService aircraftSeatsService;
    @Spy
    private ApplicationUserService applicationUserService;

    @Mock
    private PartInspectionService partInspectionService;
    @Mock
    private AircraftsEntityRepository aircraftsEntityRepository;

    private AircraftsService aircraftsService;

    @BeforeEach
    public void beforeEach() {
        this.aircraftsService = new AircraftsServiceImpl(
                this.aircraftSeatsService,
                this.partsService,
                this.applicationUserService,
                this.partInspectionService,
                this.aircraftsEntityRepository
        );
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
                                        .setAircraftType(AircraftType.PLANE)
                                        .setStatus(AircraftStatus.SERVICEABLE)
                                        .setRegisteredAt(requiredRegistrationDate)
                        )
                );
        try {
            List<AircraftResponseDto> aircraftResponseDtoList =
                    this.aircraftsService.getAllAircrafts(
                            searchedAircraftType,
                            searchedAircraftStatus,
                            endDate,
                            startDate
                    );

            Assertions.assertEquals(1L, aircraftResponseDtoList.get(0).getId());
            Assertions.assertEquals("test1", aircraftResponseDtoList.get(0).getTitle());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertEquals(requiredRegistrationDate, aircraftResponseDtoList.get(0).getRegisteredAt());
            Assertions.assertEquals(searchedAircraftStatus, aircraftResponseDtoList.get(0).getStatus());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllAircrafts_AircraftsNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.getAllAircrafts(
                        searchedAircraftType,
                        searchedAircraftStatus,
                        endDate,
                        startDate
                )
        );
        Assertions.assertEquals(
                "Самолетов по заданным параметрам не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAllAircrafts_IncorrectDateFilters() {
        Exception exception = Assertions.assertThrows(
                IncorrectDateFiltersException.class,
                () -> this.aircraftsService.getAllAircrafts(
                        searchedAircraftType,
                        searchedAircraftStatus,
                        startDate,
                        endDate
                )
        );
        Assertions.assertEquals(
                "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetNewAircrafts_OK() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new AircraftsEntity()
                                        .setId(1L)
                                        .setTitle("test1")
                                        .setAircraftType(searchedAircraftType)
                                        .setStatus(AircraftStatus.NEEDS_INSPECTION)
                                        .setRegisteredAt(requiredRegistrationDate)
                                        .setServicedBy(new ApplicationUsersEntity().setId(
                                                UserDetailsConfigurationTest.ENGINEERS_DEFAULT_ID
                                        ))
                        )
                );

        try {
            List<AircraftResponseDto> aircraftResponseDtoList =
                    this.aircraftsService.getNewAircrafts(searchedAircraftType, endDate, startDate);

            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(AircraftStatus.NEEDS_INSPECTION, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertTrue(endDate.isAfter(aircraftResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertTrue(startDate.isBefore(aircraftResponseDtoList.get(0).getRegisteredAt()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNewAircrafts_AircraftNotFound() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.getNewAircrafts(
                        searchedAircraftType,
                        endDate,
                        startDate
                )
        );
        Assertions.assertEquals(
                "Самолетов по заданным параметрам не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetNewAircrafts_IncorrectDateFilters() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();

        Exception exception = Assertions.assertThrows(
                IncorrectDateFiltersException.class,
                () -> this.aircraftsService.getNewAircrafts(
                        searchedAircraftType,
                        startDate,
                        endDate
                )
        );
        Assertions.assertEquals(
                "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAircraftsForRepairs_OK() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new AircraftsEntity()
                                        .setId(1L)
                                        .setTitle("test1")
                                        .setAircraftType(searchedAircraftType)
                                        .setStatus(AircraftStatus.ON_REPAIRS)
                                        .setRegisteredAt(requiredRegistrationDate)
                                        .setServicedBy(new ApplicationUsersEntity().setId(
                                                UserDetailsConfigurationTest.ENGINEERS_DEFAULT_ID
                                        ))
                        )
                );
        try {
            List<AircraftResponseDto> aircraftResponseDtoList =
                    this.aircraftsService.getAircraftsForRepairs(searchedAircraftType, endDate, startDate);

            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(AircraftStatus.ON_REPAIRS, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertTrue(endDate.isAfter(aircraftResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertTrue(startDate.isBefore(aircraftResponseDtoList.get(0).getRegisteredAt()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRepairs_AircraftNotFound() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.getAircraftsForRepairs(
                        searchedAircraftType,
                        endDate,
                        startDate
                )
        );
        Assertions.assertEquals(
                "Самолетов по заданным параметрам не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAircraftsForRepairs_IncorrectDateFilters() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();

        Exception exception = Assertions.assertThrows(
                IncorrectDateFiltersException.class,
                () -> this.aircraftsService.getAircraftsForRepairs(
                        searchedAircraftType,
                        startDate,
                        endDate
                )
        );
        Assertions.assertEquals(
                "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAircraftsForRefueling_OK() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        LocalDateTime requiredRegistrationDate = LocalDateTime.parse("2020-06-15T15:00:03", formatter);
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new AircraftsEntity()
                                        .setId(1L)
                                        .setTitle("test1")
                                        .setAircraftType(searchedAircraftType)
                                        .setStatus(AircraftStatus.ON_REFUELING)
                                        .setRegisteredAt(requiredRegistrationDate)
                                        .setServicedBy(new ApplicationUsersEntity().setId(
                                                UserDetailsConfigurationTest.ENGINEERS_DEFAULT_ID
                                        ))
                        )
                );
        try {
            List<AircraftResponseDto> aircraftResponseDtoList =
                    this.aircraftsService.getAircraftsForRefueling(searchedAircraftType, endDate, startDate);

            Assertions.assertEquals(1, aircraftResponseDtoList.size());
            Assertions.assertEquals(AircraftStatus.ON_REFUELING, aircraftResponseDtoList.get(0).getStatus());
            Assertions.assertEquals(searchedAircraftType, aircraftResponseDtoList.get(0).getAircraftType());
            Assertions.assertTrue(endDate.isAfter(aircraftResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertTrue(startDate.isBefore(aircraftResponseDtoList.get(0).getRegisteredAt()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftsForRefueling_AircraftNotFound() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();
        Mockito
                .when(this.aircraftsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.getAircraftsForRefueling(
                        searchedAircraftType,
                        endDate,
                        startDate
                )
        );
        Assertions.assertEquals(
                "Самолетов по заданным параметрам не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAircraftsForRefueling_IncorrectDateFilters() {
        AuthenticationMockingUtils.mockAuthenticatedEngineer();

        Exception exception = Assertions.assertThrows(
                IncorrectDateFiltersException.class,
                () -> this.aircraftsService.getAircraftsForRefueling(
                        searchedAircraftType,
                        startDate,
                        endDate
                )
        );
        Assertions.assertEquals(
                "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAircraftTypes_OK() {
        try {
            AircraftTypesResponseDto aircraftTypesResponseDto =
                    this.aircraftsService.getAllAircraftTypes();
            Assertions.assertEquals(List.of(AircraftType.values()), aircraftTypesResponseDto.getAircraftTypes());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}