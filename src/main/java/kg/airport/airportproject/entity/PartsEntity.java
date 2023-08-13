package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "parts")
public class PartsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "part_title")
    private String title;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "aircraft_type")
    private AircraftType aircraftType;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "part_type")
    private PartType partType;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToMany(mappedBy = "partsEntities")
    private List<AircraftsEntity> aircraftsEntities;
    @OneToMany(mappedBy = "partsEntity")
    private List<PartInspectionsEntity> partInspectionsEntities;

    public PartsEntity() {
        this.aircraftsEntities = new ArrayList<>();
        this.partInspectionsEntities = new ArrayList<>();
    }

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PartsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PartsEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public PartsEntity setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public PartType getPartType() {
        return partType;
    }

    public PartsEntity setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public PartsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public List<AircraftsEntity> getAircraftsEntities() {
        return aircraftsEntities;
    }

    public PartsEntity setAircraftsEntities(List<AircraftsEntity> aircraftsEntities) {
        this.aircraftsEntities = aircraftsEntities;
        return this;
    }

    public List<PartInspectionsEntity> getPartInspectionsEntities() {
        return partInspectionsEntities;
    }

    public PartsEntity setPartInspectionsEntities(List<PartInspectionsEntity> partInspectionsEntities) {
        this.partInspectionsEntities = partInspectionsEntities;
        return this;
    }
}