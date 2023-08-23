package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartsTestDtoProvider;
import kg.airport.airportproject.exception.InvalidPartTitleException;
import kg.airport.airportproject.exception.InvalidPartTypeException;
import kg.airport.airportproject.validator.impl.PartsValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
public class PartsValidatorTest {
    private PartsValidator partsValidator;

    @BeforeEach
    public void beforeEach() {
        this.partsValidator = new PartsValidatorImpl();
    }

    @Test
    public void testValidatePartRequestDto_OK() {
        PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
        try {
            this.partsValidator.validatePartRequestDto(requestDto);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidatePartRequestDto_InvalidPartType() {
        PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
        requestDto.setPartType(null);

        Exception exception = Assertions.assertThrows(
                InvalidPartTypeException.class,
                () -> this.partsValidator.validatePartRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "Тип создаваемой детали не может быть null!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidatePartRequestDto_EmptyPartTitle() {
        PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
        requestDto.setTitle("");

        Exception exception = Assertions.assertThrows(
                InvalidPartTitleException.class,
                () -> this.partsValidator.validatePartRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "Название создаваемой детали не может быть null или пустым!",
                exception.getMessage()
        );
    }
}