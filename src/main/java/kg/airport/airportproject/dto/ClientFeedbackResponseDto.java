package kg.airport.airportproject.dto;

import java.time.LocalDateTime;

public class ClientFeedbackResponseDto {
    private Long id;
    private String feedbackText;
    private LocalDateTime registeredAt;
    private Long clientId;
    private Long flightId;

    public ClientFeedbackResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public ClientFeedbackResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public ClientFeedbackResponseDto setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public ClientFeedbackResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public Long getClientId() {
        return clientId;
    }

    public ClientFeedbackResponseDto setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public Long getFlightId() {
        return flightId;
    }

    public ClientFeedbackResponseDto setFlightId(Long flightId) {
        this.flightId = flightId;
        return this;
    }
}
