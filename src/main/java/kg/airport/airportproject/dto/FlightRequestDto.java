package kg.airport.airportproject.dto;

import java.util.List;

public class FlightRequestDto {
    private String destination;
    private Long aircraftId;
    private List<Long> crewMembersIdList;

    public FlightRequestDto() {
    }

    public String getDestination() {
        return destination;
    }

    public FlightRequestDto setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public FlightRequestDto setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
        return this;
    }

    public List<Long> getCrewMembersIdList() {
        return crewMembersIdList;
    }

    public FlightRequestDto setCrewMembersIdList(List<Long> crewMembersIdList) {
        this.crewMembersIdList = crewMembersIdList;
        return this;
    }
}
