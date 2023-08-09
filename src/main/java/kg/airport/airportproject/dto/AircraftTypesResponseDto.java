package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.AircraftType;

import java.util.List;

public class AircraftTypesResponseDto {
    private List<AircraftType> aircraftTypes;

    public AircraftTypesResponseDto() {
    }

    public List<AircraftType> getAircraftTypes() {
        return aircraftTypes;
    }

    public AircraftTypesResponseDto setAircraftTypes(List<AircraftType> aircraftTypes) {
        this.aircraftTypes = aircraftTypes;
        return this;
    }
}
