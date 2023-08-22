package kg.airport.airportproject.security;


import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnBean(name = "securityConfigurationTest")
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

        if(userRoleTitle.equals("ENGINEER")) {
            applicationUsersEntity.setUsername(TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME);
            applicationUsersEntity.setPassword(
                    this.passwordEncoder.encode(TestCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD)
            );
            applicationUsersEntity.setFullName("Default Engineer");
            applicationUsersEntity.setId(TestCredentialsProvider.ENGINEERS_DEFAULT_ID);
            applicationUsersEntity.setEnabled(true);
            applicationUsersEntity.setUserRolesEntityList(
                    TestAuthoritiesFactory.getUserRolesByUserRoleTitle(userRoleTitle)
            );
        }
        if(userRoleTitle.equals("MANAGER")) {
            applicationUsersEntity.setUsername(TestCredentialsProvider.DEFAULT_MANAGER_USERNAME);
            applicationUsersEntity.setPassword(
                    this.passwordEncoder.encode(TestCredentialsProvider.DEFAULT_MANAGER_RAW_PASSWORD)
            );
            applicationUsersEntity.setFullName("Default Manager");
            applicationUsersEntity.setId(TestCredentialsProvider.MANAGERS_DEFAULT_ID);
            applicationUsersEntity.setEnabled(true);
            applicationUsersEntity.setUserRolesEntityList(
                    TestAuthoritiesFactory.getUserRolesByUserRoleTitle(userRoleTitle)
            );
        }
        if(userRoleTitle.equals("DISPATCHER")) {
            applicationUsersEntity.setUsername(TestCredentialsProvider.DEFAULT_DISPATCHER_USERNAME);
            applicationUsersEntity.setPassword(
                    this.passwordEncoder.encode(TestCredentialsProvider.DEFAULT_DISPATCHER_RAW_PASSWORD)
            );
            applicationUsersEntity.setFullName("Default Dispatcher");
            applicationUsersEntity.setId(TestCredentialsProvider.DISPATCHER_DEFAULT_ID);
            applicationUsersEntity.setEnabled(true);
            applicationUsersEntity.setUserRolesEntityList(
                    TestAuthoritiesFactory.getUserRolesByUserRoleTitle(userRoleTitle)
            );
        }
        
        return applicationUsersEntity;
    }
}
