package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.dto.JwtTokenResponseDto;
import kg.airport.airportproject.exception.UserPositionNotExists;
import kg.airport.airportproject.exception.UserRolesNotAssignedException;
import kg.airport.airportproject.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/auth")
@Tag(
        name = "Authentication Controller",
        description = "Endpoint'ы для аутентификации пользователей, регистрации аккаунта клиента и выхода из системы"
)
public class AuthenticationsController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationsController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Вход в систему",
            description = "Аутентификация пользователя по логину и паролю. Возвращает JWT-токен"
    )
    @PostMapping(value = "/login")
    public JwtTokenResponseDto login(
            @RequestBody ApplicationUserCredentialsRequestDto applicationUserCredentialsRequestDto
    ) {
        return this.authenticationService.login(applicationUserCredentialsRequestDto);
    }

    @Operation(
            summary = "Регистрация аккаунта клиента",
            description = "Регистрация аккаунта для клиентов"
    )
    @PostMapping(value = "/register")
    public ApplicationUserResponseDto registerClient(
            @RequestBody ApplicationUserRequestDto requestDto
    )
            throws UserPositionNotExists,
            UserRolesNotAssignedException
    {
        return this.authenticationService.registerNewClient(requestDto);
    }
}
