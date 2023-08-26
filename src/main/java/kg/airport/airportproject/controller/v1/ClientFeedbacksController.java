package kg.airport.airportproject.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.airport.airportproject.dto.ClientFeedbackRequestDto;
import kg.airport.airportproject.dto.ClientFeedbackResponseDto;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.service.ClientFeedbacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/feedbacks")
@Tag(
        name = "Client Feedbacks Controller",
        description = "Endpoint'ы для добавления и просмотра отзывов о рейсах"
)
public class ClientFeedbacksController {
    private final ClientFeedbacksService clientFeedbacksService;

    @Autowired
    public ClientFeedbacksController(ClientFeedbacksService clientFeedbacksService) {
        this.clientFeedbacksService = clientFeedbacksService;
    }

    @Operation(
            summary = "Регистрация нового отзыва. ",
            description = "Регистрация нового отзыва о рейсе. Принимает dto для регистрации сущности отзыва. " +
                    "Необходимые роли: [CLIENT]"
    )
    @PreAuthorize(value = "hasRole('CLIENT')")
    @PostMapping(value = "/register")
    public ClientFeedbackResponseDto registerNewClientsFeedback(
            @RequestBody ClientFeedbackRequestDto requestDto
    )
            throws InvalidFeedbackTextException,
            UserFlightsNotFoundException,
            FlightsNotFoundException,
            InvalidIdException
    {
        return this.clientFeedbacksService.registerNewClientsFeedback(requestDto);
    }

    @Operation(
            summary = "Просмотр отзывов клиентов о рейсе. ",
            description = "Просмотр отзывов клиентов о рейсе. " +
                    "Параметры поиска: фильтр начальной даты, фильтр конечной даты, id рейса" +
                    "Необходимые роли: [MANAGER, PILOT]"
    )
    @PreAuthorize(value = "hasAnyRole('MANAGER', 'PILOT')")
    @GetMapping(value = "/all")
    public List<ClientFeedbackResponseDto> getAllClientFeedbacks(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime registeredBefore,
            @RequestParam(required = false) Long flightId
    )
            throws ClientFeedbacksNotFoundException,
            IncorrectDateFiltersException,
            InvalidIdException
    {
        return this.clientFeedbacksService.getAllClientsFeedbacks(registeredAfter, registeredBefore, flightId);
    }
}
