package kg.airport.airportproject.service;

import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.entity.AircraftSeatsTestEntityProvider;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.SeatReservationException;
import kg.airport.airportproject.mock.matcher.AircraftSeatsReservationMatcher;
import kg.airport.airportproject.repository.AircraftSeatsEntityRepository;
import kg.airport.airportproject.service.impl.AircraftSeatsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
public class AircraftSeatsServiceTest {
    private AircraftSeatsService aircraftSeatsService;

    @Mock
    private AircraftSeatsEntityRepository aircraftSeatsEntityRepository;

    @BeforeEach
    public void beforeEach() {
        this.aircraftSeatsService = new AircraftSeatsServiceImpl(this.aircraftSeatsEntityRepository);
    }

    @Test
    public void testReserveSeat_OK() {
        AircraftSeatsEntity aircraftSeatsEntity =
                AircraftSeatsTestEntityProvider.getNotReservedAircraftSeatsTestEntity();
        Mockito
                .when(this.aircraftSeatsEntityRepository.getAircraftSeatsEntityById(
                        Mockito.eq(aircraftSeatsEntity.getId())
                ))
                .thenAnswer(invocationOnMock -> Optional.of(aircraftSeatsEntity));

        ArgumentMatcher<AircraftSeatsEntity> matcher = new AircraftSeatsReservationMatcher(
                AircraftSeatsTestEntityProvider.getReservedAircraftSeatsTestEntity()
        );
        Mockito
                .when(this.aircraftSeatsEntityRepository.save(Mockito.argThat(matcher)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        try {
            AircraftSeatsEntity result =
                    this.aircraftSeatsService.reserveSeat(aircraftSeatsEntity.getId());

            Assertions.assertEquals(AircraftSeatsTestEntityProvider.TEST_SEAT_RESERVED_VALUE, result.getReserved());
            Assertions.assertEquals(aircraftSeatsEntity.getId(), result.getId());
            Assertions.assertEquals(aircraftSeatsEntity.getRowNumber(), result.getRowNumber());
            Assertions.assertEquals(aircraftSeatsEntity.getNumberInRow(), result.getNumberInRow());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testReserveSeat_InvalidId() {
        Assertions.assertThrowsExactly(
                InvalidIdException.class,
                () -> this.aircraftSeatsService.reserveSeat(0L),
                "ID места в самолете не может быть меньше 1!"
        );
    }

    @Test
    public void testReserveSeat_SeatAlreadyReservedException() {
        AircraftSeatsEntity aircraftSeatsEntity =
                AircraftSeatsTestEntityProvider.getReservedAircraftSeatsTestEntity();
        Mockito
                .when(this.aircraftSeatsEntityRepository.getAircraftSeatsEntityById(
                        Mockito.eq(aircraftSeatsEntity.getId())
                ))
                .thenAnswer(invocationOnMock -> Optional.of(aircraftSeatsEntity));

        Assertions.assertThrowsExactly(
                SeatReservationException.class,
                () -> this.aircraftSeatsService.reserveSeat(aircraftSeatsEntity.getId()),
                String.format("Ошибка! Место с ID [%d] уже забронировано!", aircraftSeatsEntity.getId())
        );
    }
}