package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.FlightsController;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = FlightsController.class)
public class FlightsControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = StatusChangeException.class)
    public ErrorResponse handleStatusChangeException(StatusChangeException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = FlightsNotFoundException.class)
    public ErrorResponse handleFlightsNotFoundException(FlightsNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidIdException.class)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = AircraftNotReadyException.class)
    public ErrorResponse handleAircraftNotReadyException(AircraftNotReadyException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = UnavailableAircraftException.class)
    public ErrorResponse handleUnavailableAircraftException(UnavailableAircraftException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidDestinationException.class)
    public ErrorResponse handleInvalidDestinationException(InvalidDestinationException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = AircraftNotFoundException.class)
    public ErrorResponse handleAircraftNotFoundException(AircraftNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = IncorrectDateFiltersException.class)
    public ErrorResponse handleIncorrectDateFiltersException(IncorrectDateFiltersException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = AircraftSeatNotFoundException.class)
    public ErrorResponse handleAircraftSeatNotFoundException(AircraftSeatNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
