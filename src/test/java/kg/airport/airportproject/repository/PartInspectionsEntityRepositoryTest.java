package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.AircraftsTestEntityProvider;
import kg.airport.airportproject.entity.PartInspectionsEntity;
import kg.airport.airportproject.entity.PartInspectionsTestEntityProvider;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@TestPropertySource(value = "classpath:test.properties")
public class PartInspectionsEntityRepositoryTest {
    private static final Long MAX_INSPECTION_CODE = 5L;

    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;
    @Autowired
    private PartInspectionsEntityRepository partInspectionsEntityRepository;

    @BeforeEach
    public void beforeEach() {
        this.partInspectionsEntityRepository.flush();
    }

    @Test
    public void testGetCurrentMaxInspectionCode_OK() {
        List<PartInspectionsEntity> partInspectionsEntities = new ArrayList<>();
        partInspectionsEntities.add(
                new PartInspectionsEntity()
                        .setInspectionCode(MAX_INSPECTION_CODE)
                        .setRegisteredAt(LocalDateTime.now())
                        .setPartState(PartState.CORRECT)
        );
        partInspectionsEntities.add(
                new PartInspectionsEntity()
                        .setInspectionCode(1L)
                        .setRegisteredAt(LocalDateTime.now())
                        .setPartState(PartState.CORRECT)
        );
        this.partInspectionsEntityRepository.saveAll(partInspectionsEntities);

        Long result = this.partInspectionsEntityRepository.getCurrentMaxInspectionCode();
        Assertions.assertEquals(MAX_INSPECTION_CODE, result);
    }

    @Test
    public void testGetLastAircraftInspectionByAircraftId_OK() {
        AircraftsEntity aircrafts = new AircraftsEntity();
        aircrafts
                .setTitle("test")
                .setRegisteredAt(LocalDateTime.now())
                .setStatus(AircraftStatus.AVAILABLE)
                .setAircraftType(AircraftType.PLANE);
        aircrafts = this.aircraftsEntityRepository.save(aircrafts);

        List<PartInspectionsEntity> partInspectionsEntities = new ArrayList<>();
        partInspectionsEntities.add(
                new PartInspectionsEntity()
                        .setAircraftsEntity(aircrafts)
                        .setInspectionCode(MAX_INSPECTION_CODE)
                        .setRegisteredAt(LocalDateTime.now())
                        .setPartState(PartState.CORRECT)
        );
        partInspectionsEntities.add(
                new PartInspectionsEntity()
                        .setAircraftsEntity(aircrafts)
                        .setInspectionCode(1L)
                        .setRegisteredAt(LocalDateTime.now())
                        .setPartState(PartState.CORRECT)
        );
        this.partInspectionsEntityRepository.saveAll(partInspectionsEntities);

        List<PartInspectionsEntity> partInspectionsEntityList =
                this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(aircrafts.getId());

        Assertions.assertEquals(1, partInspectionsEntityList.size());
        Assertions.assertEquals(MAX_INSPECTION_CODE, partInspectionsEntityList.get(0).getInspectionCode());
        Assertions.assertEquals(aircrafts.getId(), partInspectionsEntityList.get(0).getAircraftsEntity().getId());
    }

    @Test
    public void testGetDistinctServicedAircraftsTitles_OK() {
        AircraftsEntity aircraft1 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "first");
        AircraftsEntity aircraft2 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "second");
        AircraftsEntity aircraft3 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "third");

        aircraft1 = this.aircraftsEntityRepository.save(aircraft1);
        aircraft2 = this.aircraftsEntityRepository.save(aircraft2);
        aircraft3 = this.aircraftsEntityRepository.save(aircraft3);

        PartInspectionsEntity partInspection1 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft1, 1L, PartState.NEEDS_FIXING);
        aircraft1.getPartInspectionsEntities().add(partInspection1);
        PartInspectionsEntity partInspection2 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft1, 2L, PartState.NEEDS_FIXING);
        aircraft1.getPartInspectionsEntities().add(partInspection2);

        PartInspectionsEntity partInspection3 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft2, 3L, PartState.NEEDS_FIXING);
        aircraft2.getPartInspectionsEntities().add(partInspection3);

        PartInspectionsEntity partInspection4 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft3, 4L, PartState.CORRECT);
        aircraft3.getPartInspectionsEntities().add(partInspection4);

        this.partInspectionsEntityRepository.saveAll(List.of(partInspection1, partInspection2, partInspection3));

        List<String> resultList = this.partInspectionsEntityRepository.getDistinctServicedAircraftsTitles();
        Assertions.assertEquals(2, resultList.size());
        Assertions.assertEquals("first", resultList.get(0));
        Assertions.assertEquals("second", resultList.get(1));
    }

    @Test
    public void testGetNumbersOfRepairedParts_OK() {
//        AircraftsEntity aircraft1 = AircraftsTestEntityProvider.getAircraftsTestEntity(1L, "first");
//        AircraftsEntity aircraft2 = AircraftsTestEntityProvider.getAircraftsTestEntity(2L, "second");
//        AircraftsEntity aircraft3 = AircraftsTestEntityProvider.getAircraftsTestEntity(3L, "third");
//
//        PartInspectionsEntity partInspection1 = PartInspectionsTestEntityProvider
//                .getTestPartInspectionEntity(1L, aircraft1, 1L, PartState.NEEDS_FIXING);
//        aircraft1.getPartInspectionsEntities().add(partInspection1);
//        PartInspectionsEntity partInspection2 = PartInspectionsTestEntityProvider
//                .getTestPartInspectionEntity(2L, aircraft1, 2L, PartState.NEEDS_FIXING);
//        aircraft1.getPartInspectionsEntities().add(partInspection2);
//
//        PartInspectionsEntity partInspection3 = PartInspectionsTestEntityProvider
//                .getTestPartInspectionEntity(3L, aircraft2, 3L, PartState.NEEDS_FIXING);
//        aircraft2.getPartInspectionsEntities().add(partInspection2);
//
//        PartInspectionsEntity partInspection4 = PartInspectionsTestEntityProvider
//                .getTestPartInspectionEntity(4L, aircraft3, 4L, PartState.CORRECT);
//        aircraft3.getPartInspectionsEntities().add(partInspection4);
        AircraftsEntity aircraft1 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "first");
        AircraftsEntity aircraft2 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "second");
        AircraftsEntity aircraft3 = AircraftsTestEntityProvider.getAircraftsTestEntity(null, "third");

        aircraft1 = this.aircraftsEntityRepository.save(aircraft1);
        aircraft2 = this.aircraftsEntityRepository.save(aircraft2);
        aircraft3 = this.aircraftsEntityRepository.save(aircraft3);

        PartInspectionsEntity partInspection1 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft1, 1L, PartState.NEEDS_FIXING);
        aircraft1.getPartInspectionsEntities().add(partInspection1);
        PartInspectionsEntity partInspection2 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft1, 2L, PartState.NEEDS_FIXING);
        aircraft1.getPartInspectionsEntities().add(partInspection2);

        PartInspectionsEntity partInspection3 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft2, 3L, PartState.NEEDS_FIXING);
        aircraft2.getPartInspectionsEntities().add(partInspection3);

        PartInspectionsEntity partInspection4 = PartInspectionsTestEntityProvider
                .getTestPartInspectionEntity(null, aircraft3, 4L, PartState.CORRECT);
        aircraft3.getPartInspectionsEntities().add(partInspection4);

        this.partInspectionsEntityRepository.saveAll(List.of(partInspection1, partInspection2, partInspection3));

        List<Integer> result = this.partInspectionsEntityRepository.getNumbersOfRepairedPartsPerAircraft();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(2, result.get(0));
        Assertions.assertEquals(1, result.get(1));
    }
}