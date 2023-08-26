package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/roles")
@PreAuthorize(value = "hasRole('ADMIN')")
@Tag(
        name = "User Roles Controller",
        description = "Endpoint'ы для управления ролями пользователей системы. "
)
public class UserRolesController {

    private final UserRolesService userRolesService;

    @Autowired
    public UserRolesController(UserRolesService userRolesService) {
        this.userRolesService = userRolesService;
    }


    @Operation(
            summary = "Добавление ролей пользователя конкретному пользователю. ",
            description = "Добавление ролей пользователя конкретному пользователю. " +
                    "Необходимые роли: [ADMIN]"
    )
    @PutMapping(value = "/update")
    public List<UserRoleResponseDto> updateUserRoles(
            @RequestParam Long userId,
            @RequestBody List<Long> roleIdList
    )
            throws ApplicationUserNotFoundException,
            InvalidIdException
    {
        return this.userRolesService.updateUsersRoles(roleIdList, userId);
    }

    @Operation(
            summary = "Просмотр всех доступных ролей пользователя в системе. ",
            description = "Просмотр всех доступных ролей пользователя в системе. " +
                    "Необходимые роли: [ADMIN]"
    )
    @GetMapping(value = "/all")
    public List<UserRoleResponseDto> getAllUserRoles()
            throws UserRolesNotFoundException
    {
        return this.userRolesService.getAllUserRoles();
    }

    @Operation(
            summary = "Просмотр ролей определенного пользователя. ",
            description = "Просмотр ролей определенного пользователя. " +
                    "Необходимые роли: [ADMIN]"
    )
    @GetMapping(value = "/of-user")
    public List<UserRoleResponseDto> getUsersRoles(
            @RequestParam Long userId
    )
            throws UserRolesNotFoundException,
            ApplicationUserNotFoundException,
            InvalidIdException
    {
        return this.userRolesService.getUserRoles(userId);
    }

}
