package kg.airport.airportproject.validator.impl;

import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.exception.InvalidDestinationException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.validator.FlightsValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlightsValidatorImpl implements FlightsValidator {
    @Override
    public void validateFlightRequestDto(FlightRequestDto requestDto)
            throws InvalidDestinationException, InvalidIdException {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создавемый рейс не может быть null!");
        }
        if(Objects.isNull(requestDto.getDestination()) || requestDto.getDestination().isEmpty()) {
            throw new InvalidDestinationException(
                    "Назвнаие пункта назначения создавемого рейса не может быть null или пустым!"
            );
        }
        if(Objects.isNull(requestDto.getAircraftId()) || requestDto.getAircraftId() < 1L) {
            throw new InvalidIdException("ID регистрируемого на рейс самолета не может быть null или пустым!");
        }
    }
}
