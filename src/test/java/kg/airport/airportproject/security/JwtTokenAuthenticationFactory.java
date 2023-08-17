package kg.airport.airportproject.security;

import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.TestComponent;
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
            credentialsRequestDto.setUsername(DefaultCredentialsProvider.DEFAULT_CLIENT_USERNAME);
            credentialsRequestDto.setPassword(DefaultCredentialsProvider.DEFAULT_CLIENT_RAW_PASSWORD);
        }
        if (roleTitle.equals("CHIEF_ENGINEER")) {
            credentialsRequestDto.setUsername(DefaultCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_USERNAME);
            credentialsRequestDto.setPassword(DefaultCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD);
        }
        if (roleTitle.equals("ENGINEER")) {
            credentialsRequestDto.setUsername(DefaultCredentialsProvider.DEFAULT_ENGINEERS_USERNAME);
            credentialsRequestDto.setPassword(DefaultCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD);
        }
        if(roleTitle.equals("MANAGER")) {
            credentialsRequestDto.setUsername(DefaultCredentialsProvider.DEFAULT_MANAGER_USERNAME);
            credentialsRequestDto.setPassword(DefaultCredentialsProvider.DEFAULT_MANAGER_RAW_PASSWORD);
        }
        if(roleTitle.equals("DISPATCHER")) {
            credentialsRequestDto.setUsername(DefaultCredentialsProvider.DEFAULT_DISPATCHER_USERNAME);
            credentialsRequestDto.setPassword(DefaultCredentialsProvider.DEFAULT_DISPATCHER_RAW_PASSWORD);
        }

        return this.formatToken(this.authenticationService.login(credentialsRequestDto).getJwtToken());
    }

    private String formatToken(String unformattedJwtTokenValue) {
        return "Bearer " + unformattedJwtTokenValue;
    }
}
