package kg.airport.airportproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserRolesNotFoundException extends Exception {
    public UserRolesNotFoundException(String message) {
        super(message);
    }
}
