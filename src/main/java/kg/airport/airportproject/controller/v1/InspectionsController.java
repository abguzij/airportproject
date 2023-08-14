package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.dto.PartStatesResponseDto;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.service.PartInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/inspections")
public class InspectionsController {
    private final PartInspectionService partInspectionService;

    @Autowired
    public InspectionsController(
            PartInspectionService partInspectionService
    ) {
        this.partInspectionService = partInspectionService;
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @GetMapping(value = "/history")
    public List<PartInspectionsResponseDto> getPartInspectionsHistory(
            @RequestParam Long aircraftId,
            @RequestParam(required = false) Long inspectionCode
    )
            throws PartInspectionsNotFoundException,
            InvalidIdException
    {
        return this.partInspectionService.getPartInspectionsHistory(aircraftId, inspectionCode);
    }

    @PreAuthorize(value = "hasRole('CHIEF_ENGINEER')")
    @GetMapping(value = "/history/last-inspection")
    public List<PartInspectionsResponseDto> getPartLastAircraftInspection(
            @RequestParam Long aircraftId
    )
            throws PartInspectionsNotFoundException,
            InvalidIdException
    {
        return this.partInspectionService.getLastAircraftInspection(aircraftId);
    }

    @PreAuthorize(value = "hasAnyRole('ENGINEER', 'CHIEF_ENGINEER')")
    @GetMapping(value = "/part-states")
    public PartStatesResponseDto getPartStates() {
        return this.partInspectionService.getAllPartStates();
    }
}
