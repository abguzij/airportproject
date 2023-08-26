package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.ClientFeedbackRequestDto;
import kg.airport.airportproject.dto.ClientFeedbackResponseDto;
import kg.airport.airportproject.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientFeedbacksService {
    @Transactional
    ClientFeedbackResponseDto registerNewClientsFeedback(ClientFeedbackRequestDto requestDto) throws UserFlightsNotFoundException, InvalidIdException, FlightsNotFoundException, InvalidFeedbackTextException;

    List<ClientFeedbackResponseDto> getAllClientsFeedbacks(
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore,
            Long flightId
    ) throws InvalidIdException, IncorrectDateFiltersException, ClientFeedbacksNotFoundException;
}
