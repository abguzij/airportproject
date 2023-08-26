package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.StatisticsController;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = StatisticsController.class)
public class StatisticsControllerAdvice {
    @ExceptionHandler(value = FlightsNotFoundException.class)
    public ErrorResponse handleFlightsNotFoundException(FlightsNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = PartInspectionsNotFoundException.class)
    public ErrorResponse handlePartInspectionsNotFoundException(PartInspectionsNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
