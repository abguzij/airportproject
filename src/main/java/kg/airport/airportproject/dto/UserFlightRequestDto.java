package kg.airport.airportproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFlightRequestDto {
    private Long aircraftSeatId;
    private Long flightId;
    private Long userId;

    public UserFlightRequestDto() {
    }

    public Long getAircraftSeatId() {
        return aircraftSeatId;
    }

    public UserFlightRequestDto setAircraftSeatId(Long aircraftSeatId) {
        this.aircraftSeatId = aircraftSeatId;
        return this;
    }

    public Long getFlightId() {
        return flightId;
    }

    public UserFlightRequestDto setFlightId(Long flightId) {
        this.flightId = flightId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public UserFlightRequestDto setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
