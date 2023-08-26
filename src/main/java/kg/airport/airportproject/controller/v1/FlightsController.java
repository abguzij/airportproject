package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightResponseDto;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.AircraftSeatsService;
import kg.airport.airportproject.service.FlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/flights")
@Tag(
        name = "Flights Controller",
        description = "Endpoint'ы для управления статусами, создания и просмотра рейсов," +
                " а также необходимы для регистрации на рейс. "
)
public class FlightsController {
    private final FlightsService flightsService;
    private final AircraftSeatsService aircraftSeatsService;

    @Autowired
    public FlightsController(
            FlightsService flightsService,
            AircraftSeatsService aircraftSeatsService
    ) {
        this.flightsService = flightsService;
        this.aircraftSeatsService = aircraftSeatsService;
    }

    @Operation(
            summary = "Назначение раздачи еды. ",
            description = "Назначение раздачи еды. " +
                    "Необходимые роли: [CHIEF_STEWARD]"
    )
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

    @Operation(
            summary = "Начало рейса. ",
            description = "Начало рейса. " +
                    "Необходимые роли: [PILOT]"
    )
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

    @Operation(
            summary = "Подтверждение отправки рейса. ",
            description = "Подтверждение отправки рейса. " +
                    "Необходимые роли: [CHIEF_DISPATCHER]"
    )
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

    @Operation(
            summary = "Инициация старта рейса. ",
            description = "Инициация старта рейса после подготовки. " +
                    "Необходимые роли: [DISPATCHER]"
    )
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

    @Operation(
            summary = "Подтверждение готовности клиентов к рейсу. ",
            description = "Подтверждение готовности клиентов к рейсу. " +
                    "Необходимые роли: [CHIEF_STEWARD]"
    )
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

    @Operation(
            summary = "Назначение инструктажа. ",
            description = "Назначение инструктажа. " +
                    "Необходимые роли: [CHIEF_STEWARD]"
    )
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

    @Operation(
            summary = "Назначение проверки готовности клиентов и проведения инструктажа экипажем. ",
            description = "Назначение проверки готовности клиентов и проведения инструктажа экипажем. " +
                    "Необходимые роли: [DISPATCHER]"
    )
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

    @Operation(
            summary = "Подтверждение заправки самолета. ",
            description = "Главный инженер передает Диспетчеру готовность старта нового рейса. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Инициация отправки рейса. ",
            description = "Инициация отправки рейса.. " +
                    "Необходимые роли: [DISPATCHER]"
    )
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/init-departure-preparations")
    public StatusChangedResponse initiateFlightDeparturePreparations(
            @RequestParam Long flightId
    )
            throws StatusChangeException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.flightsService.initiateFlightDeparturePreparations(flightId);
    }

    @Operation(
            summary = "Подтверждение регистрации рейса. ",
            description = "Подтверждение регистрации рейса. Старт продажи билетов. " +
                    "Необходимые роли: [CHIEF_DISPATCHER]"
    )
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

    @Operation(
            summary = "Запрос посадки. ",
            description = "Запрос посадки. " +
                    "Необходимые роли: [PILOT]"
    )
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

    @Operation(
            summary = "Назначение посадки. ",
            description = "Назначение посадки. " +
                    "Необходимые роли: [DISPATCHER]"
    )
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

    @Operation(
            summary = "Разрешение посадки. ",
            description = "Разрешение посадки. " +
                    "Необходимые роли: [CHIEF_DISPATCHER]"
    )
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

    @Operation(
            summary = "Посадка самолета. ",
            description = "Посадка самолета. Завершение рейса. " +
                    "Необходимые роли: [PILOT]"
    )
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

    @Operation(
            summary = "Регистрация нового рейса. ",
            description = "Регистрация нового рейса. " +
                    "Необходимые роли: [DISPATCHER]"
    )
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public FlightResponseDto registerNewFlight(
            @RequestBody FlightRequestDto flightRequestDto
    )
            throws AircraftNotFoundException,
            UnavailableAircraftException,
            InvalidIdException,
            InvalidDestinationException
    {
        return this.flightsService.registerNewFlight(flightRequestDto);
    }

    @Operation(
            summary = "Просмотр всех рейсов в системе. ",
            description = "Просмотр всех рейсов в системе. " +
                    "Необходимые роли: [MANAGER, CHIEF_DISPATCHER, DISPATCHER, PILOT, CHIEF_STEWARD, CHIEF_ENGINEER]"
    )
    @PreAuthorize(value = "hasAnyRole(" +
            "'MANAGER', 'CHIEF_DISPATCHER', 'DISPATCHER', 'PILOT', 'CHIEF_STEWARD', 'CHIEF_ENGINEER'" +
            ")"
    )
    @GetMapping(value = "/all")
    public List<FlightResponseDto> getAllFlights(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime createdAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime createdBefore,
            @RequestParam(required = false) FlightStatus flightStatus,
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) Long aircraftId
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException
    {
        return this.flightsService.getAllFLights(createdAfter, createdBefore, flightStatus, flightId, aircraftId);
    }

    @Operation(
            summary = "Просмотр рейсов для бронирования билета. ",
            description = "Просмотр рейсов для бронирования билета. " +
                    "Необходимые роли: [MANAGER, CLIENT]"
    )
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

    @Operation(
            summary = "Просмотр мест в самолете для регситрации на рейс. ",
            description = "Просмотр мест в самолете для регситрации на рейс. " +
                    "Необходимые роли: [MANAGER, CLIENT]"
    )
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
        return this.aircraftSeatsService.getAllAircraftSeats(flight.getAircraftsEntity().getId(), isReserved);
    }
}
