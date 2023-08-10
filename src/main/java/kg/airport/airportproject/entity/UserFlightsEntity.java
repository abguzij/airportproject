package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.UserFlightsStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "users_flights")
public class UserFlightsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_status")
    private UserFlightsStatus userStatus;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ApplicationUsersEntity applicationUsersEntity;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "seat_id", referencedColumnName = "id")
    private AircraftSeatsEntity aircraftSeatsEntity;
    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "flight_id", referencedColumnName = "id")
    private FlightsEntity flightsEntity;

    public UserFlightsEntity() {
    }

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UserFlightsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public UserFlightsStatus getUserStatus() {
        return userStatus;
    }

    public UserFlightsEntity setUserStatus(UserFlightsStatus userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public UserFlightsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public ApplicationUsersEntity getApplicationUsersEntity() {
        return applicationUsersEntity;
    }

    public UserFlightsEntity setApplicationUsersEntity(ApplicationUsersEntity applicationUsersEntity) {
        this.applicationUsersEntity = applicationUsersEntity;
        return this;
    }

    public AircraftSeatsEntity getAircraftSeatsEntity() {
        return aircraftSeatsEntity;
    }

    public UserFlightsEntity setAircraftSeatsEntity(AircraftSeatsEntity aircraftSeatsEntity) {
        this.aircraftSeatsEntity = aircraftSeatsEntity;
        return this;
    }

    public FlightsEntity getFlightsEntity() {
        return flightsEntity;
    }

    public UserFlightsEntity setFlightsEntity(FlightsEntity flightsEntity) {
        this.flightsEntity = flightsEntity;
        return this;
    }
}