package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.repository.AircraftSeatsEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class AircraftSeatsServiceTest {
    @Autowired
    private AircraftSeatsService aircraftSeatsService;
    @Autowired
    private AircraftSeatsEntityRepository aircraftSeatsEntityRepository;

    @Test
    public void testReserveSeat_OK() {
        AircraftSeatsEntity seat = new AircraftSeatsEntity();
        seat
                .setRowNumber(1)
                .setNumberInRow(1)
                .setReserved(Boolean.FALSE);
        this.aircraftSeatsEntityRepository.save(seat);

        try {
            AircraftSeatsEntity result = this.aircraftSeatsService.reserveSeat(seat.getId());

            Assertions.assertEquals(1 , result.getNumberInRow());
            Assertions.assertEquals(1 , result.getRowNumber());
            Assertions.assertTrue(result.getReserved());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelSeatReservation_OK() {
        AircraftSeatsEntity seat = new AircraftSeatsEntity();
        seat
                .setRowNumber(1)
                .setNumberInRow(1)
                .setReserved(Boolean.TRUE);
        this.aircraftSeatsEntityRepository.save(seat);

        try {
            AircraftSeatsEntity result = this.aircraftSeatsService.cancelSeatReservation(seat.getId());

            Assertions.assertEquals(1 , result.getNumberInRow());
            Assertions.assertEquals(1 , result.getRowNumber());
            Assertions.assertFalse(result.getReserved());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}