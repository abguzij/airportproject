package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.exception.InvalidAircraftTypeException;
import kg.airport.airportproject.exception.InvalidPartTitleException;
import kg.airport.airportproject.exception.InvalidPartTypeException;

public interface PartsValidator {
    void validatePartRequestDto(PartRequestDto requestDto)
            throws InvalidPartTypeException,
            InvalidAircraftTypeException,
            InvalidPartTitleException;
}
