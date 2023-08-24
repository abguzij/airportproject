package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.AircraftsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/aircrafts")
public class AircraftsController {
    private final AircraftsService aircraftsService;

    @Autowired
    public AircraftsController(
            AircraftsService aircraftsService
    ) {
        this.aircraftsService = aircraftsService;
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @PutMapping(value = "/{aircraftId}/assign-aircraft-inspection")
    public StatusChangedResponse assignAircraftInspection(
            @PathVariable Long aircraftId,
            @RequestParam Long engineersId
    )
            throws AircraftNotFoundException,
            EngineerIsBusyException,
            ApplicationUserNotFoundException,
            StatusChangeException,
            InvalidIdException
    {
        return this.aircraftsService.assignAircraftInspection(aircraftId, engineersId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @PutMapping(value = "/{aircraftId}/confirm-serviceability")
    public StatusChangedResponse confirmAircraftServiceability(
            @PathVariable Long aircraftId
    )
            throws AircraftNotFoundException,
            PartInspectionsNotFoundException,
            StatusChangeException,
            InvalidIdException
    {
        return this.aircraftsService.confirmAircraftServiceability(aircraftId);
    }

    @PreAuthorize(value = "hasRole('ENGINEER')")
    @PostMapping(value = "/{aircraftId}/inspect")
    public List<PartInspectionsResponseDto> inspectAircraft(
            @PathVariable Long aircraftId,
            @RequestBody List<PartInspectionsRequestDto> partInspectionsRequestDtoList
    )
            throws AircraftNotFoundException,
            AircraftIsNotOnServiceException,
            PartsNotFoundException,
            WrongAircraftException,
            IncompatiblePartException,
            StatusChangeException,
            WrongEngineerException,
            InvalidIdException, InvalidPartStateException {
        return this.aircraftsService.inspectAircraft(aircraftId, partInspectionsRequestDtoList);
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @PutMapping(value = "/{aircraftId}/assign-repairs")
    public StatusChangedResponse assignAircraftRepairs(
            @PathVariable Long aircraftId,
            @RequestParam Long engineersId
    )
            throws EngineerIsBusyException,
            AircraftNotFoundException,
            PartInspectionsNotFoundException,
            StatusChangeException,
            ApplicationUserNotFoundException,
            InvalidIdException
    {
        return this.aircraftsService.assignAircraftRepairs(aircraftId, engineersId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/{aircraftId}/send-to-confirmation")
    public StatusChangedResponse sendAircraftToRegistrationConfirmation(
            @PathVariable Long aircraftId
    )
            throws AircraftNotFoundException,
            StatusChangeException,
            InvalidIdException
    {
        return this.aircraftsService.sendAircraftToRegistrationConfirmation(aircraftId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_DISPATCHER')")
    @PutMapping(value = "/{aircraftId}/confirm-registration")
    public StatusChangedResponse confirmAircraftRegistration(
            @PathVariable Long aircraftId
    )
            throws AircraftNotFoundException,
            StatusChangeException,
            InvalidIdException
    {
        return this.aircraftsService.confirmAircraftRegistration(aircraftId);
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @PutMapping(value = "/{aircraftId}/assign_refueling")
    public StatusChangedResponse assignAircraftRefueling(
            @PathVariable Long aircraftId,
            @RequestParam Long engineerId
    )
            throws AircraftNotFoundException,
            EngineerIsBusyException,
            StatusChangeException,
            ApplicationUserNotFoundException,
            InvalidIdException,
            FlightsNotAssignedException
    {
        return this.aircraftsService.assignAircraftRefueling(aircraftId, engineerId);
    }

    @PreAuthorize(value = "hasRole('ENGINEER')")
    @PutMapping(value = "/{aircraftId}/refuel-aircraft")
    public StatusChangedResponse refuelAircraft(
            @PathVariable Long aircraftId
    )
            throws AircraftNotFoundException,
            StatusChangeException,
            WrongEngineerException,
            InvalidIdException
    {
        return this.aircraftsService.refuelAircraft(aircraftId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public AircraftResponseDto registerNewAircraft(
            @RequestBody AircraftRequestDto requestDto
    )
            throws PartsNotFoundException,
            IncompatiblePartException,
            InvalidIdException,
            InvalidAircraftTypeException,
            InvalidAircraftTitleException
    {
        return this.aircraftsService.registerNewAircraft(requestDto);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'CHIEF_DISPATCHER', 'DISPATCHER', 'CHIEF_ENGINEER', 'ENGINEER')")
    @GetMapping(value = "/all")
    public List<AircraftResponseDto> getAllAircrafts(
            @RequestParam(required = false) AircraftType aircraftType,
            @RequestParam(required = false) AircraftStatus aircraftStatus,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore
    )
            throws AircraftNotFoundException,
            IncorrectDateFiltersException
    {
        return this.aircraftsService.getAllAircrafts(aircraftType, aircraftStatus, registeredBefore, registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/new")
    public List<AircraftResponseDto> getNewAircrafts(
            @RequestParam(required = false) AircraftType aircraftType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore
    )
            throws AircraftNotFoundException,
            IncorrectDateFiltersException
    {
        return this.aircraftsService.getNewAircrafts(aircraftType, registeredBefore, registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/for-repairs")
    public List<AircraftResponseDto> getAircraftsForRepairs(
            @RequestParam(required = false) AircraftType aircraftType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore
    )
            throws AircraftNotFoundException,
            IncorrectDateFiltersException
    {
        return this.aircraftsService.getAircraftsForRepairs(aircraftType, registeredBefore, registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/for-refueling")
    public List<AircraftResponseDto> getAircraftsForRefueling(
            @RequestParam(required = false) AircraftType aircraftType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter
    )
            throws AircraftNotFoundException,
            IncorrectDateFiltersException
    {
        return this.aircraftsService.getAircraftsForRefueling(aircraftType, registeredBefore, registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER', 'ENGINEER', 'CHIEF_ENGINEER', 'CHIEF_DISPATCHER')")
    @GetMapping(value = "/aircraft-types")
    public AircraftTypesResponseDto getAircraftTypes() {
        return this.aircraftsService.getAllAircraftTypes();
    }
}
