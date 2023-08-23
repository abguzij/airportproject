package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    List<DestinationStatisticsResponseDto> getDestinationStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException;
}
