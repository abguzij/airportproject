package kg.airport.airportproject.dto;

public class ClientFeedbackRequestDto {
    private String feedbackText;

    public ClientFeedbackRequestDto() {
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public ClientFeedbackRequestDto setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
        return this;
    }
}
