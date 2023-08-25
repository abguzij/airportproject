package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.dto.UserFlightRegistrationResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.FlightsMapper;
import kg.airport.airportproject.repository.UserFlightsEntityRepository;
import kg.airport.airportproject.service.AircraftSeatsService;
import kg.airport.airportproject.service.ApplicationUserService;
import kg.airport.airportproject.service.FlightsService;
import kg.airport.airportproject.service.UserFlightsService;
import kg.airport.airportproject.utils.UserRolesUtils;
import kg.airport.airportproject.validator.UserFlightsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserFlightsServiceImpl implements UserFlightsService {
    private final UserFlightsEntityRepository userFlightsEntityRepository;
    private final AircraftSeatsService aircraftSeatsService;
    private final ApplicationUserService applicationUserService;
    private final FlightsService flightsService;
    private final UserFlightsValidator userFlightsValidator;

    @Autowired
    public UserFlightsServiceImpl(
            UserFlightsEntityRepository userFlightsEntityRepository,
            AircraftSeatsService aircraftSeatsService,
            ApplicationUserService applicationUserService,
            FlightsService flightsService,
            UserFlightsValidator userFlightsValidator
    ) {
        this.userFlightsEntityRepository = userFlightsEntityRepository;
        this.aircraftSeatsService = aircraftSeatsService;
        this.applicationUserService = applicationUserService;
        this.flightsService = flightsService;
        this.userFlightsValidator = userFlightsValidator;
    }

    @Override
    public List<UserFlightRegistrationResponseDto> registerEmployeesForFlight(
            List<UserFlightRequestDto> requestDtoList
    )
            throws InvalidIdException,
            FlightsNotFoundException,
            WrongFlightException,
            ApplicationUserNotFoundException,
            NotEnoughRolesForCrewRegistrationException,
            InvalidUserRoleException
    {
        if(Objects.isNull(requestDtoList) || requestDtoList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Список регистрируемых на рейс сотрудников не может быть null или пустым!"
            );
        }

        Long flightId = requestDtoList.get(0).getFlightId();
        List<Long> crewMembersIdList = new ArrayList<>();
        for (UserFlightRequestDto requestDto : requestDtoList) {

            this.userFlightsValidator.validateUserFlightsRequestDto(requestDto);
            this.userFlightsValidator.validateCrewMemberId(requestDto.getUserId());

            Long crewMemberId = requestDto.getUserId();
            Long comparativeFlightId = requestDto.getFlightId();
            if(Objects.isNull(comparativeFlightId) || Objects.isNull(crewMemberId)) {
                throw new IllegalArgumentException(
                        "ID рейса, на который регистрируется сотрудник, и ID сотрудника не может быть null!"
                );
            }
            if(comparativeFlightId < 1L || crewMemberId < 1L) {
                throw new InvalidIdException(
                        "ID рейса, на который регистрируется сотрудник, и ID сотруднка не может быть меньше 1!"
                );
            }
            if(!flightId.equals(comparativeFlightId)){
                throw new InvalidIdException(
                        "ID рейса для всех регистрируемых на этот рейс сотрудников должен совпадать!"
                );
            }
            crewMembersIdList.add(crewMemberId);
        }

        FlightsEntity flightsEntity = this.flightsService.getFlightEntityByFlightId(flightId);
        if(!flightsEntity.getStatus().equals(FlightStatus.REGISTERED)) {
            throw new WrongFlightException(
                    "Регистрация сотрудников возможна только на недавно зарегистрированный рейс!"
            );
        }

        List<ApplicationUsersEntity> crewMembers =
                this.applicationUserService.getUserEntitiesByIdList(crewMembersIdList);
        boolean userRolesEnoughForRegistration =
                UserRolesUtils.checkIfApplicationUsersListContainsSuchUserRolesTitles(
                        crewMembers,
                        "PILOT", "STEWARD", "CHIEF_STEWARD"
                );
        if(!userRolesEnoughForRegistration) {
            throw new NotEnoughRolesForCrewRegistrationException(
                    "Для регистрации рейса необходимо, чтобы в команде был хотя бы 1 пилот, стюард и старший стюард!"
            );
        }
        boolean crewMembersListContainsOnlyUsersWithValidRoles =
                UserRolesUtils.checkIfEachApplicationUserInListContainsRequiredRoles(
                        crewMembers,
                        "PILOT", "STEWARD", "CHIEF_STEWARD"
                );
        if(!crewMembersListContainsOnlyUsersWithValidRoles) {
            throw new InvalidUserRoleException(
                    "Список регистрируемых на рейс сотрудников содержит пользователя с недопустимыми ролями!"
            );
        }

        flightsEntity.setStatus(FlightStatus.CREW_MEMBERS_REGISTERED);
        List<UserFlightsEntity> crewMembersRegistrations = new ArrayList<>();
        for (ApplicationUsersEntity crewMember : crewMembers) {
            UserFlightsEntity crewMemberRegistration = new UserFlightsEntity();
            crewMemberRegistration.setApplicationUsersEntity(crewMember);

            crewMemberRegistration.setFlightsEntity(flightsEntity);
            flightsEntity.getUserFlightsEntities().add(crewMemberRegistration);

            crewMemberRegistration.setUserStatus(UserFlightsStatus.CREW_MEMBER_REGISTERED_FOR_FLIGHT);

            crewMembersRegistrations.add(crewMemberRegistration);
        }

        crewMembersRegistrations = this.userFlightsEntityRepository.saveAll(crewMembersRegistrations);
        return crewMembersRegistrations
                .stream()
                .map(FlightsMapper::mapToUserFlightRegistrationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserFlightRegistrationResponseDto registerClientForFlight(UserFlightRequestDto requestDto)
            throws InvalidIdException,
            FlightsNotFoundException,
            WrongFlightException,
            AircraftSeatNotFoundException,
            SeatReservationException
    {
        this.userFlightsValidator.validateUserFlightsRequestDto(requestDto);
        this.userFlightsValidator.validateAircraftSeatId(requestDto.getAircraftSeatId());

        ApplicationUsersEntity client =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FlightsEntity flight = this.flightsService.getFlightEntityByFlightId(requestDto.getFlightId());
        if(!flight.getStatus().equals(FlightStatus.SELLING_TICKETS)) {
            throw new WrongFlightException(
                    "Регистрация пользователей возможно только на рейсы, на которые продаются билеты!"
            );
        }

        AircraftSeatsEntity aircraftSeatsEntity =
                this.aircraftSeatsService.reserveSeat(requestDto.getAircraftSeatId());
        flight = this.flightsService.updateNumberOfRemainingTickets(flight.getId());

        UserFlightsEntity userFlightsEntity = new UserFlightsEntity();
        userFlightsEntity.setFlightsEntity(flight);
        userFlightsEntity.setAircraftSeatsEntity(aircraftSeatsEntity);
        userFlightsEntity.setApplicationUsersEntity(client);
        userFlightsEntity.setUserStatus(UserFlightsStatus.CLIENT_REGISTERED_FOR_FLIGHT);

        this.userFlightsEntityRepository.save(userFlightsEntity);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(userFlightsEntity);
    }

    @Override
    public UserFlightRegistrationResponseDto cancelClientRegistration(Long registrationId)
            throws InvalidIdException,
            UserFlightsNotFoundException,
            TicketCancelingException,
            AircraftSeatNotFoundException,
            SeatReservationException,
            FlightsNotFoundException
    {
        ApplicationUsersEntity client =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserFlightsEntity clientRegistration =
                this.getClientFlightRegistrationByClientIdAndUserFlightId(registrationId, client.getId());
        if(!clientRegistration.getFlightsEntity().getStatus().equals(FlightStatus.SELLING_TICKETS)) {
            throw new TicketCancelingException(
                    "Ошибка отмены регистрации на рейс! Отменить регистрацию возможно только во время продажи билетов!"
            );
        }

        this.aircraftSeatsService.cancelSeatReservation(clientRegistration.getAircraftSeatsEntity().getId());
        this.flightsService.updateNumberOfRemainingTickets(clientRegistration.getFlightsEntity().getId());

        clientRegistration.setUserStatus(UserFlightsStatus.CLIENT_REGISTRATION_DECLINED);

        clientRegistration = this.userFlightsEntityRepository.save(clientRegistration);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(clientRegistration);
    }

    @Override
    public UserFlightRegistrationResponseDto checkClient(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        UserFlightsEntity clientRegistration = this.getClientFlightRegistrationById(clientRegistrationId);
        FlightsEntity flight = clientRegistration.getFlightsEntity();
        if(!flight.getStatus().equals(FlightStatus.CLIENT_CHECK)) {
            throw new StatusChangeException(
                    "Чтобы начать проверку мест клиентов она должна быть назначена диспетчером!"
            );
        }
        if(!clientRegistration.getUserStatus().equals(UserFlightsStatus.CLIENT_REGISTERED_FOR_FLIGHT)) {
            throw new StatusChangeException(
                    "Чтобы проверить правильность занятого клиентом места он должен быть зарегестрированным на рейс!"
            );
        }

        clientRegistration.setUserStatus(UserFlightsStatus.CLIENT_CHECKED);

        clientRegistration = this.userFlightsEntityRepository.save(clientRegistration);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(clientRegistration);
    }

    @Override
    public UserFlightRegistrationResponseDto distributeClientsFood(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        UserFlightsEntity clientRegistration = this.getClientFlightRegistrationById(clientRegistrationId);
        FlightsEntity flight = clientRegistration.getFlightsEntity();
        if(!flight.getStatus().equals(FlightStatus.FLIGHT_FOOD_DISTRIBUTION)) {
            throw new StatusChangeException(
                    "Чтобы начать раздачу еды она должна быть назначена главным стюардом!"
            );
        }
        if(!clientRegistration.getUserStatus().equals(UserFlightsStatus.CLIENT_BRIEFED)) {
            throw new StatusChangeException(
                    "Чтобы провести клиенту раздачу еды он должен быть помечен как проиструктированный!"
            );
        }

        clientRegistration.setUserStatus(UserFlightsStatus.CLIENT_FOOD_DISTRIBUTED);

        clientRegistration = this.userFlightsEntityRepository.save(clientRegistration);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(clientRegistration);
    }

    @Override
    public UserFlightRegistrationResponseDto conductClientsBriefing(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        UserFlightsEntity clientRegistration = this.getClientFlightRegistrationById(clientRegistrationId);
        FlightsEntity flight = clientRegistration.getFlightsEntity();
        if(!flight.getStatus().equals(FlightStatus.CLIENTS_BRIEFING)) {
            throw new StatusChangeException(
                    "Чтобы начать инструктаж клиентов он должен быть назначен главным стюардом!"
            );
        }
        if(!clientRegistration.getUserStatus().equals(UserFlightsStatus.CLIENT_CHECKED)) {
            throw new StatusChangeException(
                    "Чтобы провести клиенту инструктаж стюард должен проверить занял ли клиент свое место в салоне!"
            );
        }

        clientRegistration.setUserStatus(UserFlightsStatus.CLIENT_BRIEFED);

        clientRegistration = this.userFlightsEntityRepository.save(clientRegistration);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(clientRegistration);
    }

    @Override
    public UserFlightRegistrationResponseDto confirmReadinessForFlight()
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserFlightsEntity userFlightsEntity = this.getUserFlightRegistrationByUserId(authorizedUser.getId());
        if(!userFlightsEntity.getFlightsEntity().getStatus().equals(FlightStatus.CLIENTS_READY)) {
            throw new StatusChangeException(
                    "Проверка готовности экипажа может начаться только после проверки готовности клиентов!"
            );
        }
        if(!userFlightsEntity.getUserStatus().equals(UserFlightsStatus.CREW_MEMBER_REGISTERED_FOR_FLIGHT)) {
            throw new StatusChangeException(
                    String.format(
                            "Пользователь с ID[%d] не был зарегистрирован на данный рейс как член экипажа!",
                            authorizedUser.getId()
                    )
            );
        }

        userFlightsEntity.setUserStatus(UserFlightsStatus.CREW_MEMBER_READY);
        this.checkIfAllCrewMembersIsReadyForFlight(userFlightsEntity.getFlightsEntity().getId());

        userFlightsEntity = this.userFlightsEntityRepository.save(userFlightsEntity);
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(userFlightsEntity);
    }

    @Override
    public List<UserFlightRegistrationResponseDto> getAllUserRegistrations(
            Long flightId,
            UserFlightsStatus status,
            Long userId,
            Boolean isClients
    )
            throws UserFlightsNotFoundException, InvalidIdException {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUserFlightsEntity root = QUserFlightsEntity.userFlightsEntity;

        if(Objects.nonNull(flightId)) {
            booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        }
        if(Objects.nonNull(status)) {
            booleanBuilder.and(root.userStatus.eq(status));
        }
        if(Objects.nonNull(userId)) {
            if(userId < 1L) {
                throw new InvalidIdException("ID пользователя не может быть меньше 1!");
            }
            booleanBuilder.and(root.applicationUsersEntity.id.eq(userId));
        }
        if(Objects.nonNull(isClients)) {
            if(isClients) {
                booleanBuilder.and(root.applicationUsersEntity.userPosition.positionTitle.eq("CLIENT"));
            } else {
                booleanBuilder.and(root.applicationUsersEntity.userPosition.positionTitle.ne("CLIENT"));
            }
        }

        Iterable<UserFlightsEntity> userFlightsEntityIterable =
                this.userFlightsEntityRepository.findAll(booleanBuilder.getValue());
        List<UserFlightRegistrationResponseDto> userFlightRegistrationResponseDtoList =
                StreamSupport
                        .stream(userFlightsEntityIterable.spliterator(), false)
                        .map(FlightsMapper::mapToUserFlightRegistrationResponseDto)
                        .collect(Collectors.toList());
        if(userFlightRegistrationResponseDtoList.isEmpty()) {
            throw new UserFlightsNotFoundException(
                    "Регистраций пользователей на рейс по укзанным параметрам не найдено!"
            );
        }
        return userFlightRegistrationResponseDtoList;
    }

    @Override
    public List<UserFlightRegistrationResponseDto> getAllClientRegistrations(
            Long flightId,
            UserFlightsStatus status
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.getAllUserRegistrations(flightId, status, null, Boolean.TRUE);
    }

    @Override
    public List<UserFlightRegistrationResponseDto> getAllEmployeesRegistrations(
            Long flightId,
            UserFlightsStatus status
    )
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        return this.getAllUserRegistrations(flightId, status, null, Boolean.FALSE);
    }

    @Override
    public List<UserFlightRegistrationResponseDto> getAllClientRegistrationsForCurrentFLight(UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserFlightsEntity userRegistration = this.getUserFlightRegistrationByUserId(authorizedUser.getId());
        return this.getAllClientRegistrations(userRegistration.getFlightsEntity().getId(), status);
    }

    @Override
    public List<UserFlightRegistrationResponseDto> getClientsFlightRegistrationHistory(UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.getAllUserRegistrations(null, status, authorizedUser.getId(), Boolean.TRUE);
    }

    @Override
    public UserFlightRegistrationResponseDto getCurrentFlight()
            throws UserFlightsNotFoundException,
            InvalidIdException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return FlightsMapper.mapToUserFlightRegistrationResponseDto(
                this.getUserFlightRegistrationByUserId(authorizedUser.getId())
        );
    }

    @Override
    public UserFlightsEntity getClientFlightRegistrationByClientIdAndUserFlightId(
            Long registrationId,
            Long userId
    ) throws InvalidIdException, UserFlightsNotFoundException {
        if(Objects.isNull(registrationId)) {
            throw new IllegalArgumentException("ID регистрации на рейс может быть null!");
        }
        if(registrationId < 1L) {
            throw new InvalidIdException("ID регистрации на рейс не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUserFlightsEntity root = QUserFlightsEntity.userFlightsEntity;

        booleanBuilder.and(root.userStatus.eq(UserFlightsStatus.CLIENT_REGISTERED_FOR_FLIGHT));
        booleanBuilder.and(root.id.eq(registrationId));
        booleanBuilder.and(root.applicationUsersEntity.id.eq(userId));

        Optional<UserFlightsEntity> userFlightsEntityOptional =
                this.userFlightsEntityRepository.findOne(booleanBuilder.getValue());
        if(userFlightsEntityOptional.isEmpty()) {
            throw new UserFlightsNotFoundException(
                    String.format(
                            "Регистраций клиента с ID[%d] на рейс с ID[%d] не было найдено!",
                            userId,
                            registrationId
                    )
            );
        }
        return userFlightsEntityOptional.get();
    }

    @Override
    public UserFlightsEntity getClientFlightRegistrationById(Long registrationId)
            throws InvalidIdException,
            UserFlightsNotFoundException
    {
        if(Objects.isNull(registrationId)) {
            throw new IllegalArgumentException("ID регистрации на рейс может быть null!");
        }
        if(registrationId < 1L) {
            throw new InvalidIdException("ID регистрации на рейс не может быть меньше 1!");
        }

        Optional<UserFlightsEntity> userFlightsEntityOptional =
                this.userFlightsEntityRepository.getUserFlightsEntityById(registrationId);

        if(userFlightsEntityOptional.isEmpty()) {
            throw new UserFlightsNotFoundException(
                    String.format("Регистрации пользователя на рейс с ID[%d] не найдено!", registrationId)
            );
        }
        return userFlightsEntityOptional.get();
    }

    @Override
    public UserFlightsEntity getUserFlightRegistrationByUserId(Long userId)
            throws InvalidIdException, UserFlightsNotFoundException {
        if(Objects.isNull(userId)) {
            throw new IllegalArgumentException("ID регистрации пользователя на рейс не может быть null!");
        }
        if(userId < 1L) {
            throw new InvalidIdException("ID регистрации пользователя на рейс не может быть меньше 1!");
        }

        Optional<UserFlightsEntity> userFlightsEntityOptional =
                this.userFlightsEntityRepository.getUserFlightsEntityByApplicationUsersEntityId(userId);

        if(userFlightsEntityOptional.isEmpty()) {
            throw new UserFlightsNotFoundException(
                    String.format("Для пользователя с ID[%d] не найдено ни одной регистрации на рейс!", userId)
            );
        }
        return userFlightsEntityOptional.get();
    }

    @Override
    public boolean checkIfAllPassengersOfFlightHaveStatus(
            Long flightId,
            UserFlightsStatus status
    )
            throws InvalidIdException
    {
        if(Objects.isNull(flightId) || Objects.isNull(status)) {
            throw new IllegalArgumentException(
                    "ID рейса и статус регистрации пользователя на рейс не могут быть null!"
            );
        }
        if(flightId < 1L) {
            throw new InvalidIdException("ID рейса не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUserFlightsEntity root = QUserFlightsEntity.userFlightsEntity;
        booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        booleanBuilder.and(root.applicationUsersEntity.userPosition.positionTitle.eq("CLIENT"));

        Iterable<UserFlightsEntity> userFlightsEntitiesIterable =
                this.userFlightsEntityRepository.findAll(booleanBuilder.getValue());
        List<UserFlightsEntity> userFlightsEntities =
                StreamSupport
                        .stream(userFlightsEntitiesIterable.spliterator(), false)
                        .collect(Collectors.toList());

        for (UserFlightsEntity userRegistration : userFlightsEntities) {
            if(!userRegistration.getUserStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkIfAllCrewMembersIsReadyForFlight(Long flightId)
            throws InvalidIdException
    {
        if(Objects.isNull(flightId)) {
            throw new IllegalArgumentException(
                    "ID рейса не может быть null!"
            );
        }
        if(flightId < 1L) {
            throw new InvalidIdException("ID рейса не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUserFlightsEntity root = QUserFlightsEntity.userFlightsEntity;
        booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        booleanBuilder.and(root.applicationUsersEntity.userPosition.positionTitle.ne("CLIENT"));

        Iterable<UserFlightsEntity> userFlightsEntitiesIterable =
                this.userFlightsEntityRepository.findAll(booleanBuilder.getValue());
        List<UserFlightsEntity> userFlightsEntities =
                StreamSupport
                        .stream(userFlightsEntitiesIterable.spliterator(), false)
                        .collect(Collectors.toList());

        for (UserFlightsEntity userRegistration : userFlightsEntities) {
            if(!userRegistration.getUserStatus().equals(UserFlightsStatus.CREW_MEMBER_READY)) {
                return false;
            }
        }
        return true;
    }
}
