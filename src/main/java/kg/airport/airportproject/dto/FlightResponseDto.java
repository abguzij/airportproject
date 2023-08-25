package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.FlightStatus;

import java.time.LocalDateTime;

public class FlightResponseDto {
    private Long id;
    private String destination;
    private FlightStatus status;
    private Integer ticketsLeft;
    private LocalDateTime registeredAt;
    private Long aircraftId;

    public FlightResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public FlightResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public FlightResponseDto setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public FlightResponseDto setStatus(FlightStatus status) {
        this.status = status;
        return this;
    }

    public Integer getTicketsLeft() {
        return ticketsLeft;
    }

    public FlightResponseDto setTicketsLeft(Integer ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public FlightResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public FlightResponseDto setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
        return this;
    }
}
