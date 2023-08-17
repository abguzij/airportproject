package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.exception.InvalidAircraftTitleException;
import kg.airport.airportproject.exception.InvalidAircraftTypeException;

public interface AircraftsValidator {
    void validateAircraftRequestDto(AircraftRequestDto requestDto)
            throws InvalidAircraftTitleException,
            InvalidAircraftTypeException;
}
