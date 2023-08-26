package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.dto.UserFlightsTestDtoProvider;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.validator.impl.UserFlightsValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFlightsValidatorTest {
    private UserFlightsValidator userFlightsValidator;

    @BeforeEach
    public void beforeEach() {
        this.userFlightsValidator = new UserFlightsValidatorImpl();
    }

    @Test
    public void testValidateUserFlightsRequestDto_OK() {
        try {
            UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(1L);
            this.userFlightsValidator.validateUserFlightsRequestDto(requestDto);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateUserFlightsRequestDto_InvalidFlightId() {
        UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(1L);
        requestDto.setFlightId(null);

        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.userFlightsValidator.validateUserFlightsRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "ID рейса, на который регистрируется пользователь не может быть null или меньше 1!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidateAircraftSeatId_OK() {
        try {
            UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(1L);
            this.userFlightsValidator.validateAircraftSeatId(requestDto.getAircraftSeatId());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateAircraftSeatId_InvalidSeatId() {
        UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(null);
        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.userFlightsValidator.validateAircraftSeatId(requestDto.getAircraftSeatId())
        );
        Assertions.assertEquals(
                "ID места в самолете забронированного клиентом не может быть null или меньше 1!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidateCrewMemberId_OK() {
        UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(1L);
        try {
            this.userFlightsValidator.validateCrewMemberId(requestDto.getUserId());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateCrewMemberId_InvalidUserId() {
        UserFlightRequestDto requestDto = UserFlightsTestDtoProvider.getTestUserFlightRequestDto(1L);
        requestDto.setUserId(null);

        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.userFlightsValidator.validateCrewMemberId(requestDto.getUserId())
        );
        Assertions.assertEquals(
                "ID пользователя регистрируемого на рейс не может быть null или меньше 1!",
                exception.getMessage()
        );
    }
}