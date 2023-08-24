package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.InvalidPartStateException;

public interface PartInspectionsValidator {
    void validatePartInspectionRequestDto(PartInspectionsRequestDto requestDto)
            throws InvalidPartStateException,
            InvalidIdException;
}
