package kg.airport.airportproject.security;

import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;

import java.util.List;
import java.util.Objects;

public class TestAuthoritiesFactory {
    public static List<UserRolesEntity> getUserRolesByUserRoleTitle(
            String userRoleTitle
    )
            throws UserRolesNotFoundException
    {
        if(Objects.isNull(userRoleTitle) || userRoleTitle.isEmpty()) {
            throw new IllegalArgumentException("Название роли пользовтеля не может быть null или пустым!");
        }
        if("ENGINEER".equals(userRoleTitle)) {
            return List.of(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        }
        if("MANAGER".equals(userRoleTitle)) {
            return List.of(new UserRolesEntity().setId(3L).setRoleTitle("MANAGER"));
        }
        if("DISPATCHER".equals(userRoleTitle)) {
            return List.of(new UserRolesEntity().setId(5L).setRoleTitle("DISPATCHER"));
        }
        throw new UserRolesNotFoundException("Роли пользователя с указанным названием не найдено!");
    }
}
