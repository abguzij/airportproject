package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.AircraftsRepairsStatisticsResponseDto;
import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;

import java.util.List;

public interface StatisticsService {
    List<DestinationStatisticsResponseDto> getDestinationStatistics()
            throws FlightsNotFoundException;

    List<AircraftsRepairsStatisticsResponseDto> getAircraftRepairsStatistics()
            throws PartInspectionsNotFoundException;
}
