package kg.airport.airportproject.dto;

public class ClientFeedbackRequestDto {
    private String feedbackText;
    private Long flightRegistrationId;

    public ClientFeedbackRequestDto() {
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public ClientFeedbackRequestDto setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
        return this;
    }

    public Long getFlightRegistrationId() {
        return flightRegistrationId;
    }

    public ClientFeedbackRequestDto setFlightRegistrationId(Long flightRegistrationId) {
        this.flightRegistrationId = flightRegistrationId;
        return this;
    }
}
