package kg.airport.airportproject.security;

import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesTestEntityProvider;
import kg.airport.airportproject.security.TestCredentialsProvider;
import kg.airport.airportproject.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnBean(name = "securityConfigurationTest")
public class JwtTokenAuthenticationFactory {
    private final AuthenticationService authenticationService;

    @Autowired
    public JwtTokenAuthenticationFactory(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String getJwtTokenForDefaultUserWithSpecifiedRoleTitle(String roleTitle) {
        if(Objects.isNull(roleTitle) || roleTitle.isEmpty()) {
            throw new IllegalArgumentException("Роль пользователя не может быть null или пустой!");
        }

        ApplicationUserCredentialsRequestDto credentialsRequestDto = new ApplicationUserCredentialsRequestDto();
        if(roleTitle.equals("CLIENT")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_CLIENT_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_CLIENT_RAW_PASSWORD);
        }
        if (roleTitle.equals("CHIEF_ENGINEER")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD);
        }
        if (roleTitle.equals("ENGINEER")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD);
        }
        if(roleTitle.equals("MANAGER")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_MANAGER_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_MANAGER_RAW_PASSWORD);
        }
        if(roleTitle.equals("CHIEF_DISPATCHER")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_CHIEF_DISPATCHER_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_CHIEF_DISPATCHER_RAW_PASSWORD);
        }
        if(roleTitle.equals("DISPATCHER")) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.DEFAULT_DISPATCHER_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.DEFAULT_DISPATCHER_RAW_PASSWORD);
        }
        if(roleTitle.equals(UserRolesTestEntityProvider.TEST_ADMIN_ROLE_TITLE)) {
            credentialsRequestDto.setUsername(TestCredentialsProvider.TEST_ADMIN_USERNAME);
            credentialsRequestDto.setPassword(TestCredentialsProvider.TEST_ADMIN_RAW_PASSWORD);
        }

        return this.formatToken(this.authenticationService.login(credentialsRequestDto).getJwtToken());
    }

    public String authenticateConcreteUser(ApplicationUsersEntity applicationUsersEntity) {
        ApplicationUserCredentialsRequestDto credentialsRequestDto =
                new ApplicationUserCredentialsRequestDto()
                        .setUsername(applicationUsersEntity.getUsername())
                        .setPassword(applicationUsersEntity.getPassword());
        return this.formatToken(this.authenticationService.login(credentialsRequestDto).getJwtToken());
    }

    private String formatToken(String unformattedJwtTokenValue) {
        return "Bearer " + unformattedJwtTokenValue;
    }
}
