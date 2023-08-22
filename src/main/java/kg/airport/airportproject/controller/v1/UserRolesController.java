package kg.airport.airportproject.controller.v1;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/roles")
public class UserRolesController {

    private final UserRolesService userRolesService;

    @Autowired
    public UserRolesController(UserRolesService userRolesService) {
        this.userRolesService = userRolesService;
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping(value = "/all")
    public List<UserRoleResponseDto> getAllUserRoles()
            throws UserRolesNotFoundException
    {
        return this.userRolesService.getAllUserRoles();
    }
}
