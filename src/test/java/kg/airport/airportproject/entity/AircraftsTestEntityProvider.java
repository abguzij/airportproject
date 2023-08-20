package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;

public class AircraftsTestEntityProvider {
    public static final Long TEST_AIRCRAFT_ID = 1L;
    public static final AircraftType TEST_AIRCRAFT_TYPE = AircraftType.PLANE;
    public static final AircraftStatus TEST_AIRCRAFT_STATUS = AircraftStatus.NEEDS_INSPECTION;
    public static final String TEST_AIRCRAFT_TITLE = "test";

    public static AircraftsEntity getAircraftsTestEntity() {
        return new AircraftsEntity()
                .setId(TEST_AIRCRAFT_ID)
                .setAircraftType(TEST_AIRCRAFT_TYPE)
                .setStatus(TEST_AIRCRAFT_STATUS)
                .setTitle(TEST_AIRCRAFT_TITLE);
    }
}
