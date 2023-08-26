package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.dto.UserFlightRegistrationResponseDto;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.service.FlightsService;
import kg.airport.airportproject.service.UserFlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/flights/registrations")
@Tag(
        name = "User Flights Controller",
        description = "Endpoint'ы для управления статусами регистраций на рейс и регистрации на рейс. "
)
public class UsersFlightsController {
    private final FlightsService flightsService;
    private final UserFlightsService userFlightsService;

    @Autowired
    public UsersFlightsController(
            FlightsService flightsService,
            UserFlightsService userFlightsService
    ) {
        this.flightsService = flightsService;
        this.userFlightsService = userFlightsService;
    }

    @Operation(
            summary = "Регистрация членов экипажа на рейс. ",
            description = "Регистрация членов экипажа на рейс. " +
                    "Необходимые роли: [DISPATCHER]"
    )
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/crew/register")
    public List<UserFlightRegistrationResponseDto> registerCrewMembersForFlight(
            @RequestBody List<UserFlightRequestDto> requestDtoList
    )
            throws InvalidUserRoleException,
            WrongFlightException,
            NotEnoughRolesForCrewRegistrationException,
            ApplicationUserNotFoundException,
            FlightsNotFoundException,
            InvalidIdException
    {
        List<UserFlightRegistrationResponseDto> responseDtoList =
                this.userFlightsService.registerEmployeesForFlight(requestDtoList);
        return responseDtoList;
    }

    @Operation(
            summary = "Регистрация клиента на рейс. ",
            description = "Регистрация клиента на рейс. " +
                    "Необходимые роли: [CLIENT]"
    )
    @PreAuthorize(value = "hasRole('CLIENT')")
    @PostMapping(value = "/clients/register")
    public UserFlightRegistrationResponseDto registerClientForFlight(
            @RequestBody UserFlightRequestDto requestDto
    )
            throws WrongFlightException,
            AircraftSeatNotFoundException,
            SeatReservationException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.registerClientForFlight(requestDto);
    }

    @Operation(
            summary = "Отмена регистрации клиента на рейс. ",
            description = "Отмена регистрации клиента на рейс. " +
                    "Необходимые роли: [CLIENT]"
    )
    @PreAuthorize(value = "hasRole('CLIENT')")
    @PutMapping(value = "/clients/cancel-registration")
    public UserFlightRegistrationResponseDto cancelClientRegistrationForFlight(
            @RequestParam Long registrationId
    )
            throws AircraftSeatNotFoundException,
            SeatReservationException,
            TicketCancelingException,
            UserFlightsNotFoundException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.cancelClientRegistration(registrationId);
    }

    @Operation(
            summary = "Проведение инструктажа клиента. ",
            description = "Проведение инструктажа клиента. " +
                    "Необходимые роли: [STEWARD]"
    )
    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients/brief-client")
    public UserFlightRegistrationResponseDto conductClientBriefing(
            @RequestParam Long registrationId
    )
            throws UserFlightsNotFoundException,
            StatusChangeException,
            InvalidIdException, FlightsNotFoundException {
        UserFlightRegistrationResponseDto responseDto = this.userFlightsService.conductClientsBriefing(registrationId);
        if(
                this.userFlightsService.checkIfAllPassengersOfFlightHaveStatus(
                        responseDto.getFlightId(),
                        UserFlightsStatus.CLIENT_BRIEFED
                )
        ) {
            this.flightsService.informThatAllClientsAreBriefed(responseDto.getFlightId());
        }
        return responseDto;
    }

    @Operation(
            summary = "Проверка клиента на готовность к рейсу. ",
            description = "Проверка клиента на готовность к рейсу. " +
                    "Необходимые роли: [STEWARD]"
    )
    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients/check-client")
    public UserFlightRegistrationResponseDto checkClient(
            @RequestParam Long registrationId
    )
            throws UserFlightsNotFoundException,
            StatusChangeException,
            InvalidIdException,
            FlightsNotFoundException
    {
        UserFlightRegistrationResponseDto responseDto = this.userFlightsService.checkClient(registrationId);
        if(
                this.userFlightsService.checkIfAllPassengersOfFlightHaveStatus(
                        responseDto.getFlightId(),
                        UserFlightsStatus.CLIENT_CHECKED
                )
        ) {
            this.flightsService.informThatAllClientsAreChecked(responseDto.getFlightId());
        }
        return responseDto;
    }

    @Operation(
            summary = "Раздача клиенту еды. ",
            description = "Раздача клиенту еды. " +
                    "Необходимые роли: [STEWARD]"
    )
    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients/distribute-clients-food")
    public UserFlightRegistrationResponseDto distributeClientsFood(
            @RequestParam Long registrationId
    )
            throws UserFlightsNotFoundException,
            StatusChangeException,
            InvalidIdException, FlightsNotFoundException {
        UserFlightRegistrationResponseDto responseDto = this.userFlightsService.distributeClientsFood(registrationId);
        if(
                this.userFlightsService.checkIfAllPassengersOfFlightHaveStatus(
                        responseDto.getFlightId(),
                        UserFlightsStatus.CLIENT_FOOD_DISTRIBUTED
                )
        ) {
            this.flightsService.informThatAllClientsFoodIsDistributed(responseDto.getFlightId());
        }
        return responseDto;
    }

    @Operation(
            summary = "Подтверждение членом экипажа готовности к рейсу. ",
            description = "Подтверждение членом экипажа готовности к рейсу. " +
                    "Необходимые роли: [STEWARD, CHIEF_STEWARD, PILOT]"
    )
    @PreAuthorize(value = "hasAnyRole('STEWARD', 'CHIEF_STEWARD', 'PILOT')")
    @PutMapping(value = "/crew-members/confirm-readiness")
    public UserFlightRegistrationResponseDto confirmReadiness()
            throws UserFlightsNotFoundException,
            StatusChangeException,
            InvalidIdException,
            FlightsNotFoundException
    {
        UserFlightRegistrationResponseDto responseDto = this.userFlightsService.confirmReadinessForFlight();
        if(this.userFlightsService.checkIfAllCrewMembersIsReadyForFlight(responseDto.getFlightId())) {
            this.flightsService.informThatAllCrewMembersIsReadyForFlight(responseDto.getFlightId());
        }
        return responseDto;
    }

    @Operation(
            summary = "Просмотр регистраций клиентов на рейс. ",
            description = "Просмотр регистраций клиентов на рейс. " +
                    "Необходимые роли: [MANAGER]"
    )
    @PreAuthorize(value = "hasAnyRole('MANAGER')")
    @GetMapping(value = "/clients/all")
    public List<UserFlightRegistrationResponseDto> getClientsRegistrations(
            @RequestParam Long flightId,
            @RequestParam(required = false) UserFlightsStatus clientStatus
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getAllClientRegistrations(flightId, clientStatus);
    }

    @Operation(
            summary = "Просмотр регистраций членов экипажа на рейс. ",
            description = "Просмотр регистраций членов экипажа на рейс. " +
                    "Необходимые роли: [MANAGER, CHIEF_DISPATCHER, DISPATCHER]"
    )
    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'CHIEF_DISPATCHER', 'MANAGER')")
    @GetMapping(value = "/employees/all")
    public List<UserFlightRegistrationResponseDto> getEmployeesRegistrationsForFlights(
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) UserFlightsStatus status
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getAllEmployeesRegistrations(flightId, status);
    }

    @Operation(
            summary = "Просмотр регистраций клиентов на текущий рейс авторизованного пользователя. ",
            description = "Просмотр регистраций клиентов на текущий рейс авторизованного пользователя. " +
                    "Необходимые роли: [CHIEF_STEWARD, STEWARD, MANAGER]"
    )
    @PreAuthorize(value = "hasAnyRole('CHIEF_STEWARD', 'STEWARD', 'MANAGER')")
    @GetMapping(value = "/clients/current-flight")
    public List<UserFlightRegistrationResponseDto> getClientRegistrationsForCurrentFlight(
            @RequestParam(required = false) UserFlightsStatus clientStatus
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getAllClientRegistrationsForCurrentFLight(clientStatus);
    }

    @Operation(
            summary = "Просмотр текущей регистрации на рейс авторизованного пользователя. ",
            description = "росмотр текущей регистрации на рейс авторизованного пользователя. " +
                    "Необходимые роли: [CLIENT, STEWARD, CHIEF_STEWARD, PILOT]"
    )
    @PreAuthorize(value = "hasAnyRole('CLIENT', 'STEWARD', 'CHIEF_STEWARD', 'PILOT')")
    @GetMapping(value = "/current-flight")
    public UserFlightRegistrationResponseDto getUsersCurrentFlight()
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getCurrentFlight();
    }

    @Operation(
            summary = "Просмотр истории рейсов авторизованного клиента. ",
            description = "Просмотр истории рейсов авторизованного клиента " +
                    "Необходимые роли: [CLIENT]"
    )
    @PreAuthorize(value = "hasRole('CLIENT')")
    @GetMapping(value = "/clients/client-flight-history")
    public List<UserFlightRegistrationResponseDto> getClientsFlightHistory(
            @RequestParam(required = false) UserFlightsStatus status
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getClientsFlightRegistrationHistory(status);
    }
}
