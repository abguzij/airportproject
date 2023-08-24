package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.AircraftsTestEntityProvider;

public class FlightsTestDtoProvider {
    public static final String TEST_DESTINATION = "test";
    public static FlightRequestDto getTestFlightRequestDto() {
        return new FlightRequestDto()
                .setDestination(TEST_DESTINATION)
                .setAircraftId(AircraftsTestEntityProvider.TEST_AIRCRAFT_ID);
    }
}
