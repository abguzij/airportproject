package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.UserRolesNotFoundException;

import java.util.List;

public interface UserRolesService {
    List<UserRoleResponseDto> getAllUserRoles()
            throws UserRolesNotFoundException;

    List<UserRoleResponseDto> getUserRoles(Long userId)
            throws ApplicationUserNotFoundException,
            InvalidIdException,
            UserRolesNotFoundException;

    List<UserRoleResponseDto> updateUsersRoles(List<Long> rolesIdList, Long userId)
            throws ApplicationUserNotFoundException,
            InvalidIdException;
}
