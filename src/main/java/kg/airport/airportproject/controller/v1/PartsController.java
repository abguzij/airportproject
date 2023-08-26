package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/parts")
@Tag(
        name = "Parts Controller",
        description = "Endpoint'ы для создания и просмотра деталей. "
)
public class PartsController {
    private final PartsService partsService;

    @Autowired
    public PartsController(
            PartsService partsService
    ) {
        this.partsService = partsService;
    }

    @Operation(
            summary = "Регистрация детали самолета в системе. ",
            description = "Регистрация детали самолета в системе. " +
                    "Необходимые роли: [DISPATCHER]"
    )
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public PartResponseDto registerNewPart(
            @RequestBody PartRequestDto requestDto
    )
            throws InvalidAircraftTypeException,
            InvalidPartTypeException,
            InvalidPartTitleException
    {
        return this.partsService.registerNewPart(requestDto);
    }

    @Operation(
            summary = "Регистрация нескольких деталей самолета в системе. ",
            description = "Регистрация нескольких деталей самолета в системе. " +
                    "Необходимые роли: [DISPATCHER]"
    )
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register-all")
    public List<PartResponseDto> registerNewParts(
            @RequestBody List<PartRequestDto> requestDto
    )
            throws InvalidAircraftTypeException,
            InvalidPartTypeException,
            InvalidPartTitleException
    {
        return this.partsService.registerNewParts(requestDto);
    }

    @Operation(
            summary = "Просмотр всех зарегистрированных деталей самолета в системе. ",
            description = "Просмотр всех зарегистрированных деталей самолета в системе. " +
                    "Необходимые роли: [DISPATCHER, MANAGER]"
    )
    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER')")
    @GetMapping(value = "/all")
    public List<PartResponseDto> getAllParts(
            @RequestParam(required = false) AircraftType aircraftType,
            @RequestParam(required = false) PartType partType,
            @RequestParam(required = false) Long aircraftId,
            @RequestParam(required = false) Long partId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter
    )
            throws PartsNotFoundException,
            InvalidIdException
    {
        return this.partsService.getAllParts(
                aircraftType,
                partType,
                aircraftId,
                partId,
                registeredBefore,
                registeredAfter
        );
    }

    @Operation(
            summary = "Просмотр всех доступных типов деталей самолета в системе. ",
            description = "Просмотр всех доступных типов деталей самолета в системе. " +
                    "Необходимые роли: [DISPATCHER, MANAGER, ENGINEER, ENGINEER, CHIEF_DISPATCHER]"
    )
    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER', 'ENGINEER', 'CHIEF_ENGINEER', 'CHIEF_DISPATCHER')")
    @GetMapping(value = "/part-types")
    public PartTypesResponseDto getPartTypes() {
        return this.partsService.getAllPartTypes();
    }
}
