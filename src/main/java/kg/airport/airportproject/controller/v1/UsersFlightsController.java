package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.dto.UserFlightRegistrationResponseDto;
import kg.airport.airportproject.entity.UserFlightsEntity;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.FlightsService;
import kg.airport.airportproject.service.UserFlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/flights/registrations")
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

    @PreAuthorize(value = "hasAnyRole('CHIEF_STEWARD', 'STEWARD')")
    @GetMapping(value = "/clients/current-flight")
    public List<UserFlightRegistrationResponseDto> getClientRegistrationsForCurrentFlight(
            @RequestParam(required = false) UserFlightsStatus clientStatus
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.userFlightsService.getAllClientRegistrationsForCurrentFLight(clientStatus);
    }

    @PreAuthorize(value = "hasRole('CLIENT')")
    @GetMapping(value = "/clients/client-current-flight")
    public UserFlightRegistrationResponseDto getClientsCurrentFlight() throws UserFlightsNotFoundException, InvalidIdException {
        return this.userFlightsService.getCurrentFlight();
    }

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
