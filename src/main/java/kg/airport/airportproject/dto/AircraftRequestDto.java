package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.attributes.AircraftType;

import java.util.List;

public class AircraftRequestDto {
    private String title;
    private AircraftType aircraftType;
    private Integer numberOfSeatsInRow;
    private Integer numberOfRows;
    private List<Long> partIdList;

    public AircraftRequestDto() {
    }

    public String getTitle() {
        return title;
    }

    public AircraftRequestDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public AircraftRequestDto setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
        return this;
    }

    public Integer getNumberOfSeatsInRow() {
        return numberOfSeatsInRow;
    }

    public AircraftRequestDto setNumberOfSeatsInRow(Integer numberOfSeatsInRow) {
        this.numberOfSeatsInRow = numberOfSeatsInRow;
        return this;
    }

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public AircraftRequestDto setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
        return this;
    }

    public List<Long> getPartIdList() {
        return partIdList;
    }

    public AircraftRequestDto setPartIdList(List<Long> partIdList) {
        this.partIdList = partIdList;
        return this;
    }
}
