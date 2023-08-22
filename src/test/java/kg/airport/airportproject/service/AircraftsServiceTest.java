package kg.airport.airportproject.service;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mock.matcher.AircraftsStatusChangedMatcher;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.security.TestCredentialsProvider;
import kg.airport.airportproject.mock.AuthenticationMockingUtils;
import kg.airport.airportproject.service.impl.AircraftsServiceImpl;
import kg.airport.airportproject.validator.AircraftsValidator;
import kg.airport.airportproject.validator.impl.AircraftsValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(value = MockitoExtension.class)
public class AircraftsServiceTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final AircraftType searchedAircraftType = AircraftType.PLANE;
    private static final AircraftStatus searchedAircraftStatus = AircraftStatus.SERVICEABLE;
    private static final LocalDateTime startDate = LocalDateTime.parse("2020-02-12T23:40:00", formatter);
    private static final LocalDateTime endDate = LocalDateTime.parse("2021-02-12T23:40:00", formatter);

    @Mock
    private PartsService partsService;
    @Mock
    private AircraftSeatsService aircraftSeatsService;
    @Mock
    private ApplicationUserService applicationUserService;
    @Mock
    private PartInspectionService partInspectionService;
    @Mock
    private AircraftsEntityRepository aircraftsEntityRepository;

    private AircraftsValidator aircraftsValidator;
    private AircraftsService aircraftsService;

    @BeforeEach
    public void beforeEach() {
        this.aircraftsValidator = new AircraftsValidatorImpl();
        this.aircraftsService = new AircraftsServiceImpl(
                this.aircraftSeatsService,
                this.partsService,
                this.applicationUserService,
                this.partInspectionService,
                this.aircraftsEntityRepository,
                this.aircraftsValidator
        );
    }

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

            AircraftRequestDto aircraftRequestDto = new AircraftRequestDto();
            aircraftRequestDto
                    .setAircraftType(AircraftType.PLANE)
                    .setPartIdList(List.of(1L, 2L, 3L))
                    .setNumberOfSeatsInRow(1)
                    .setNumberOfRows(1)
                    .setTitle("aircraft");

            AircraftResponseDto aircraftResponseDto = this.aircraftsService.registerNewAircraft(aircraftRequestDto);

            Assertions.assertEquals(1L, aircraftResponseDto.getId());
            Assertions.assertEquals(aircraftRequestDto.getTitle(), aircraftResponseDto.getTitle());
            Assertions.assertEquals(aircraftRequestDto.getAircraftType(), aircraftResponseDto.getAircraftType());
            Assertions.assertEquals(AircraftStatus.NEEDS_INSPECTION, aircraftResponseDto.getStatus());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewAircraft_NullRequestDto() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.aircraftsService.registerNewAircraft(null)
        );
        Assertions.assertEquals(
                "Регистрируемый самолет не может быть null!",
                exception.getMessage()
        );
    }

    @Test
    public void testRegisterNewAircraft_InvalidTitle() {
        AircraftRequestDto aircraftRequestDto = new AircraftRequestDto();
        aircraftRequestDto
                .setAircraftType(AircraftType.PLANE)
                .setPartIdList(List.of(1L, 2L, 3L))
                .setNumberOfSeatsInRow(1)
                .setNumberOfRows(1)
                .setTitle("");

        Exception exception = Assertions.assertThrows(
                InvalidAircraftTitleException.class,
                () -> this.aircraftsService.registerNewAircraft(aircraftRequestDto)
        );
        Assertions.assertEquals(
                "Название создаваемого самолета не может быть null или пустым!",
                exception.getMessage()
        );
    }

    @Test
    public void testRefuelAircraft_OK() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.ON_REFUELING);
        ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        AuthenticationMockingUtils.mockAuthentication(engineer);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        AircraftsStatusChangedMatcher aircraftsStatusChangedMatcher =
                new AircraftsStatusChangedMatcher(
                        new AircraftsEntity().setStatus(AircraftStatus.REFUELED).setServicedBy(null)
                );
        Mockito
                .when(this.aircraftsEntityRepository.save(Mockito.argThat(aircraftsStatusChangedMatcher)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        try {
            StatusChangedResponse statusChangedResponse = this.aircraftsService.refuelAircraft(1L);
            Assertions.assertTrue(
                    statusChangedResponse.getMessage().endsWith(String.format("[%s]",AircraftStatus.REFUELED))
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRefuelAircraft_StatusChangeException() {
        AircraftsEntity aircraft = this.createAircraft().setStatus(AircraftStatus.NEEDS_INSPECTION);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.refuelAircraft(1L)
        );
        Assertions.assertEquals(
                "Ошибка! Заправка для самолета с ID[1] еще не была назначена!",
                exception.getMessage()
        );
    }

    @Test
    public void testRefuelAircraft_WrongEngineer() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.ON_REFUELING);
        ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        ApplicationUsersEntity anotherEngineer = AuthenticationMockingUtils
                .buildDefaultEngineersEntity()
                .setId(TestCredentialsProvider.ENGINEERS_DEFAULT_ID + 1);
        AuthenticationMockingUtils.mockAuthentication(anotherEngineer);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        Exception exception = Assertions.assertThrows(
                WrongEngineerException.class,
                () -> this.aircraftsService.refuelAircraft(1L)
        );
        Assertions.assertEquals(
                "Ошибка! Заправка самолета с ID[1] была назначена другому инженеру!",
                exception.getMessage()
        );
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

            StatusChangedResponse result = this.aircraftsService.assignAircraftInspection(
                    1L,
                    TestCredentialsProvider.ENGINEERS_DEFAULT_ID
            );

            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format("[%s]", AircraftStatus.ON_INSPECTION))
            );
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

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.assignAircraftInspection(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Для назначения техосмотра самолет должен быть передан на техосмотр диспетчером!",
                exception.getMessage()
        );
    }

    @Test
    public void testAssignAircraftInspection_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.assignAircraftInspection(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
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

            StatusChangedResponse result = this.aircraftsService.assignAircraftRepairs(
                    1L,
                    TestCredentialsProvider.ENGINEERS_DEFAULT_ID
            );

            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format("[%s]", AircraftStatus.ON_REPAIRS))
            );
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

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.assignAircraftRepairs(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Чтобы отправить самолет на ремонт самолет должен быть осмотрен инженером!",
                exception.getMessage()
        );
    }

    @Test
    public void testAssignAircraftRepairs_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.assignAircraftInspection(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
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

            StatusChangedResponse result = this.aircraftsService.assignAircraftRefueling(
                    1L,
                    TestCredentialsProvider.ENGINEERS_DEFAULT_ID
            );

            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format("[%s]", AircraftStatus.ON_REFUELING))
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

        Exception exception = Assertions.assertThrows(
                FlightsNotAssignedException.class,
                () -> this.aircraftsService.assignAircraftRefueling(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Данный самолет не был назначен ни на один рейс!",
                exception.getMessage()
        );
    }

    @Test
    public void testAssignAircraftRefueling_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.assignAircraftRefueling(
                        1L,
                        TestCredentialsProvider.ENGINEERS_DEFAULT_ID
                )
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testInspectAircraft_OK() {
        try {
            AircraftsEntity aircraft = this.createAircraft();
            aircraft.setStatus(AircraftStatus.ON_INSPECTION);
            ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
            engineer.setServicedAircraft(aircraft);
            aircraft.setServicedBy(engineer);

            AuthenticationMockingUtils.mockAuthentication(engineer);

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

            List<PartInspectionsResponseDto> result =
                    this.aircraftsService.inspectAircraft(1L, requestDtoList);
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

        List<PartInspectionsRequestDto> partInspectionsRequestDtoList = List.of(
                new PartInspectionsRequestDto()
        );

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.inspectAircraft(1L, partInspectionsRequestDtoList)
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testInspectAircraft_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.SERVICEABLE);
        ApplicationUsersEntity engineer = AuthenticationMockingUtils.buildDefaultEngineersEntity();
        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        List<PartInspectionsRequestDto> partInspectionsRequestDtoList = List.of(
                new PartInspectionsRequestDto()
        );

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.inspectAircraft(1L, partInspectionsRequestDtoList)
        );
        Assertions.assertEquals(
                "Для проведения техосмотра самолета он должен быть назначен главным инжененром!",
                exception.getMessage()
        );
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
        try {
            Mockito
                    .when(this.partInspectionService.getLastAircraftInspectionResult(1L))
                    .thenReturn(PartState.CORRECT);

            StatusChangedResponse result = this.aircraftsService.confirmAircraftServiceability(1L);
            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format("[%s]", AircraftStatus.SERVICEABLE))
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

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.confirmAircraftServiceability(1L)
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testConfirmAircraftServiceability_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.confirmAircraftServiceability(1L)
        );
        Assertions.assertEquals(
                "Чтобы подтвердить исправность самолета самолет должен быть осмотрен инженером!",
                exception.getMessage()
        );

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
        try {
            StatusChangedResponse result = this.aircraftsService.sendAircraftToRegistrationConfirmation(1L);
            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format(
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

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.sendAircraftToRegistrationConfirmation(1L)
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testSendAircraftToRegistrationConfirmation_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.sendAircraftToRegistrationConfirmation(1L)
        );
        Assertions.assertEquals(
                "Чтобы отправить самолет на подверждение регистрации" +
                        " его техосмотр должен быть подтвержден главным инженером!",
                exception.getMessage()
        );
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
        try {
            StatusChangedResponse result = this.aircraftsService.confirmAircraftRegistration(1L);
            Assertions.assertTrue(
                    result.getMessage().endsWith(String.format(
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

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.confirmAircraftRegistration(1L)
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testConfirmAircraftRegistration_WrongStatus() {
        AircraftsEntity aircraft = this.createAircraft();
        aircraft.setStatus(AircraftStatus.IN_AIR);
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenReturn(Optional.of(aircraft));

        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.aircraftsService.confirmAircraftRegistration(1L)
        );
        Assertions.assertEquals(
                "Для подтверждения регистрации самолета он должен быть направлен" +
                        " главному диспетчеру диспетчером",
                exception.getMessage()
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
                                                TestCredentialsProvider.ENGINEERS_DEFAULT_ID
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
                                                TestCredentialsProvider.ENGINEERS_DEFAULT_ID
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
                                                TestCredentialsProvider.ENGINEERS_DEFAULT_ID
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

    @Test
    public void testFindAircraftsEntityById_OK() {
        AircraftsEntity aircraft = this.createAircraft();
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(Mockito.eq(1L)))
                .thenAnswer(invocationOnMock -> Optional.of(aircraft));
        try {
            AircraftsEntity result = this.aircraftsService.findAircraftsEntityById(1L);
            Assertions.assertEquals(aircraft, result);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testFindAircraftsEntityById_InvalidId() {
        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.aircraftsService.findAircraftsEntityById(0L)
        );
        Assertions.assertEquals(
                "ID самолета не может быть меньше 1!",
                exception.getMessage()
        );
    }

    @Test
    public void testFindAircraftsEntityById_AircraftNotFound() {
        Mockito
                .when(this.aircraftsEntityRepository.getAircraftsEntityById(1L))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                AircraftNotFoundException.class,
                () -> this.aircraftsService.findAircraftsEntityById(1L)
        );
        Assertions.assertEquals(
                "Самолета с ID[1] не найдено!",
                exception.getMessage()
        );
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