package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.exception.InvalidDestinationException;
import kg.airport.airportproject.exception.InvalidIdException;

public interface FlightsValidator {
    void validateFlightRequestDto(FlightRequestDto requestDto)
            throws InvalidDestinationException,
            InvalidIdException;
}
