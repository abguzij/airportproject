package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.UsersFlightsController;
import kg.airport.airportproject.exception.NotEnoughRolesForCrewRegistrationException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = UsersFlightsController.class)
public class UsersFlightsControllerAdvice {
    @ExceptionHandler(value = NotEnoughRolesForCrewRegistrationException.class)
    public ErrorResponse handleNotEnoughRolesForCrewRegistrationException(
            NotEnoughRolesForCrewRegistrationException e
    ) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
