package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Inspections Controller",
        description = "Endpoint'ы для просмотра истории технического обслуживания"
)
public class InspectionsController {
    private final PartInspectionService partInspectionService;

    @Autowired
    public InspectionsController(
            PartInspectionService partInspectionService
    ) {
        this.partInspectionService = partInspectionService;
    }

    @Operation(
            summary = "Просмотр истории технического обслуживания самолета. ",
            description = "Просмотр истории технического обслуживания самолета. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Просмотр результата последнего технического обслуживания самолета. ",
            description = "Просмотр результата последнего обслуживания самолета. " +
                    "Необходимые роли: [CHIEF_ENGINEER]"
    )
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

    @Operation(
            summary = "Просмотр всезх возможных результатов осмотра деталей. ",
            description = "Возвращает все возможные результаты осмотра деталей. " +
                    "Необходимые роли: [CHIEF_ENGINEER, ENGINEER]"
    )
    @PreAuthorize(value = "hasAnyRole('ENGINEER', 'CHIEF_ENGINEER')")
    @GetMapping(value = "/part-states")
    public PartStatesResponseDto getPartStates() {
        return this.partInspectionService.getAllPartStates();
    }
}
