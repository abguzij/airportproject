package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.exception.*;

public interface ApplicationUserValidator {
    void validateUserRequestDto(ApplicationUserRequestDto requestDto)
            throws InvalidCredentialsException,
            InvalidUserInfoException,
            UsernameAlreadyExistsException,
            InvalidIdException;

    void checkUsernameForDuplicates(String username) throws UsernameAlreadyExistsException;

    void validateEmployeesPositionId(Long positionId) throws InvalidUserInfoException, InvalidIdException, InvalidUserPositionException;
}
