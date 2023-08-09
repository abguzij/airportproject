package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;

import java.time.LocalDateTime;

public class PartResponseDto {
    private Long id;
    private String title;
    private AircraftType aircraftType;
    private PartType partType;
    private LocalDateTime registeredAt;

    public PartResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public PartResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PartResponseDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public PartResponseDto setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public PartType getPartType() {
        return partType;
    }

    public PartResponseDto setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public PartResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }
}
