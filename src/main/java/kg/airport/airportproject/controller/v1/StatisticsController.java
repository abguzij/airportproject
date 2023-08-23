package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.IncorrectDateFiltersException;
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
    public List<DestinationStatisticsResponseDto> getDestinationsStatistics(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime endDate
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException
    {
        return this.statisticsService.getDestinationStatistics(startDate, endDate);
    }
}
