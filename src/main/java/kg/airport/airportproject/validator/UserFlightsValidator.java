package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.exception.InvalidIdException;

public interface UserFlightsValidator {
    void validateUserFlightsRequestDto(UserFlightRequestDto requestDto) throws InvalidIdException;

    void validateAircraftSeatId(Long aircraftSeatId) throws InvalidIdException;

    void validateCrewMemberId(Long userId) throws InvalidIdException;
}
