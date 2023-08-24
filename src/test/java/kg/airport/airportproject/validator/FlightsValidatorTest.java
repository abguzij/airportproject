package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightsTestDtoProvider;
import kg.airport.airportproject.exception.InvalidDestinationException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.validator.impl.FlightsValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
public class FlightsValidatorTest {
    private FlightsValidator flightsValidator;

    @BeforeEach
    private void beforeEach() {
        this.flightsValidator = new FlightsValidatorImpl();
    }

    @Test
    public void testValidateFlightRequestDto_OK() {
        try {
            FlightRequestDto requestDto = FlightsTestDtoProvider.getTestFlightRequestDto();
            this.flightsValidator.validateFlightRequestDto(requestDto);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateFlightRequestDto_EmptyDestination() {
        FlightRequestDto requestDto = FlightsTestDtoProvider.getTestFlightRequestDto();
        requestDto.setDestination("");

        Exception exception = Assertions.assertThrows(
                InvalidDestinationException.class,
                () -> this.flightsValidator.validateFlightRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "Назвнаие пункта назначения создавемого рейса не может быть null или пустым!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidateFlightRequestDto_InvalidId() {
        FlightRequestDto requestDto = FlightsTestDtoProvider.getTestFlightRequestDto();
        requestDto.setAircraftId(0L);

        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.flightsValidator.validateFlightRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "ID регистрируемого на рейс самолета не может быть null или пустым!",
                exception.getMessage()
        );
    }
}