package kg.airport.airportproject.response;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;

    public ErrorResponse() {
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
