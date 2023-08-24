package kg.airport.airportproject.validator.impl;

import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.validator.UserFlightsValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserFlightsValidatorImpl implements UserFlightsValidator {
    @Override
    public void validateUserFlightsRequestDto(UserFlightRequestDto requestDto) throws InvalidIdException {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемая регистрация на рейс не может быть null!");
        }
        if(Objects.isNull(requestDto.getFlightId()) || requestDto.getFlightId() < 1L) {
            throw new InvalidIdException(
                    "ID рейса, на который регистрируется пользователь не может быть null или меньше 1!"
            );
        }
        if(Objects.isNull(requestDto.getUserId()) || requestDto.getUserId() < 1L) {
            throw new InvalidIdException(
                    "ID пользователя регистрируемого на рейс не может быть null или меньше 1!"
            );
        }
    }

    @Override
    public void validateAircraftSeatId(Long aircraftSeatId) throws InvalidIdException {
        if(Objects.isNull(aircraftSeatId) || aircraftSeatId < 1L) {
            throw new InvalidIdException(
                    "ID места в самолете забронированного клиентом не может быть null или меньше 1!"
            );
        }
    }
}
