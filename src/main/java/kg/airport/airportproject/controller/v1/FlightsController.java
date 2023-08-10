package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightResponseDto;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.AircaftSeatsService;
import kg.airport.airportproject.service.FlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/flights")
public class FlightsController {
    private final FlightsService flightsService;
    private final AircaftSeatsService aircaftSeatsService;

    @Autowired
    public FlightsController(
            FlightsService flightsService,
            AircaftSeatsService aircaftSeatsService
    ) {
        this.flightsService = flightsService;
        this.aircaftSeatsService = aircaftSeatsService;
    }

    @PreAuthorize(value = "hasRole('CHIEF_STEWARD')")
    @PutMapping(value = "/assign-food-distribution")
    public StatusChangedResponse assignFoodDistributionDuringFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.assignFoodDistribution(flightId);
    }

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/start-flight")
    public StatusChangedResponse startFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.startFlight(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_DISPATCHER')")
    @PutMapping(value = "/confirm-departure")
    public StatusChangedResponse confirmFlightDeparture(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.confirmDeparture(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/init-departure")
    public StatusChangedResponse initiateFlightDeparture(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.initiateDeparture(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_STEWARD')")
    @PutMapping(value = "/confirm-client-readiness")
    public StatusChangedResponse confirmClientReadinessForFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.confirmClientReadiness(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_STEWARD')")
    @PutMapping(value = "/assign-briefing")
    public StatusChangedResponse assignBriefingForFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.assignBriefing(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/init-crew-preparations")
    public StatusChangedResponse initiateCrewPreparationsForFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.initiateCrewPreparation(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @PutMapping(value = "/confirm-refueling")
    public StatusChangedResponse confirmRefueling(
            @RequestParam Long flightId
    )
            throws AircraftNotReadyException,
            StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.confirmAircraftRefueling(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/init-departure-preparations")
    public StatusChangedResponse initiateFlightDeparturePreparations(
            @RequestParam Long flightId
    ) throws StatusChangeException, FlightsNotFoundException, InvalidIdException {
        return this.flightsService.initiateFlightDeparturePreparations(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_DISPATCHER')")
    @PutMapping(value = "/confirm-registration")
    public StatusChangedResponse confirmFlightRegistration(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.confirmFlightRegistration(flightId);
    }

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/request-landing")
    public StatusChangedResponse requestLanding(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.requestLanding(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/assign-landing")
    public StatusChangedResponse assignLanding(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.assignLanding(flightId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_DISPATCHER')")
    @PutMapping(value = "/confirm-landing")
    public StatusChangedResponse confirmLanding(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.confirmLanding(flightId);
    }

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/end-flight")
    public StatusChangedResponse endFlight(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.endFlight(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public FlightResponseDto registerNewFlight(
            @RequestBody FlightRequestDto flightRequestDto
    )
            throws AircraftNotFoundException,
            UnavailableAircraftException,
            InvalidIdException
    {
        return this.flightsService.registerNewFlight(flightRequestDto);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'CHIEF_DISPATCHER', 'DISPATCHER', 'PILOT')")
    @GetMapping(value = "/all")
    public List<FlightResponseDto> getAllFlights(
            @RequestParam(required = false) LocalDateTime createdAfter,
            @RequestParam(required = false) LocalDateTime createdBefore,
            @RequestParam(required = false) FlightStatus flightStatus
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException
    {
        return this.flightsService.getAllFLights(createdAfter, createdBefore, flightStatus);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'CLIENT')")
    @GetMapping(value = "/selling-tickets")
    public List<FlightResponseDto> getFlightsForTicketReservation(
            @RequestParam(required = false) LocalDateTime createdAfter,
            @RequestParam(required = false) LocalDateTime createdBefore,
            @RequestParam(required = false) String flightDestination
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException
    {
        return this.flightsService.getFlightsForTicketReservation(createdAfter, createdBefore, flightDestination);
    }

    @PreAuthorize(value = "hasRole('CLIENT')")
    @GetMapping(value = "/{flightId}/aircraft-seats")
    public List<AircraftSeatResponseDto> getAllAircraftSeats(
            @PathVariable Long flightId,
            @RequestParam Boolean isReserved
    )
            throws FlightsNotFoundException,
            InvalidIdException,
            AircraftSeatNotFoundException
    {
        FlightsEntity flight = this.flightsService.getFlightEntityByFlightId(flightId);
        return this.aircaftSeatsService.getAllAircraftSeats(flight.getAircraftsEntity().getId(), isReserved);
    }
}
