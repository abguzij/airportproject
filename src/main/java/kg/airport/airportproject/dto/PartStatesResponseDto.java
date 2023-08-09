package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.PartState;

import java.util.List;

public class PartStatesResponseDto {
    private List<PartState> partStates;

    public PartStatesResponseDto() {
    }

    public List<PartState> getPartStates() {
        return partStates;
    }

    public PartStatesResponseDto setPartStates(List<PartState> partStates) {
        this.partStates = partStates;
        return this;
    }
}
