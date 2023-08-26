package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.ClientsController;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = ClientsController.class)
public class ClientsControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = ApplicationUserNotFoundException.class)
    public ErrorResponse handleApplicationUserNotFoundException(ApplicationUserNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
