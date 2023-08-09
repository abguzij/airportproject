package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.AircraftsController;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AircraftsController.class)
public class AircraftsControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = AircraftNotFoundException.class)
    public ErrorResponse handleAircraftNotFoundException(AircraftNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = EngineerIsBusyException.class)
    public ErrorResponse handleEngineerIsBusyException(EngineerIsBusyException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = ApplicationUserNotFoundException.class)
    public ErrorResponse handleApplicationUserNotFoundException(ApplicationUserNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = StatusChangeException.class)
    public ErrorResponse handleStatusChangeException(StatusChangeException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidIdException.class)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = IncorrectDateFiltersException.class)
    public ErrorResponse handleIncorrectDataFiltersException(IncorrectDateFiltersException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = PartInspectionsNotFoundException.class)
    public ErrorResponse handlePartInspectionsNotFoundException(PartInspectionsNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = WrongEngineerException.class)
    public ErrorResponse handleWrongEngineerException(WrongEngineerException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = IncompatiblePartException.class)
    public ErrorResponse handleIncompatiblePartException(IncompatiblePartException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = WrongAircraftException.class)
    public ErrorResponse handleWrongAircraftException(WrongAircraftException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
