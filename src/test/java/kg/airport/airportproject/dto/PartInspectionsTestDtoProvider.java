package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.AircraftsTestEntityProvider;
import kg.airport.airportproject.entity.PartInspectionsTestEntityProvider;
import kg.airport.airportproject.entity.PartsTestEntityProvider;
import kg.airport.airportproject.entity.attributes.PartState;

public class PartInspectionsTestDtoProvider {
    public static PartInspectionsRequestDto getTestPartInspectionsRequestDto() {
        return new PartInspectionsRequestDto()
                .setPartState(PartInspectionsTestEntityProvider.TEST_PART_STATE)
                .setAircraftId(AircraftsTestEntityProvider.TEST_AIRCRAFT_ID)
                .setPartId(PartsTestEntityProvider.TEST_PART_ID);
    }
}
