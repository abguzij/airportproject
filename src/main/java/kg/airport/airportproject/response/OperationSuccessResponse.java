package kg.airport.airportproject.response;

import org.springframework.http.HttpStatus;

public class OperationSuccessResponse {
    private HttpStatus httpStatus;
    private String successMessage;

    public OperationSuccessResponse() {
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public OperationSuccessResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public OperationSuccessResponse setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
        return this;
    }
}
