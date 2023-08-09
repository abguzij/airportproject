package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.FlightsMapper;
import kg.airport.airportproject.repository.FlightsEntityRepository;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FlightsServiceImpl implements FlightsService {
    private final FlightsEntityRepository flightsEntityRepository;
    private final AircraftsService aircraftsService;
    private final AircaftSeatsService aircaftSeatsService;

    @Autowired
    public FlightsServiceImpl(
            FlightsEntityRepository flightsEntityRepository,
            AircraftsService aircraftsService,
            AircaftSeatsService aircaftSeatsService
    ) {
        this.flightsEntityRepository = flightsEntityRepository;
        this.aircraftsService = aircraftsService;
        this.aircaftSeatsService = aircaftSeatsService;
    }

    @Override
    public FlightResponseDto registerNewFlight(FlightRequestDto requestDto)
            throws AircraftNotFoundException,
            InvalidIdException,
            UnavailableAircraftException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемый рейс не может быть null!");
        }

        FlightsEntity flightsEntity = FlightsMapper.mapFlightRequestDtoToEntity(requestDto);
        AircraftsEntity aircraft = this.aircraftsService.findAircraftsEntityById(requestDto.getAircraftId());
        if(!aircraft.getStatus().equals(AircraftStatus.AVAILABLE)) {
            throw new UnavailableAircraftException(
                    "Ошибка! Для регистрации нового рейса необходимо выбрать самолет," +
                            " зарегестрированный в системе как доступный"
            );
        }
        flightsEntity.setAircraftsEntity(aircraft);

        Integer aircraftSeatsNumber = this.aircaftSeatsService.getNumberOfFreeSeatsByAircraftId(aircraft.getId());
        flightsEntity.setTicketsLeft(aircraftSeatsNumber);

        flightsEntity = this.flightsEntityRepository.save(flightsEntity);
        return FlightsMapper.mapToFlightResponseDto(flightsEntity);
    }

    @Override
    public FlightsEntity updateNumberOfRemainingTickets(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException
    {
        FlightsEntity flight = this.getFlightEntityByFlightId(flightId);

        Integer freeSeats = this.aircaftSeatsService.getNumberOfFreeSeatsByAircraftId(flight.getId());
        if(freeSeats.equals(0)) {
            flight.setStatus(FlightStatus.SOLD_OUT);
        }

        flight.setTicketsLeft(freeSeats);
        return this.flightsEntityRepository.save(flight);
    }

    @Override
    public void informThatAllCrewMembersIsReadyForFlight(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CLIENTS_READY)) {
            throw new StatusChangeException(
                    "Перед проверкой готовности членов экипажа необходимо проверить готовность пассажиров!"
            );
        }
        flightsEntity.setStatus(FlightStatus.CREW_READY);
        this.flightsEntityRepository.save(flightsEntity);
    }

    @Override
    public void informThatAllClientsAreChecked(Long flightId) throws FlightsNotFoundException, InvalidIdException, StatusChangeException {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CLIENT_CHECK)) {
            throw new StatusChangeException(
                    "Чтобы провести проверку готовности пассажиров, она должна быть назначена диспетчером!"
            );
        }
        flightsEntity.setStatus(FlightStatus.CLIENTS_CHECKED);
        this.flightsEntityRepository.save(flightsEntity);
    }

    @Override
    public void informThatAllClientsAreBriefed(Long flightId) throws FlightsNotFoundException, InvalidIdException, StatusChangeException {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CLIENTS_BRIEFING)) {
            throw new StatusChangeException(
                    "Чтобы провести инструктаж пассажиров он должен быть назначен главным стюардом!"
            );
        }
        flightsEntity.setStatus(FlightStatus.CLIENTS_BRIEFED);
        this.flightsEntityRepository.save(flightsEntity);
    }

    @Override
    public void informThatAllClientsFoodIsDistributed(Long flightId) throws FlightsNotFoundException, InvalidIdException, StatusChangeException {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.FLIGHT_FOOD_DISTRIBUTION)) {
            throw new StatusChangeException(
                    "Чтобы провести раздачу еды она должна быть назначена главным стюардом!"
            );
        }
        flightsEntity.setStatus(FlightStatus.FLIGHT_FOOD_DISTRIBUTED);
        this.flightsEntityRepository.save(flightsEntity);
    }

    @Override
    public StatusChangedResponse confirmFlightRegistration(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CREW_MEMBERS_REGISTERED)) {
            throw new StatusChangeException(
                    "Чтобы регистрация рейса могла быть подтверждена на" +
                            " него сначала должны быть зарегестрированы все необходимые члены экипажа!"
            );
        }

        flightsEntity.setStatus(FlightStatus.SELLING_TICKETS);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Регистрация рейса подтверждена! Начинается продажа билетов! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse initiateFlightDeparturePreparations(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.SOLD_OUT)) {
            throw new StatusChangeException(
                    "Перед инициацией отправки рейса на рейс должны быть выкуплены все билеты!"
            );
        }

        flightsEntity.setStatus(FlightStatus.DEPARTURE_INITIATED);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Отправка рейса иницирована! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse initiateCrewPreparation(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.TECH_PREP_COMPLETE)) {
            throw new StatusChangeException(
                    "Перед началом проверки клиентов должна быть проведена заправка самолета!"
            );
        }

        flightsEntity.setStatus(FlightStatus.CLIENT_CHECK);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Проверка готовности клиентов начата! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse confirmAircraftRefueling(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException,
            AircraftNotReadyException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.DEPARTURE_INITIATED)) {
            throw new StatusChangeException(
                    "Чтобы провести заправку самолета должна быть инициировона отправка рейса!"
            );
        }
        if(!flightsEntity.getAircraftsEntity().getStatus().equals(AircraftStatus.REFUELED)) {
            throw new AircraftNotReadyException(
                    "Ошибка подтверждения заправки самолета! Заправка не была проведена!"
            );
        }

        flightsEntity.setStatus(FlightStatus.TECH_PREP_COMPLETE);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Заправка самолета подтверждена! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse assignBriefing(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CLIENTS_CHECKED)) {
            throw new StatusChangeException(
                    "Перед проведением инструктажа необходимо проверить правильность занимаемых клиентами мест!"
            );
        }

        flightsEntity.setStatus(FlightStatus.CLIENTS_BRIEFING);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Проверка готовности клиентов начата! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse confirmClientReadiness(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CLIENTS_BRIEFED)) {
            throw new StatusChangeException(
                    "Для подтверждения инструктажа необходимо чтобы все клиенты были проинструктированы!"
            );
        }

        flightsEntity.setStatus(FlightStatus.CLIENTS_READY);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Готовность клиентов подтверждена! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse initiateDeparture(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.CREW_READY)) {
            throw new StatusChangeException(
                    "Для инициации старта рейса необходимо, чтобы все члены экипажа подтвердили свою готовность!"
            );
        }

        flightsEntity.setStatus(FlightStatus.DEPARTURE_READY);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Старт рейса инициирован! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse confirmDeparture(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.DEPARTURE_READY)) {
            throw new StatusChangeException(
                    "Для подтверждения старта рейса он должен быть инициирован диспетчером!"
            );
        }

        flightsEntity.setStatus(FlightStatus.DEPARTURE_CONFIRMED);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Старт рейса подтвержден! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse startFlight(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.DEPARTURE_CONFIRMED)) {
            throw new StatusChangeException(
                    "Для подтверждения старта рейса он должен быть инициирован диспетчером!"
            );
        }

        flightsEntity.setStatus(FlightStatus.FLIGHT_STARTED);
        flightsEntity.getAircraftsEntity().setStatus(AircraftStatus.IN_AIR);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Рейс начат! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse assignFoodDistribution(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.FLIGHT_STARTED)) {
            throw new StatusChangeException(
                    "Для назначения раздачи еды рейс должен быть начат!"
            );
        }

        flightsEntity.setStatus(FlightStatus.FLIGHT_FOOD_DISTRIBUTION);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Рздача еды назначена! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse requestLanding(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        FlightsEntity flightsEntity = this.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.FLIGHT_FOOD_DISTRIBUTED)) {
            throw new StatusChangeException(
                    "Для запроса посадки раздача еды должна закончиться и все клиенты должны занять свои места!"
            );
        }

        flightsEntity.setStatus(FlightStatus.LANDING_REQUESTED);
        flightsEntity = this.flightsEntityRepository.save(flightsEntity);

        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Посадка запрошена! Текущий статус рейса: [%s]",
                                flightsEntity.getStatus().toString()
                        )
                );
    }

    @Override
    public List<FlightResponseDto> getAllFLights(
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore,
            FlightStatus flightStatus
    )
            throws IncorrectDateFiltersException, FlightsNotFoundException {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.createCommonFlightsSearchPredicate(registeredAfter, registeredBefore, flightStatus)
        );

        Iterable<FlightsEntity> flightsEntityIterable =
                this.flightsEntityRepository.findAll(booleanBuilder.getValue());
        List<FlightResponseDto> flightResponseDtoList =
                StreamSupport
                        .stream(flightsEntityIterable.spliterator(), false)
                        .map(FlightsMapper::mapToFlightResponseDto)
                        .collect(Collectors.toList());
        if(flightResponseDtoList.isEmpty()) {
            throw new FlightsNotFoundException("Рейсы по заданным параметрам не найдены!");
        }
        return flightResponseDtoList;
    }

    @Override
    public List<FlightResponseDto> getFlightsForTicketReservation(
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            String flightDestination
    )
            throws FlightsNotFoundException,
            IncorrectDateFiltersException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.createCommonFlightsSearchPredicate(createdAfter, createdBefore, FlightStatus.SELLING_TICKETS)
        );
        QFlightsEntity root = QFlightsEntity.flightsEntity;

        booleanBuilder.and(root.destination.eq(flightDestination));
        booleanBuilder.and(root.ticketsLeft.gt(0));

        Iterable<FlightsEntity> flightsEntityIterable =
                this.flightsEntityRepository.findAll(booleanBuilder.getValue());
        List<FlightResponseDto> flightResponseDtoList =
                StreamSupport
                        .stream(flightsEntityIterable.spliterator(), false)
                        .map(FlightsMapper::mapToFlightResponseDto)
                        .collect(Collectors.toList());
        if(flightResponseDtoList.isEmpty()) {
            throw new FlightsNotFoundException(
                    "Рейсы, на которые продвются билеты, по заданным параметрам не найдены!"
            );
        }
        return flightResponseDtoList;
    }

    @Override
    public FlightsEntity getFlightEntityByFlightId(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException
    {
        if(Objects.isNull(flightId)) {
            throw new IllegalArgumentException("ID рейса не может быть null!");
        }
        if(flightId < 1L) {
            throw new InvalidIdException("ID рейса не может быть меньше 1!");
        }

        Optional<FlightsEntity> flightsEntityOptional = this.flightsEntityRepository.getFlightsEntityById(flightId);
        if(flightsEntityOptional.isEmpty()) {
            throw new FlightsNotFoundException(
                    String.format("Рейса с ID[%d] не найдено!")
            );
        }
        return flightsEntityOptional.get();
    }

    private Predicate createCommonFlightsSearchPredicate(
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore,
            FlightStatus flightStatus
    ) throws IncorrectDateFiltersException {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightsEntity root = QFlightsEntity.flightsEntity;

        if(Objects.nonNull(flightStatus)) {
            booleanBuilder.and(root.status.eq(flightStatus));
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

        return booleanBuilder.getValue();
    }
}
