package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.ClientFeedbacksController;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = ClientFeedbacksController.class)
public class ClientFeedbacksControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidFeedbackTextException.class)
    public ErrorResponse handleInvalidFeedbackTextException(InvalidFeedbackTextException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = UserFlightsNotFoundException.class)
    public ErrorResponse handleInvalidFeedbackTextException(UserFlightsNotFoundException e) {
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

    @ExceptionHandler(value = ClientFeedbacksNotFoundException.class)
    public ErrorResponse handleClientFeedbacksNotFoundException(ClientFeedbacksNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = IncorrectDateFiltersException.class)
    public ErrorResponse handleIncorrectDateFiltersException(IncorrectDateFiltersException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
