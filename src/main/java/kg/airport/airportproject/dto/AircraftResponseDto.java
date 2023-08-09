package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;

import java.time.LocalDateTime;

public class AircraftResponseDto {
    private Long id;
    private String title;
    private AircraftType aircraftType;
    private AircraftStatus status;
    private LocalDateTime registeredAt;

    public AircraftResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public AircraftResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AircraftResponseDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public AircraftResponseDto setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public AircraftResponseDto setStatus(AircraftStatus status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public AircraftResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }
}
