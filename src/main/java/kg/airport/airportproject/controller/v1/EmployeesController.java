package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.UserPositionResponseDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.service.ApplicationUserService;
import kg.airport.airportproject.service.AuthenticationService;
import kg.airport.airportproject.service.UserPositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/users/employees")
@Tag(
        name = "Employee Controller",
        description = "Endpoint'ы для регистрации, обновления, увольнения и поиска сотрудников"
)
public class EmployeesController {
    private final AuthenticationService authenticationService;
    private final UserPositionsService userPositionsService;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public EmployeesController(
            AuthenticationService authenticationService,
            UserPositionsService userPositionsService,
            ApplicationUserService applicationUserService
    ) {
        this.authenticationService = authenticationService;
        this.userPositionsService = userPositionsService;
        this.applicationUserService = applicationUserService;
    }

    @PreAuthorize(value = "hasRole('MANAGER')")
    @DeleteMapping(value = "/fire")
    public ApplicationUserResponseDto fireEmployee(
            @RequestParam Long employeeId
    )
            throws ApplicationUserNotFoundException,
            InvalidIdException
    {
        return this.applicationUserService.deleteAccountById(employeeId);
    }

    @PreAuthorize(value = "hasRole('MANAGER')")
    @PutMapping(value = "/update")
    public ApplicationUserResponseDto updateEmployee(
            @RequestBody ApplicationUserRequestDto applicationUserRequestDto,
            @RequestParam Long userId
    )
            throws ApplicationUserNotFoundException,
            UserPositionNotExists,
            InvalidIdException
    {
        return this.applicationUserService.updateUsersInformation(applicationUserRequestDto, userId);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping(value = "/register")
    public ApplicationUserResponseDto registerNewEmployee(
            @RequestBody ApplicationUserRequestDto applicationUserRequestDto
    )
            throws UserPositionNotExists,
            UserRolesNotAssignedException, UsernameAlreadyExistsException, InvalidUserInfoException, InvalidCredentialsException, InvalidIdException {
        return this.authenticationService.registerNewEmployee(applicationUserRequestDto);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping(value = "/all")
    public List<ApplicationUserResponseDto> getAllEmployees(
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @RequestParam(required = false) LocalDateTime registeredBefore,
            @RequestParam(required = false) Boolean isFired,
            @RequestParam(required = false) List<String> userPositions
    )
            throws ApplicationUserNotFoundException
    {
        return this.applicationUserService.getAllEmployees(registeredBefore, registeredAfter, isFired, userPositions);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'DISPATCHER')")
    @GetMapping(value = "/crew-members/free")
    public List<ApplicationUserResponseDto> getAllFreeCrewMembers()
            throws ApplicationUserNotFoundException
    {
        return this.applicationUserService.getAllFreeCrewMembers();
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'CHIEF_ENGINEER')")
    @GetMapping(value = "/engineers/free")
    public List<ApplicationUserResponseDto> getAllFreeEngineers()
            throws ApplicationUserNotFoundException
    {
        return this.applicationUserService.getAllFreeEngineers();
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping(value = "/positions")
    public List<UserPositionResponseDto> getAllEmployeePositions() {
        return this.userPositionsService.getAllEmployeePositions();
    }
}
