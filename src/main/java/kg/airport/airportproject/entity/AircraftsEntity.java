package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "aircrafts")
public class AircraftsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "aircraft_title")
    private String title;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "aircraft_type")
    private AircraftType aircraftType;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "aircraft_status")
    private AircraftStatus status;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @OneToOne
    @JoinColumn(name = "serviced_by", referencedColumnName = "id")
    private ApplicationUsersEntity servicedBy;
    @OneToMany(mappedBy = "aircraftsEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<AircraftSeatsEntity> aircraftSeatsEntityList;
    @OneToMany(mappedBy = "aircraftsEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<PartInspectionsEntity> partInspectionsEntities;
    @OneToMany(mappedBy = "aircraftsEntity")
    private List<FlightsEntity> flightsEntities;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
            name = "m2m_aircrafts_parts",
            joinColumns = @JoinColumn(name = "aircraft_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "part_id", referencedColumnName = "id")
    )
    private List<PartsEntity> partsEntities;

    public AircraftsEntity() {
        this.partsEntities = new ArrayList<>();
        this.aircraftSeatsEntityList = new ArrayList<>();
        this.partInspectionsEntities = new ArrayList<>();
        this.flightsEntities = new ArrayList<>();
    }

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AircraftsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AircraftsEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public AircraftsEntity setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public AircraftsEntity setStatus(AircraftStatus status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public AircraftsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public ApplicationUsersEntity getServicedBy() {
        return servicedBy;
    }

    public AircraftsEntity setServicedBy(ApplicationUsersEntity servicedBy) {
        this.servicedBy = servicedBy;
        return this;
    }

    public List<AircraftSeatsEntity> getAircraftSeatsEntityList() {
        return aircraftSeatsEntityList;
    }

    public AircraftsEntity setAircraftSeatsEntityList(List<AircraftSeatsEntity> aircraftSeatsEntityList) {
        this.aircraftSeatsEntityList = aircraftSeatsEntityList;
        return this;
    }

    public List<PartInspectionsEntity> getPartInspectionsEntities() {
        return partInspectionsEntities;
    }

    public AircraftsEntity setPartInspectionsEntities(List<PartInspectionsEntity> partInspectionsEntities) {
        this.partInspectionsEntities = partInspectionsEntities;
        return this;
    }

    public List<FlightsEntity> getFlightsEntities() {
        return flightsEntities;
    }

    public AircraftsEntity setFlightsEntities(List<FlightsEntity> flightsEntities) {
        this.flightsEntities = flightsEntities;
        return this;
    }

    public List<PartsEntity> getPartsEntities() {
        return partsEntities;
    }

    public AircraftsEntity setPartsEntities(List<PartsEntity> partsEntities) {
        this.partsEntities = partsEntities;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AircraftsEntity aircrafts = (AircraftsEntity) o;
        return Objects.equals(title, aircrafts.title) && aircraftType == aircrafts.aircraftType && Objects.equals(aircraftSeatsEntityList, aircrafts.aircraftSeatsEntityList) && Objects.equals(partsEntities, aircrafts.partsEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, aircraftType, aircraftSeatsEntityList, partsEntities);
    }
}