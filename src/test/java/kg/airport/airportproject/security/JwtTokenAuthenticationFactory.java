package kg.airport.airportproject.security;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
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
            credentialsRequestDto.setUsername(SecurityConfigurationTest.DEFAULT_CLIENT_USERNAME);
            credentialsRequestDto.setPassword(SecurityConfigurationTest.DEFAULT_CLIENT_RAW_PASSWORD);
        }
        if (roleTitle.equals("CHIEF_ENGINEER")) {
            credentialsRequestDto.setUsername(SecurityConfigurationTest.DEFAULT_CHIEF_ENGINEERS_USERNAME);
            credentialsRequestDto.setPassword(SecurityConfigurationTest.DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD);
        }

        return this.formatToken(this.authenticationService.login(credentialsRequestDto).getJwtToken());
    }

    private String formatToken(String unformattedJwtTokenValue) {
        return "Bearer " + unformattedJwtTokenValue;
    }
}
