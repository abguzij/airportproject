package kg.airport.airportproject.validator.impl;

import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.InvalidPartStateException;
import kg.airport.airportproject.validator.PartInspectionsValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PartInspectionsValidatorImpl implements PartInspectionsValidator {
    @Override
    public void validatePartInspectionRequestDto(PartInspectionsRequestDto requestDto)
            throws InvalidPartStateException,
            InvalidIdException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемый осмотр детали не может быть null!");
        }
        if(Objects.isNull(requestDto.getPartState())) {
            throw new InvalidPartStateException("Результат осмотра детали не может быть null!");
        }
        if(Objects.isNull(requestDto.getPartId()) || requestDto.getPartId() < 1L) {
            throw new InvalidIdException("ID осмотренной детали не может быть null или меньше 1!");
        }
        if(Objects.isNull(requestDto.getAircraftId()) || requestDto.getAircraftId() < 1L) {
            throw new InvalidIdException("ID осмотренного самолета не может быть null или меньше 1!");
        }

    }
}
