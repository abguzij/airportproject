package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.FlightStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "public", name = "flights")
public class FlightsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "destination")
    private String destination;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "flight_status")
    private FlightStatus status;
    @Column(name = "tickets_left")
    private Integer ticketsLeft;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftsEntity aircraftsEntity;
    @OneToMany(mappedBy = "flightsEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<UserFlightsEntity> userFlightsEntities;
    @OneToMany(mappedBy = "flightsEntity")
    private List<ClientFeedbacksEntity> clientFeedbacksEntities;

    public FlightsEntity() {
        this.userFlightsEntities = new ArrayList<>();
        this.clientFeedbacksEntities = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public FlightsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public FlightsEntity setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public FlightsEntity setStatus(FlightStatus status) {
        this.status = status;
        return this;
    }

    public Integer getTicketsLeft() {
        return ticketsLeft;
    }

    public FlightsEntity setTicketsLeft(Integer ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public FlightsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public AircraftsEntity getAircraftsEntity() {
        return aircraftsEntity;
    }

    public FlightsEntity setAircraftsEntity(AircraftsEntity aircraftsEntity) {
        this.aircraftsEntity = aircraftsEntity;
        return this;
    }

    public List<UserFlightsEntity> getUserFlightsEntities() {
        return userFlightsEntities;
    }

    public FlightsEntity setUserFlightsEntities(List<UserFlightsEntity> userFlightsEntities) {
        this.userFlightsEntities = userFlightsEntities;
        return this;
    }

    public List<ClientFeedbacksEntity> getClientFeedbacksEntities() {
        return clientFeedbacksEntities;
    }

    public FlightsEntity setClientFeedbacksEntities(List<ClientFeedbacksEntity> clientFeedbacksEntities) {
        this.clientFeedbacksEntities = clientFeedbacksEntities;
        return this;
    }
}
