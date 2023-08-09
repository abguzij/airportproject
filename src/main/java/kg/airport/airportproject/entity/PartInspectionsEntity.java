package kg.airport.airportproject.entity;

import kg.airport.airportproject.entity.attributes.PartState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "part_inspections")
public class PartInspectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "part_state")
    private PartState partState;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "inspection_code")
    private Long inspectionCode;

    @ManyToOne
    @JoinColumn(name = "conducted_by", referencedColumnName = "id")
    private ApplicationUsersEntity conductedBy;
    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private PartsEntity partsEntity;
    @ManyToOne
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftsEntity aircraftsEntity;

    public PartInspectionsEntity() {
    }

    public Long getId() {
        return id;
    }

    public PartInspectionsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public PartState getPartState() {
        return partState;
    }

    public PartInspectionsEntity setPartState(PartState partState) {
        this.partState = partState;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public PartInspectionsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public ApplicationUsersEntity getConductedBy() {
        return conductedBy;
    }

    public PartInspectionsEntity setConductedBy(ApplicationUsersEntity conductedBy) {
        this.conductedBy = conductedBy;
        return this;
    }

    public PartsEntity getPartsEntity() {
        return partsEntity;
    }

    public PartInspectionsEntity setPartsEntity(PartsEntity partsEntity) {
        this.partsEntity = partsEntity;
        return this;
    }

    public AircraftsEntity getAircraftsEntity() {
        return aircraftsEntity;
    }

    public PartInspectionsEntity setAircraftsEntity(AircraftsEntity aircraftsEntity) {
        this.aircraftsEntity = aircraftsEntity;
        return this;
    }

    public Long getInspectionCode() {
        return inspectionCode;
    }

    public PartInspectionsEntity setInspectionCode(Long inspectionCode) {
        this.inspectionCode = inspectionCode;
        return this;
    }
}