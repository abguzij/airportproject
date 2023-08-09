package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.InspectionsController;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = InspectionsController.class)
public class InspectionsControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = PartInspectionsNotFoundException.class)
    public ErrorResponse handlePartInspectionsNotFoundException(PartInspectionsNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidIdException.class)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
