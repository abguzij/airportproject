package kg.airport.airportproject.validator.impl;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.exception.InvalidAircraftTypeException;
import kg.airport.airportproject.exception.InvalidPartTitleException;
import kg.airport.airportproject.exception.InvalidPartTypeException;
import kg.airport.airportproject.exception.NoSuchPartTypeException;
import kg.airport.airportproject.validator.PartsValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PartsValidatorImpl implements PartsValidator {
    @Override
    public void validatePartRequestDto(PartRequestDto requestDto)
            throws InvalidPartTypeException,
            InvalidAircraftTypeException,
            InvalidPartTitleException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемая деталь не может быть null!");
        }
        if(Objects.isNull(requestDto.getPartType())) {
            throw new InvalidPartTypeException("Тип создаваемой детали не может быть null!");
        }
        if(Objects.isNull(requestDto.getAircraftType())) {
            throw new InvalidAircraftTypeException("Тип создаваемого самолета не может быть null!");
        }
        if(Objects.isNull(requestDto.getTitle()) || requestDto.getTitle().isEmpty()) {
            throw new InvalidPartTitleException("Название создаваемой детали не может быть null или пустым!");
        }
    }
}
