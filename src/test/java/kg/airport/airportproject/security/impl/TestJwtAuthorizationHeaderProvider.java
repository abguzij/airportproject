package kg.airport.airportproject.security.impl;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.security.TestAuthorizationHeaderProvider;
import kg.airport.airportproject.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnBean(value = SecurityConfigurationTest.class)
// TODO: 21.08.2023 Переписать получение заголовка авторизации на этот класс
public class TestJwtAuthorizationHeaderProvider implements TestAuthorizationHeaderProvider {
    private final AuthenticationService authenticationService;
    @Autowired
    public TestJwtAuthorizationHeaderProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public HttpHeaders getAuthorizationHeaderForUser(ApplicationUsersEntity applicationUsersEntity) {
        if(Objects.isNull(applicationUsersEntity)) {
            throw new IllegalArgumentException(
                    "Сущность пользователя для формирования заголовка авторизации не может быть null!"
            );
        }

        ApplicationUserCredentialsRequestDto credentialsRequestDto =
                this.getCredentialsFromApplicationUsersEntity(applicationUsersEntity);
        String jwtAuthorizationHeaderValue =
                this.formatJwtAuthorizationHeader(this.authenticationService.login(credentialsRequestDto).getJwtToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtAuthorizationHeaderValue);
        return httpHeaders;
    }

    private ApplicationUserCredentialsRequestDto getCredentialsFromApplicationUsersEntity(
            ApplicationUsersEntity applicationUsersEntity
    ) {
        String username = applicationUsersEntity.getUsername();
        String password = applicationUsersEntity.getPassword();
        if(Objects.isNull(username) || username.isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть null или пустым!");
        }
        if(Objects.isNull(password) || password.isEmpty()) {
            throw new IllegalArgumentException("Пароль пользователя не может быть null или пустым!");
        }

        return new ApplicationUserCredentialsRequestDto()
                .setUsername(username)
                .setPassword(password);
    }

    private String formatJwtAuthorizationHeader(String rawTokenValue) {
        if(Objects.isNull(rawTokenValue) || rawTokenValue.isEmpty()) {
            throw new IllegalArgumentException("Значение JWT не может быть null или пустым!");
        }
        return "Bearer " + rawTokenValue;
    }
}
