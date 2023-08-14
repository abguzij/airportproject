package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartInspectionsEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(value = "classpath:test.properties")
public class PartInspectionsEntityRepositoryTest {
    private static final Long MAX_INSPECTION_CODE = 5L;

    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;
    @Autowired
    private PartInspectionsEntityRepository partInspectionsEntityRepository;

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
}