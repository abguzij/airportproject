package kg.airport.airportproject.security;


import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TestApplicationUsersFactory {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestApplicationUsersFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public ApplicationUsersEntity getApplicationUserByRequiredRole(String userRoleTitle)
            throws UserRolesNotFoundException
    {
        if(Objects.isNull(userRoleTitle) || userRoleTitle.isEmpty()) {
            throw new IllegalArgumentException("Название роли пользовтеля не может быть null или пустым!");
        }
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity();
        
        if(userRoleTitle.equals("MANAGER")) {
            applicationUsersEntity.setUsername(UserDetailsConfigurationTest.DEFAULT_MANAGER_USERNAME);
            applicationUsersEntity.setPassword(
                    this.passwordEncoder.encode(UserDetailsConfigurationTest.DEFAULT_MANAGER_RAW_PASSWORD)
            );
            applicationUsersEntity.setFullName("Default Manager");
            applicationUsersEntity.setId(4L);
            applicationUsersEntity.setEnabled(true);
            applicationUsersEntity.setUserRolesEntityList(
                    TestAuthoritiesFactory.getUserRolesByUserRoleTitle(userRoleTitle)
            );
        }
        
        return applicationUsersEntity;
    }
}
