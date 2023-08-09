package kg.airport.airportproject.response;

import org.springframework.http.HttpStatus;

public class StatusChangedResponse {
    private HttpStatus httpStatus;
    private String message;

    public StatusChangedResponse() {
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public StatusChangedResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StatusChangedResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
