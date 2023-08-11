package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.repository.AircraftSeatsEntityRepository;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class AircraftSeatsServiceTest {
    @Autowired
    private AircraftSeatsService aircraftSeatsService;
    @Autowired
    private AircraftSeatsEntityRepository aircraftSeatsEntityRepository;
    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;

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

    @Test
    public void testGenerateAircraftSeats_OK() {
        List<AircraftSeatsEntity> aircraftSeatsEntities =
                this.aircraftSeatsService.generateAircraftSeats(2, 3);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, aircraftSeatsEntities.get(0).getRowNumber()),
                () -> Assertions.assertEquals(1, aircraftSeatsEntities.get(0).getNumberInRow())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, aircraftSeatsEntities.get(1).getRowNumber()),
                () -> Assertions.assertEquals(2, aircraftSeatsEntities.get(1).getNumberInRow())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, aircraftSeatsEntities.get(2).getRowNumber()),
                () -> Assertions.assertEquals(3, aircraftSeatsEntities.get(2).getNumberInRow())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, aircraftSeatsEntities.get(3).getRowNumber()),
                () -> Assertions.assertEquals(1, aircraftSeatsEntities.get(3).getNumberInRow())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, aircraftSeatsEntities.get(4).getRowNumber()),
                () -> Assertions.assertEquals(2, aircraftSeatsEntities.get(4).getNumberInRow())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, aircraftSeatsEntities.get(5).getRowNumber()),
                () -> Assertions.assertEquals(3, aircraftSeatsEntities.get(5).getNumberInRow())
        );
    }

    @Test
    public void testGetAllAircraftSeats_OK() {
        AircraftsEntity aircraft1 = new AircraftsEntity();
        aircraft1
                .setTitle("test_1")
                .setStatus(AircraftStatus.AVAILABLE)
                .setAircraftType(AircraftType.PLANE);
        aircraft1 = this.aircraftsEntityRepository.save(aircraft1);

        AircraftsEntity aircraft2 = new AircraftsEntity();
        aircraft2
                .setTitle("test_2")
                .setStatus(AircraftStatus.AVAILABLE)
                .setAircraftType(AircraftType.PLANE);
        aircraft2 = this.aircraftsEntityRepository.save(aircraft2);


        List<AircraftSeatsEntity> aircraftSeatsEntities = new ArrayList<>();
        aircraftSeatsEntities.add(
                new AircraftSeatsEntity()
                        .setNumberInRow(1)
                        .setRowNumber(1)
                        .setReserved(Boolean.TRUE)
                        .setAircraftsEntity(aircraft1)
        );
        aircraft1.getAircraftSeatsEntityList().add(aircraftSeatsEntities.get(0));

        aircraftSeatsEntities.add(
                new AircraftSeatsEntity()
                        .setNumberInRow(2)
                        .setRowNumber(1)
                        .setReserved(Boolean.FALSE)
                        .setAircraftsEntity(aircraft1)
        );
        aircraft1.getAircraftSeatsEntityList().add(aircraftSeatsEntities.get(1));

        aircraftSeatsEntities.add(
                new AircraftSeatsEntity()
                        .setNumberInRow(3)
                        .setRowNumber(1)
                        .setReserved(Boolean.FALSE)
                        .setAircraftsEntity(aircraft2)
        );
        aircraft2.getAircraftSeatsEntityList().add(aircraftSeatsEntities.get(2));

        aircraftSeatsEntities = this.aircraftSeatsEntityRepository.saveAll(aircraftSeatsEntities);
        aircraft1 = this.aircraftsEntityRepository.save(aircraft1);
        aircraft2 = this.aircraftsEntityRepository.save(aircraft2);

        try {
            List<AircraftSeatResponseDto> aircraftSeatResponseDtoList =
                    this.aircraftSeatsService.getAllAircraftSeats(aircraft1.getId(), Boolean.FALSE);

            Assertions.assertEquals(1, aircraftSeatResponseDtoList.size());
            Assertions.assertEquals(2, aircraftSeatResponseDtoList.get(0).getNumberInRow());
            Assertions.assertEquals(1, aircraftSeatResponseDtoList.get(0).getRowNumber());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAircraftSeatEntityById_OK() {
        AircraftSeatsEntity aircraftSeatsEntity = new AircraftSeatsEntity();
        aircraftSeatsEntity
                .setNumberInRow(1)
                .setRowNumber(4)
                .setReserved(Boolean.FALSE);
        aircraftSeatsEntity = this.aircraftSeatsEntityRepository.save(aircraftSeatsEntity);

        try {
            AircraftSeatsEntity result =
                    this.aircraftSeatsService.getAircraftSeatEntityById(aircraftSeatsEntity.getId());

            Assertions.assertEquals(aircraftSeatsEntity.getId(), result.getId());
            Assertions.assertEquals(Boolean.FALSE, result.getReserved());
            Assertions.assertEquals(1, result.getNumberInRow());
            Assertions.assertEquals(4, result.getRowNumber());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}