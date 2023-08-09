package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.ClientFeedbackRequestDto;
import kg.airport.airportproject.dto.ClientFeedbackResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.FeedbacksMapper;
import kg.airport.airportproject.repository.ClientFeedbacksEntityRepository;
import kg.airport.airportproject.service.ClientFeedbacksService;
import kg.airport.airportproject.service.FlightsService;
import kg.airport.airportproject.service.UserFlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ClientFeedbacksServiceImpl implements ClientFeedbacksService {
    private final ClientFeedbacksEntityRepository clientFeedbacksEntityRepository;
    private final UserFlightsService userFlightsService;
    private final FlightsService flightsService;

    @Autowired
    public ClientFeedbacksServiceImpl(
            ClientFeedbacksEntityRepository clientFeedbacksEntityRepository,
            UserFlightsService userFlightsService,
            FlightsService flightsService
    ) {
        this.clientFeedbacksEntityRepository = clientFeedbacksEntityRepository;
        this.userFlightsService = userFlightsService;
        this.flightsService = flightsService;
    }

    @Override
    public ClientFeedbackResponseDto registerNewClientsFeedback(ClientFeedbackRequestDto requestDto)
            throws UserFlightsNotFoundException,
            InvalidIdException,
            FlightsNotFoundException,
            InvalidFeedbackTextException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемый отзыв не может быть null!");
        }
        if(requestDto.getFeedbackText().isEmpty() || Objects.isNull(requestDto.getFeedbackText())) {
            throw new InvalidFeedbackTextException("Текст отзыва не может быть null или пустым!");
        }

        ClientFeedbacksEntity feedback = FeedbacksMapper.mapClientFeedbackRequestDtoToEntity(requestDto);
        ApplicationUsersEntity currentUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserFlightsEntity lastClientRegistration =
                this.userFlightsService.getClientFlightRegistrationById(currentUser.getId());
        FlightsEntity flight =
                this.flightsService.getFlightEntityByFlightId(lastClientRegistration.getFlightsEntity().getId());

        feedback.setApplicationUsersEntity(currentUser);
        feedback.setFlightsEntity(flight);

        feedback = this.clientFeedbacksEntityRepository.save(feedback);
        return FeedbacksMapper.mapToClientFeedbackResponseDto(feedback);
    }

    @Override
    public List<ClientFeedbackResponseDto> getAllClientsFeedbacks(
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore,
            Long flightId
    )
            throws InvalidIdException,
            IncorrectDateFiltersException, CLientFeedbacksNotFoundException {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QClientFeedbacksEntity root = QClientFeedbacksEntity.clientFeedbacksEntity;

        if(Objects.nonNull(flightId)) {
            if(flightId < 1L) {
                throw new InvalidIdException("ID рейса не может быть меньше 1!");
            }
        }

        boolean registeredAfterIsNonNull = Objects.nonNull(registeredAfter);
        if(registeredAfterIsNonNull) {
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }
        if(Objects.nonNull(registeredBefore)) {
            if(registeredAfterIsNonNull && registeredAfter.isAfter(registeredBefore)) {
                throw new IncorrectDateFiltersException(
                        "Неверно заданы фильтры поиска по дате! Начальная дата не может быть позже конечной!"
                );
            }
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }

        Iterable<ClientFeedbacksEntity> clientFeedbacksEntityIterable =
                this.clientFeedbacksEntityRepository.findAll(booleanBuilder.getValue());
        List<ClientFeedbackResponseDto> clientFeedbackResponseDtoList =
                StreamSupport
                        .stream(clientFeedbacksEntityIterable.spliterator(), false)
                        .map(FeedbacksMapper::mapToClientFeedbackResponseDto)
                        .collect(Collectors.toList());
        if(clientFeedbackResponseDtoList.isEmpty()) {
            throw new CLientFeedbacksNotFoundException("Отзывов пользователей по заданным параметрам не найдено!");
        }
        return clientFeedbackResponseDtoList;
    }
}
