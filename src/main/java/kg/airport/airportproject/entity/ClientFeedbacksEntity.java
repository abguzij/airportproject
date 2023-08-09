package kg.airport.airportproject.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "client_feedbacks")
public class ClientFeedbacksEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "feedback_text")
    private String feedbackText;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private ApplicationUsersEntity applicationUsersEntity;
    @ManyToOne
    @JoinColumn(name = "flight_id", referencedColumnName = "id")
    private FlightsEntity flightsEntity;

    public ClientFeedbacksEntity() {
    }
    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ClientFeedbacksEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public ClientFeedbacksEntity setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public ClientFeedbacksEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public ApplicationUsersEntity getApplicationUsersEntity() {
        return applicationUsersEntity;
    }

    public ClientFeedbacksEntity setApplicationUsersEntity(ApplicationUsersEntity applicationUsersEntity) {
        this.applicationUsersEntity = applicationUsersEntity;
        return this;
    }

    public FlightsEntity getFlightsEntity() {
        return flightsEntity;
    }

    public ClientFeedbacksEntity setFlightsEntity(FlightsEntity flightsEntity) {
        this.flightsEntity = flightsEntity;
        return this;
    }
}
