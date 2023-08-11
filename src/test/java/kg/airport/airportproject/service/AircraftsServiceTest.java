package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.dto.AircraftResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.PartsEntityRepository;
import kg.airport.airportproject.response.StatusChangedResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
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

    // TODO: 11.08.2023 Протестировать методы требующие аутентикации во время тестирования контроллеров

    @Test
    public void testAssignAircraftsInspection_OK() {
        AircraftsEntity aircraft = this.createAircraftsEntity(AircraftStatus.NEEDS_INSPECTION);
        aircraft = this.aircraftsEntityRepository.save(aircraft);

        ApplicationUsersEntity engineer = this.createEngineersEntityByParameters("test", "test");
        engineer = this.applicationUsersEntityRepository.save(engineer);
        try {
            StatusChangedResponse statusChangedResponse =
                    this.aircraftsService.assignAircraftInspection(aircraft.getId(), engineer.getId());
            Assertions.assertTrue(statusChangedResponse.getMessage().endsWith("[ON_INSPECTION]"));

            AircraftsEntity resultEntity = this.aircraftsService.findAircraftsEntityById(aircraft.getId());
            Assertions.assertEquals(engineer.getId(), resultEntity.getServicedBy().getId());
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

    private ApplicationUsersEntity createEngineersEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(6L).setPositionTitle("ENGINEER"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));

        return applicationUsersEntity;
    }

    private AircraftsEntity createAircraftsEntity(AircraftStatus status) {
        return new AircraftsEntity()
                .setAircraftType(AircraftType.PLANE)
                .setStatus(status)
                .setTitle("test");
    }
}