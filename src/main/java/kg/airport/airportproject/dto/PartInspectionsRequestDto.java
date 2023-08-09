package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.PartState;

public class PartInspectionsRequestDto {
    private PartState partState;
    private Long partId;
    private Long aircraftId;

    public PartInspectionsRequestDto() {
    }

    public PartState getPartState() {
        return partState;
    }

    public PartInspectionsRequestDto setPartState(PartState partState) {
        this.partState = partState;
        return this;
    }

    public Long getPartId() {
        return partId;
    }

    public PartInspectionsRequestDto setPartId(Long partId) {
        this.partId = partId;
        return this;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public PartInspectionsRequestDto setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
        return this;
    }
}
