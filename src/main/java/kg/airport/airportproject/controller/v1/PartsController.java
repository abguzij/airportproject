package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartsNotFoundException;
import kg.airport.airportproject.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/parts")
public class PartsController {
    private final PartsService partsService;

    @Autowired
    public PartsController(
            PartsService partsService
    ) {
        this.partsService = partsService;
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public PartResponseDto registerNewPart(
            @RequestBody PartRequestDto requestDto
    ) {
        return this.partsService.registerNewPart(requestDto);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register-all")
    public List<PartResponseDto> registerNewParts(
            @RequestBody List<PartRequestDto> requestDto
    ) {
        return this.partsService.registerNewParts(requestDto);
    }

    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER')")
    @GetMapping(value = "/all")
    public List<PartResponseDto> getAllParts(
            @RequestParam(required = false) AircraftType aircraftType,
            @RequestParam(required = false) PartType partType,
            @RequestParam(required = false) Long aircraftId,
            @RequestParam(required = false) Long partId,
            @RequestParam(required = false) LocalDateTime registeredBefore,
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

    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER', 'ENGINEER', 'CHIEF_ENGINEER', 'CHIEF_DISPATCHER')")
    @GetMapping(value = "/part-types")
    public PartTypesResponseDto getPartTypes() {
        return this.partsService.getAllPartTypes();
    }
}
