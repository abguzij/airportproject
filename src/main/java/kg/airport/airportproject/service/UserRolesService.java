package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.exception.UserRolesNotFoundException;

import java.util.List;

public interface UserRolesService {
    List<UserRoleResponseDto> getAllUserRoles() throws UserRolesNotFoundException;
}
