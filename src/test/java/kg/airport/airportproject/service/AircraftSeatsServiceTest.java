package kg.airport.airportproject.service;

import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.repository.AircraftSeatsEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
            AircraftSeatsEntity result = this.aircraftSeatsService.reserveSeat(1L);

            Assertions.assertEquals(1 , result.getNumberInRow());
            Assertions.assertEquals(1 , result.getRowNumber());
            Assertions.assertTrue(result.getReserved());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}