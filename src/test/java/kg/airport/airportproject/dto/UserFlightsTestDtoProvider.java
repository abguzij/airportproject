package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.entity.FlightsTestEntityProvider;

public class UserFlightsTestDtoProvider {
    public static UserFlightRequestDto getTestUserFlightRequestDto(Long seatId) {
        return new UserFlightRequestDto()
                .setFlightId(FlightsTestEntityProvider.TEST_FLIGHT_ID)
                .setUserId(ApplicationUsersTestEntityProvider.TEST_CLIENT_USER_ID)
                .setAircraftSeatId(seatId);
    }
}
