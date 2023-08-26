package kg.airport.airportproject.controller.advice;

import kg.airport.airportproject.controller.v1.EmployeesController;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = EmployeesController.class)
public class EmployeeControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = ApplicationUserNotFoundException.class)
    public ErrorResponse handleApplicationUserNotFoundException(ApplicationUserNotFoundException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = UserPositionNotExistsException.class)
    public ErrorResponse handleUserPositionNotExistsException(UserPositionNotExistsException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = UserRolesNotAssignedException.class)
    public ErrorResponse handleUserRolesNotAssignedException(UserRolesNotAssignedException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = UsernameAlreadyExistsException.class)
    public ErrorResponse handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ErrorResponse handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidUserInfoException.class)
    public ErrorResponse handleInvalidUserInfoException(InvalidUserInfoException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidIdException.class)
    public ErrorResponse handleInvalidIdException(InvalidIdException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }

    @ExceptionHandler(value = InvalidUserPositionException.class)
    public ErrorResponse handleInvalidUserPositionException(InvalidUserPositionException e) {
        return new ErrorResponse().setHttpStatus(HttpStatus.BAD_REQUEST).setMessage(e.getMessage());
    }
}
