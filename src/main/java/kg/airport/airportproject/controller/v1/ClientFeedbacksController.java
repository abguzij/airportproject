package kg.airport.airportproject.controller.v1;

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
public class ClientFeedbacksController {
    private final ClientFeedbacksService clientFeedbacksService;

    @Autowired
    public ClientFeedbacksController(ClientFeedbacksService clientFeedbacksService) {
        this.clientFeedbacksService = clientFeedbacksService;
    }

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
