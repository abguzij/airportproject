package kg.airport.airportproject.validator.impl;

import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.exception.InvalidAircraftTitleException;
import kg.airport.airportproject.exception.InvalidAircraftTypeException;
import kg.airport.airportproject.validator.AircraftsValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AircraftsValidatorImpl implements AircraftsValidator {
    @Override
    public void validateAircraftRequestDto(AircraftRequestDto requestDto)
            throws InvalidAircraftTitleException,
            InvalidAircraftTypeException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Регистрируемый самолет не может быть null!");
        }
        if(Objects.isNull(requestDto.getTitle()) || requestDto.getTitle().isEmpty()) {
            throw new InvalidAircraftTitleException("Название создаваемого самолета не может быть null или пустым!");
        }
        if(Objects.isNull(requestDto.getAircraftType())) {
            throw new InvalidAircraftTypeException("Тип создаваемого ссамолета не может быть null!");
        }
    }
}
