package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.AircraftsRepairsStatisticsResponseDto;
import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@PreAuthorize(value = "hasAnyRole('ADMIN', 'MANAGER')")
@RequestMapping(value = "/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/destinations")
    public List<DestinationStatisticsResponseDto> getDestinationsStatistics()
            throws FlightsNotFoundException
    {
        return this.statisticsService.getDestinationStatistics();
    }

    @GetMapping(value = "/repaired-aircrafts")
    public List<AircraftsRepairsStatisticsResponseDto> getNumberOfRepairedPartsPerAircraft()
            throws PartInspectionsNotFoundException
    {
        return this.statisticsService.getAircraftRepairsStatistics();
    }
}
