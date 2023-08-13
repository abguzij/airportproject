package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.AircraftsMapper;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.response.StatusChangedResponse;
import kg.airport.airportproject.service.*;
import kg.airport.airportproject.utils.UserRolesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AircraftsServiceImpl implements AircraftsService {
    private final AircraftSeatsService aircraftSeatsService;
    private final PartsService partsService;
    private final ApplicationUserService applicationUserService;
    private final PartInspectionService partInspectionService;
    private final AircraftsEntityRepository aircraftsEntityRepository;

    @Autowired
    public AircraftsServiceImpl(
            AircraftSeatsService aircraftSeatsService,
            PartsService partsService,
            ApplicationUserService applicationUserService,
            PartInspectionService partInspectionService,
            AircraftsEntityRepository aircraftsEntityRepository
    ) {
        this.aircraftSeatsService = aircraftSeatsService;
        this.partsService = partsService;
        this.applicationUserService = applicationUserService;
        this.partInspectionService = partInspectionService;
        this.aircraftsEntityRepository = aircraftsEntityRepository;
    }

    @Override
    public AircraftResponseDto registerNewAircraft(AircraftRequestDto requestDto)
            throws PartsNotFoundException,
            IncompatiblePartException,
            InvalidIdException
    {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемый самолет не может быть null!");
        }
        AircraftsEntity aircraft = AircraftsMapper.mapAircraftRequestDtoToEntity(requestDto);

        List<AircraftSeatsEntity> aircraftSeatsEntities =
                this.aircraftSeatsService.generateAircraftSeats(
                        requestDto.getNumberOfRows(),
                        requestDto.getNumberOfSeatsInRow()
                );
        aircraft.setAircraftSeatsEntityList(aircraftSeatsEntities);
        for (AircraftSeatsEntity aircraftSeatsEntity : aircraftSeatsEntities) {
            aircraftSeatsEntity.setAircraftsEntity(aircraft);
        }

        List<PartsEntity> partsEntities = partsService.getPartEntitiesByPartsIdListAndAircraftType(
                requestDto.getPartIdList(),
                aircraft.getAircraftType()
        );
        aircraft.setPartsEntities(partsEntities);
        for (PartsEntity part : partsEntities) {
            part.getAircraftsEntities().add(aircraft);
        }

        aircraft.setStatus(AircraftStatus.NEEDS_INSPECTION);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return AircraftsMapper.mapToAircraftResponseDto(aircraft);
    }

    @Override
    public StatusChangedResponse refuelAircraft(Long aircraftId)
            throws InvalidIdException,
            AircraftNotFoundException,
            StatusChangeException,
            WrongEngineerException
    {
        AircraftsEntity aircraftsEntity = this.findAircraftsEntityById(aircraftId);
        if(!aircraftsEntity.getStatus().equals(AircraftStatus.ON_REFUELING)) {
            throw new StatusChangeException(
                    String.format("Ошибка! Заправка для самолета с ID[%d] еще не была назначена!", aircraftId)
            );
        }

        ApplicationUsersEntity engineer =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!engineer.getId().equals(aircraftsEntity.getServicedBy().getId())) {
            throw new WrongEngineerException(
                    String.format(
                            "Ошибка! Заправка самолета с ID[%d] была назначена другому инженеру!",
                            aircraftsEntity.getId()
                    )
            );
        }

        aircraftsEntity.getServicedBy().setServicedAircraft(null);
        aircraftsEntity.setServicedBy(null);
        aircraftsEntity.setStatus(AircraftStatus.REFUELED);

        aircraftsEntity = this.aircraftsEntityRepository.save(aircraftsEntity);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Самолет успешно заправлен! Текущий статус самолета [%s]!",
                                aircraftsEntity.getStatus()
                        )
                );
    }

    @Override
    public StatusChangedResponse assignAircraftInspection(Long aircraftId, Long engineersId)
            throws AircraftNotFoundException,
            InvalidIdException,
            ApplicationUserNotFoundException,
            StatusChangeException,
            EngineerIsBusyException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftStatus.NEEDS_INSPECTION)) {
            throw new StatusChangeException(
                    "Для назначения техосмотра самолет должен быть передан на техосмотр диспетчером!"
            );
        }

        ApplicationUsersEntity engineer = this.applicationUserService.getEngineerEntityById(engineersId);
        if(Objects.nonNull(engineer.getServicedAircraft())) {
            throw new EngineerIsBusyException(
                    String.format(
                            "Невозможно назначить инженера с ID[%d] на техосмотр." +
                            " В данный момент инженер обслуживает другой самолет!",
                            engineersId
                    )
            );
        }

        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        aircraft.setStatus(AircraftStatus.ON_INSPECTION);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Самолет передан инженеру на техосмотр! Текущий статус самолета [%s]",
                                aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse confirmAircraftServiceability(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            PartInspectionsNotFoundException,
            StatusChangeException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftStatus.INSPECTED)) {
            throw new StatusChangeException(
                    "Чтобы подтвердить исправность самолета самолет должен быть осмотрен инженером!"
            );
        }

        if(!this.partInspectionService.getLastAircraftInspectionResult(aircraftId).equals(PartState.CORRECT)) {
            throw new StatusChangeException(
                    String.format(
                            "Чтобы подтвердить исправность самолета все детали самолета должны быть исправны!" +
                                    "Результат последнего техосмотра: %s",
                            PartState.NEEDS_FIXING.toString()
                    )
            );
        }

        aircraft.setStatus(AircraftStatus.SERVICEABLE);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Исправность самолета подтверждена! Текущий статус самолета: [%s]",
                                aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse assignAircraftRepairs(Long aircraftId, Long engineersId)
            throws EngineerIsBusyException,
            StatusChangeException,
            AircraftNotFoundException,
            InvalidIdException,
            ApplicationUserNotFoundException,
            PartInspectionsNotFoundException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftStatus.INSPECTED)) {
            throw new StatusChangeException(
                    "Чтобы отправить самолет на ремонт самолет должен быть осмотрен инженером!"
            );
        }
        if(!this.partInspectionService.getLastAircraftInspectionResult(aircraftId).equals(PartState.NEEDS_FIXING)) {
            throw new StatusChangeException(
                    String.format(
                            "Чтобы отправить самолет на ремонт хотя бы одна деталь самолета должны быть неисправна!" +
                                    "Результат последнего техосмотра: %s",
                            PartState.CORRECT.toString()
                    )
            );
        }

        ApplicationUsersEntity engineer = this.applicationUserService.getEngineerEntityById(engineersId);
        if(Objects.nonNull(engineer.getServicedAircraft())) {
            throw new EngineerIsBusyException(
                    String.format(
                            "Невозможно назначить инженера с ID[%d] на ремонт самолета." +
                                    " В данный момент инженер обслуживает другой самолет!",
                            engineersId
                    )
            );
        }

        aircraft.setStatus(AircraftStatus.ON_REPAIRS);
        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Самолет отправлен на ремонт! Текущий статус самолета:[%s]"
                                , aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse sendAircraftToRegistrationConfirmation(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftStatus.SERVICEABLE)) {
            throw new StatusChangeException(
                    "Чтобы отправить самолет на подверждение регистрации его" +
                            " техосмотр должен быть подтвержден главным инженером!"
            );
        }

        aircraft.setStatus(AircraftStatus.REGISTRATION_PENDING_CONFIRMATION);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Самолет отправлен на подтверждение регистрации! Текущий статус самолета:[%s]"
                                , aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse confirmAircraftRegistration(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftStatus.REGISTRATION_PENDING_CONFIRMATION)) {
            throw new StatusChangeException(
                    "Для подтверждения регистрации самолета он должен быть направлен главному диспетчеру диспетчером"
            );
        }

        aircraft.setStatus(AircraftStatus.AVAILABLE);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Регистрация самолета подтверждена! Текущий статус самолета: [%s]",
                                aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public StatusChangedResponse assignAircraftRefueling(Long aircraftId, Long engineerId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException,
            ApplicationUserNotFoundException,
            EngineerIsBusyException
    {
        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        List<FlightsEntity> aircraftsFlights = aircraft.getFlightsEntities();
        if(!aircraftsFlights.get(aircraftsFlights.size() - 1).getStatus().equals(FlightStatus.DEPARTURE_INITIATED)) {
            throw new StatusChangeException(
                    "Чтобы отправить самолет на заправку отпрака рейса самолета должна быть инициирована!"
            );
        }

        ApplicationUsersEntity engineer = this.applicationUserService.getEngineerEntityById(engineerId);
        if(Objects.nonNull(engineer.getServicedAircraft())) {
            throw new EngineerIsBusyException(
                    String.format(
                            "Невозможно назначить инженера с ID[%d] на заправку." +
                                    " В данный момент инженер обслуживает другой самолет!",
                            engineerId
                    )
            );
        }

        aircraft.setStatus(AircraftStatus.ON_REFUELING);

        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        aircraft = this.aircraftsEntityRepository.save(aircraft);
        return new StatusChangedResponse()
                .setHttpStatus(HttpStatus.OK)
                .setMessage(
                        String.format(
                                "Самолет отправлен на заправку! Текущий статус самолета:[%s]"
                                , aircraft.getStatus().toString()
                        )
                );
    }

    @Override
    public List<PartInspectionsResponseDto> inspectAircraft(
            Long aircraftId,
            List<PartInspectionsRequestDto> partInspectionsRequestDtoList
    )
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException,
            WrongEngineerException,
            AircraftIsNotOnServiceException,
            PartsNotFoundException,
            WrongAircraftException,
            IncompatiblePartException
    {
        if(partInspectionsRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("Список осмотров деталей не может быть null!");
        }

        AircraftsEntity aircraft = this.findAircraftsEntityById(aircraftId);
        if(
                !aircraft.getStatus().equals(AircraftStatus.ON_INSPECTION) &&
                        !aircraft.getStatus().equals(AircraftStatus.ON_REPAIRS)
        ) {
            throw new StatusChangeException(
                    "Для проведения техосмотра самолета он должен быть назначен главным инжененром!"
            );
        }

        ApplicationUsersEntity engineer =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!engineer.getId().equals(aircraft.getServicedBy().getId())) {
            throw new WrongEngineerException(
                    String.format(
                            "Ошибка! Технический осмотр самолета с ID[%d] был назначен другому инженеру!",
                            aircraft.getId()
                    )
            );
        }

        List<PartInspectionsResponseDto> partInspectionsResponseDtoList =
                this.partInspectionService.registerPartInspections(aircraft, partInspectionsRequestDtoList);

        aircraft.getServicedBy().setServicedAircraft(null);
        aircraft.setServicedBy(null);
        aircraft.setStatus(AircraftStatus.INSPECTED);

        this.aircraftsEntityRepository.save(aircraft);
        return partInspectionsResponseDtoList;
    }

    @Override
    public List<AircraftResponseDto> getAllAircrafts(
            AircraftType aircraftType,
            AircraftStatus aircraftStatus,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildCommonAircraftsSearchPredicate(aircraftType, registeredAfter, registeredBefore)
        );
        QAircraftsEntity root = QAircraftsEntity.aircraftsEntity;

        if(Objects.nonNull(aircraftStatus)) {
            booleanBuilder.and(root.status.eq(aircraftStatus));
        }

        return this.findAircraftsByBuiltPredicate(booleanBuilder.getValue());
    }

    @Override
    public List<AircraftResponseDto> getAircraftsForRepairs(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildCommonAircraftsSearchPredicate(aircraftType, registeredAfter, registeredBefore)
        );
        QAircraftsEntity root = QAircraftsEntity.aircraftsEntity;
        booleanBuilder.and(root.status.eq(AircraftStatus.ON_REPAIRS));
        if(UserRolesUtils.checkIfUserRolesListContainsSuchUserRoleTitle(
                authorizedUser.getUserRolesEntityList(),
                "ENGINEER"
        )) {
            booleanBuilder.and(root.servicedBy.id.eq(authorizedUser.getId()));
        }
        return this.findAircraftsByBuiltPredicate(booleanBuilder.getValue());
    }

    @Override
    public List<AircraftResponseDto> getNewAircrafts(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildCommonAircraftsSearchPredicate(aircraftType, registeredAfter, registeredBefore)
        );
        QAircraftsEntity root = QAircraftsEntity.aircraftsEntity;
        booleanBuilder.and(root.status.eq(AircraftStatus.NEEDS_INSPECTION));
        if(UserRolesUtils.checkIfUserRolesListContainsSuchUserRoleTitle(
                authorizedUser.getUserRolesEntityList(),
                "ENGINEER"
        )) {
            booleanBuilder.and(root.servicedBy.id.eq(authorizedUser.getId()));
        }
        return this.findAircraftsByBuiltPredicate(booleanBuilder.getValue());
    }

    @Override
    public List<AircraftResponseDto> getAircraftsForRefueling(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException
    {
        ApplicationUsersEntity authorizedUser =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildCommonAircraftsSearchPredicate(aircraftType, registeredAfter, registeredBefore)
        );
        QAircraftsEntity root = QAircraftsEntity.aircraftsEntity;
        booleanBuilder.and(root.status.eq(AircraftStatus.ON_REFUELING));
        if(UserRolesUtils.checkIfUserRolesListContainsSuchUserRoleTitle(
                authorizedUser.getUserRolesEntityList(),
                "ENGINEER"
        )) {
            booleanBuilder.and(root.servicedBy.id.eq(authorizedUser.getId()));
        }
        return this.findAircraftsByBuiltPredicate(booleanBuilder.getValue());
    }

    @Override
    public AircraftsEntity findAircraftsEntityById(Long aircraftId)
            throws InvalidIdException,
            AircraftNotFoundException
    {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета не может быть меньше 1!");
        }

        Optional<AircraftsEntity> aircraftsEntityOptional =
                this.aircraftsEntityRepository.getAircraftsEntityById(aircraftId);
        if(aircraftsEntityOptional.isEmpty()) {
            throw new AircraftNotFoundException(String.format("Самолета с ID %d не найдено!", aircraftId));
        }
        return aircraftsEntityOptional.get();
    }

    @Override
    public AircraftTypesResponseDto getAllAircraftTypes() {
        return AircraftsMapper.mapToAircraftTypesResponseDto(List.of(AircraftType.values()));
    }

    private Predicate buildCommonAircraftsSearchPredicate(
            AircraftType aircraftType,
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore
    )
            throws IncorrectDateFiltersException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QAircraftsEntity root = QAircraftsEntity.aircraftsEntity;

        if(Objects.nonNull(aircraftType)) {
            booleanBuilder.and(root.aircraftType.eq(aircraftType));
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

    private List<AircraftResponseDto> findAircraftsByBuiltPredicate(Predicate builtPredicate)
            throws AircraftNotFoundException
    {
        Iterable<AircraftsEntity> aircraftsEntityIterable =
                this.aircraftsEntityRepository.findAll(builtPredicate);
        List<AircraftResponseDto> aircraftResponseDtoList =
                StreamSupport
                        .stream(aircraftsEntityIterable.spliterator(), false)
                        .map(AircraftsMapper::mapToAircraftResponseDto)
                        .collect(Collectors.toList());

        if(aircraftResponseDtoList.isEmpty()) {
            throw new AircraftNotFoundException("Самолетов по заданным параметрам не найдено!");
        }
        return aircraftResponseDtoList;
    }
}
