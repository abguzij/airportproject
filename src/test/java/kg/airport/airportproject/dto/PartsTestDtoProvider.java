package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.AircraftsTestEntityProvider;
import kg.airport.airportproject.entity.PartsTestEntityProvider;

import java.util.List;

public class PartsTestDtoProvider {
    public static PartResponseDto getTestPartResponseDto() {
        return new PartResponseDto()
                .setId(PartsTestEntityProvider.TEST_PART_ID)
                .setTitle(PartsTestEntityProvider.TEST_PART_TITLE)
                .setPartType(PartsTestEntityProvider.TEST_PART_TYPE)
                .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE);
    }


    public static PartRequestDto getTestPartRequestDto() {
        return new PartRequestDto()
                .setTitle(PartsTestEntityProvider.TEST_PART_TITLE)
                .setPartType(PartsTestEntityProvider.TEST_PART_TYPE)
                .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE);
    }

    public static List<PartRequestDto> getListOfTestPartRequestDto() {
        return List.of(
                new PartRequestDto()
                        .setTitle(PartsTestEntityProvider.TEST_PART_TITLE)
                        .setPartType(PartsTestEntityProvider.TEST_PART_TYPE)
                        .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE),
                new PartRequestDto()
                        .setTitle(PartsTestEntityProvider.TEST_PART_TITLE)
                        .setPartType(PartsTestEntityProvider.TEST_PART_TYPE)
                        .setAircraftType(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE)
        );
    }
}
