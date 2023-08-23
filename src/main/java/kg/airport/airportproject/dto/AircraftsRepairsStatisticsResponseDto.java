package kg.airport.airportproject.dto;

public class AircraftsRepairsStatisticsResponseDto {
    private String aircraftTitle;
    private Integer numberOfRepairedParts;

    public AircraftsRepairsStatisticsResponseDto() {
    }

    public String getAircraftTitle() {
        return aircraftTitle;
    }

    public AircraftsRepairsStatisticsResponseDto setAircraftTitle(String aircraftTitle) {
        this.aircraftTitle = aircraftTitle;
        return this;
    }

    public Integer getNumberOfRepairedParts() {
        return numberOfRepairedParts;
    }

    public AircraftsRepairsStatisticsResponseDto setNumberOfRepairedParts(Integer numberOfRepairedParts) {
        this.numberOfRepairedParts = numberOfRepairedParts;
        return this;
    }
}
