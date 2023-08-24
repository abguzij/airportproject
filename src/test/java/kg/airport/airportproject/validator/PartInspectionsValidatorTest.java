package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.dto.PartInspectionsTestDtoProvider;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.InvalidPartStateException;
import kg.airport.airportproject.validator.impl.PartInspectionsValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PartInspectionsValidatorTest {
    private PartInspectionsValidator partInspectionsValidator;

    @BeforeEach
    public void beforeEach() {
        this.partInspectionsValidator = new PartInspectionsValidatorImpl();
    }

    @Test
    public void testValidatePartInspectionRequestDto_OK() {
        try {
            PartInspectionsRequestDto requestDto = PartInspectionsTestDtoProvider.getTestPartInspectionsRequestDto();
            this.partInspectionsValidator.validatePartInspectionRequestDto(requestDto);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidatePartInspectionRequestDto_InvalidPartState() {
        PartInspectionsRequestDto requestDto = PartInspectionsTestDtoProvider.getTestPartInspectionsRequestDto();
        requestDto.setPartState(null);

        Exception exception = Assertions.assertThrows(
                InvalidPartStateException.class,
                () -> this.partInspectionsValidator.validatePartInspectionRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "Результат осмотра детали не может быть null!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidatePartInspectionRequestDto_InvalidPartId() {
        PartInspectionsRequestDto requestDto = PartInspectionsTestDtoProvider.getTestPartInspectionsRequestDto();
        requestDto.setPartId(null);

        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.partInspectionsValidator.validatePartInspectionRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "ID осмотренной детали не может быть null или меньше 1!",
                exception.getMessage()
        );
    }
}