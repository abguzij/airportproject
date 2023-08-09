package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.entity.attributes.PartType;

import java.time.LocalDateTime;

public class PartInspectionsResponseDto {
    private Long id;
    private Long inspectionCode;
    private PartState partState;
    private LocalDateTime registeredAt;
    private Long partId;
    private PartType partType;
    private String partTitle;
    private Long aircraftId;
    private String aircraftTitle;

    public PartInspectionsResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public PartInspectionsResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public PartState getPartState() {
        return partState;
    }

    public PartInspectionsResponseDto setPartState(PartState partState) {
        this.partState = partState;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public PartInspectionsResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public Long getPartId() {
        return partId;
    }

    public PartInspectionsResponseDto setPartId(Long partId) {
        this.partId = partId;
        return this;
    }

    public PartType getPartType() {
        return partType;
    }

    public PartInspectionsResponseDto setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }

    public String getPartTitle() {
        return partTitle;
    }

    public PartInspectionsResponseDto setPartTitle(String partTitle) {
        this.partTitle = partTitle;
        return this;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public PartInspectionsResponseDto setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
        return this;
    }

    public String getAircraftTitle() {
        return aircraftTitle;
    }

    public PartInspectionsResponseDto setAircraftTitle(String aircraftTitle) {
        this.aircraftTitle = aircraftTitle;
        return this;
    }

    public Long getInspectionCode() {
        return inspectionCode;
    }

    public PartInspectionsResponseDto setInspectionCode(Long inspectionCode) {
        this.inspectionCode = inspectionCode;
        return this;
    }
}
