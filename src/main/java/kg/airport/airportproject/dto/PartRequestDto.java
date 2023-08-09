package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;

public class PartRequestDto {
    private String title;
    private AircraftType aircraftType;
    private PartType partType;

    public PartRequestDto() {
    }

    public String getTitle() {
        return title;
    }

    public PartRequestDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public PartRequestDto setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public PartType getPartType() {
        return partType;
    }

    public PartRequestDto setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }
}
