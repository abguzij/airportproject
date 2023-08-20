package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.PartType;

import java.util.List;

public class PartsTestEntityProvider {
    public static final Long TEST_PART_ID = 1L;
    public static final Long TEST_SECOND_PART_ID = 2L;
    public static final PartType TEST_PART_TYPE = PartType.WING_PART;
    public static final String TEST_PART_TITLE = "test";

    public static PartsEntity getTestPartsEntity() {
        return new PartsEntity()
                .setId(TEST_PART_ID)
                .setPartType(TEST_PART_TYPE)
                .setTitle(TEST_PART_TITLE)
                .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE);
    }

    public static List<PartsEntity> getListOfTestPartsEntities() {
        return List.of(
                new PartsEntity()
                        .setId(TEST_PART_ID)
                        .setPartType(TEST_PART_TYPE)
                        .setTitle(TEST_PART_TITLE)
                        .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE),
                new PartsEntity()
                        .setId(TEST_SECOND_PART_ID)
                        .setPartType(TEST_PART_TYPE)
                        .setTitle(TEST_PART_TITLE)
                        .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE)
        );
    }
}
