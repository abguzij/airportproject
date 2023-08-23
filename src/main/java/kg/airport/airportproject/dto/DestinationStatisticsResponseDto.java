package kg.airport.airportproject.dto;

public class DestinationStatisticsResponseDto {
    private String destination;
    private Integer numberOfFlights;

    public DestinationStatisticsResponseDto() {
    }

    public String getDestination() {
        return destination;
    }

    public DestinationStatisticsResponseDto setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public Integer getNumberOfFlights() {
        return numberOfFlights;
    }

    public DestinationStatisticsResponseDto setNumberOfFlights(Integer numberOfFlights) {
        this.numberOfFlights = numberOfFlights;
        return this;
    }
}
