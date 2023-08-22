package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.UserRolesController;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice(basePackageClasses = UserRolesController.class)
public class UserRolesControllerAdvice {

    @ExceptionHandler(value = UserRolesNotFoundException.class)
    public ErrorResponse handleUserRolesNotFoundException(UserRolesNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
