package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/users/clients")
@Tag(
        name = "Clients Controller",
        description = "Endpoint'ы для удаления, обновления информации аккаунта клиента и поиска аккаунтов клиентов"
)
public class ClientsController {
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ClientsController(
            ApplicationUserService applicationUserService
    ) {
        this.applicationUserService = applicationUserService;
    }

    @Operation(
            summary = "Удаление аккаунта клиента [РОЛИ]: CLIENT",
            description = "Удаляет аккаунт текщуего клиента по его ID из SecurityContext"
    )
    @PreAuthorize(value = "hasAnyRole('CLIENT')")
    @DeleteMapping(value = "/delete-account")
    public ApplicationUserResponseDto deleteAccount() {
        return this.applicationUserService.deleteCurrentAccount();
    }

    @Operation(
            summary = "Обновление данных аккаунта клиента. [РОЛИ]: CLIENT",
            description = "Обновляет данные аккаунта клиента. Принимает... "
    )
    @PreAuthorize(value = "hasAnyRole('CLIENT')")
    @PutMapping(value = "/update")
    public ApplicationUserResponseDto updateInfo(
            @RequestBody ApplicationUserRequestDto clientRequestDto
    ) {
        return this.applicationUserService.updateCurrentUsersInformation(clientRequestDto);
    }

    @Operation(
            summary = "Поиск аккаунтов клиентов по дате регистрации и статусу аккаунта. [РОЛИ]: MANAGER",
            description = "Поиск клиентов по дате регистрации (registeredAt) и" +
                    " статусу аккаунта (isDeleted -- удален или нет)"
    )
    @PreAuthorize(value = "hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping(value = "/all")
    public List<ApplicationUserResponseDto> getAllClients(
            @RequestParam(required = false) LocalDateTime registeredBefore,
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @RequestParam(required = false) Boolean isDeleted
    )
            throws ApplicationUserNotFoundException
    {
        return this.applicationUserService.getAllClients(registeredBefore, registeredAfter, isDeleted);
    }
}
