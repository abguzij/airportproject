package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Aircrafts Controller",
        description = "Endpoint'ы для управления статусами, создания и поиска самолетов"
)
public class AircraftsController {
    private final AircraftsService aircraftsService;

    @Autowired
    public AircraftsController(
            AircraftsService aircraftsService
    ) {
        this.aircraftsService = aircraftsService;
    }

    @Operation(
            summary = "Назначение инженера на техосмотр. ",
            description = "Назначение свободного инженера на осмотр самолета. Принимает id самолета и id инженера. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Подтверждение исправности самолета. ",
            description = "Подтверждение исправности самолета по результатам техосмотра. Принимает id самолета. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Проведение технического осмотра самолета. ",
            description = "Проведение технического осмотра самолета. " +
                    "Принимает id самолета и список dto для регистрации сущностей осмотра детали. " +
                    "Необходимые роли: [ENGINEER]"
    )
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
            InvalidIdException,
            InvalidPartStateException
    {
        return this.aircraftsService.inspectAircraft(aircraftId, partInspectionsRequestDtoList);
    }

    @Operation(
            summary = "Назначение ремонта самолета. ",
            description = "Назначение ремонта самолета свободному инженеру. " +
                    "Принимает id самолета и id инженера. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Отправка самолета на подтверждение регистрации. ",
            description = "Отправляет самолет на подтверждение регистрации главным диспетчером. " +
                    "Принимает id самолета. " +
                    "Необходимые роли: [DISPATCHER]"
    )
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

    @Operation(
            summary = "Подтверждение регистрации самолета. ",
            description = "Подтверждает регистрацию самолета. Самолет регистрируется в системе, как доступный. " +
                    "Принимает id самолета. " +
                    "Необходимые роли: [CHIEF_DISPATCHER]"
    )
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

    @Operation(
            summary = "Назначение заправки самолета. ",
            description = "Назначение заправки самолета свободному инженеру. " +
                    "Принимает id самолета. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Выполнение заправки самолета. ",
            description = "Заправка самолета перед стартом рейса. " +
                    "Принимает id самолета. " +
                    "Необходимые роли: [ENGINEER]"
    )
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

    @Operation(
            summary = "Регистрация самолета. ",
            description = "Регистрирует самолет в системе. " +
                    "Принимает dto для регистрации сущности самолета" +
                    "Необходимые роли: [DISPATCHER]"
    )
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

    @Operation(
            summary = "Поиск самолетов. ",
            description = "Поиск по зарегестрированным в системе самолетам. " +
                    "Параметры поиска: тип самолета, статус самолета, фильтр начальной даты, фильтр конечной даты. " +
                    "Необходимые роли: [MANAGER, CHIEF_DISPATCHER, DISPATCHER, CHIEF_ENGINEER, ENGINEER]"
    )
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

    @Operation(
            summary = "Поиск по новым самолетам. ",
            description = "Поиск по новым самолетам. " +
                    "Параметры поиска: тип самолета, фильтр начальной даты, фильтр конечной даты. " +
                    "Необходимые роли: [MANAGER, ENGINEER]"
    )
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

    @Operation(
            summary = "Поиск по самолетам, отправленным на ремонт. ",
            description = "Поиск по самолетам, отправленным на ремонт. " +
                    "Параметры поиска: тип самолета, фильтр начальной даты, фильтр конечной даты. " +
                    "Необходимые роли: [MANAGER, ENGINEER]"
    )
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

    @Operation(
            summary = "Поиск по самолетам, отправленным на заправку. ",
            description = "Поиск по самолетам, отправленным на заправку. " +
                    "Параметры поиска: тип самолета, фильтр начальной даты, фильтр конечной даты. " +
                    "Необходимые роли: [MANAGER, ENGINEER]"
    )
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

    @Operation(
            summary = "Просмотр всех типов самолетов в системе. ",
            description = "Возвращает все зарегистрированные типы самолетов в системе. " +
                    "Необходимые роли: [DISPATCHER, MANAGER, ENGINEER, CHIEF_ENGINEER, CHIEF_DISPATCHER]"
    )
    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER', 'ENGINEER', 'CHIEF_ENGINEER', 'CHIEF_DISPATCHER')")
    @GetMapping(value = "/aircraft-types")
    public AircraftTypesResponseDto getAircraftTypes() {
        return this.aircraftsService.getAllAircraftTypes();
    }
}
