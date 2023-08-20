package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.PartsController;
import kg.airport.airportproject.exception.InvalidAircraftTypeException;
import kg.airport.airportproject.exception.InvalidPartTitleException;
import kg.airport.airportproject.exception.InvalidPartTypeException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = PartsController.class)
public class PartsControllerAdvice {
    @ExceptionHandler(value = InvalidAircraftTypeException.class)
    public ErrorResponse handleInvalidAircraftTypeException(InvalidAircraftTypeException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidPartTypeException.class)
    public ErrorResponse handleInvalidPartTypeException(InvalidPartTypeException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidPartTitleException.class)
    public ErrorResponse handleInvalidPartTitleException(InvalidPartTitleException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
