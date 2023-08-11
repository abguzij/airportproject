package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.dto.AircraftResponseDto;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.repository.PartsEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class AircraftsServiceTest {
    @Autowired
    private AircraftsService aircraftsService;
    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;
    @Autowired
    private PartsEntityRepository partsEntityRepository;
    @Test
    public void testRegisterNewAircraft_OK() {
        List<PartsEntity> partsEntities = this.createTestAircraftParts();
        partsEntities = this.partsEntityRepository.saveAll(partsEntities);

        AircraftRequestDto requestDto = new AircraftRequestDto();
        requestDto
                .setTitle("test")
                .setNumberOfRows(2)
                .setAircraftType(AircraftType.PLANE)
                .setNumberOfSeatsInRow(2)
                .setPartIdList(
                        List.of(partsEntities.get(0).getId(), partsEntities.get(2).getId())
                );

        try {
            AircraftResponseDto responseDto = this.aircraftsService.registerNewAircraft(requestDto);

            Assertions.assertEquals(1L , responseDto.getId());
            Assertions.assertEquals(requestDto.getAircraftType() , responseDto.getAircraftType());
            Assertions.assertEquals(AircraftStatus.NEEDS_INSPECTION, responseDto.getStatus());
            Assertions.assertEquals(requestDto.getTitle(), responseDto.getTitle());
            Assertions.assertNotNull(responseDto.getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private List<PartsEntity> createTestAircraftParts() {
        List<PartsEntity> partsEntities = new ArrayList<>();
        partsEntities.add(
                new PartsEntity()
                        .setTitle("test_1")
                        .setAircraftType(AircraftType.PLANE)
                        .setPartType(PartType.TAIL_PART)
        );
        partsEntities.add(
                new PartsEntity()
                        .setTitle("test_2")
                        .setAircraftType(AircraftType.PLANE)
                        .setPartType(PartType.POWER_PLANT)
        );
        partsEntities.add(
                new PartsEntity()
                        .setTitle("test_3")
                        .setAircraftType(AircraftType.PLANE)
                        .setPartType(PartType.WING_PART)
        );
        return this.partsEntityRepository.saveAll(partsEntities);
    }
}