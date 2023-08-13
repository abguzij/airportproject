package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.UserPositionNotExists;
import kg.airport.airportproject.mapper.ApplicationUsersMapper;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {
    private final ApplicationUsersEntityRepository applicationUsersEntityRepository;
    private final UserPositionsEntityRepository userPositionsEntityRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Long MIN_USER_ID_VALUE = 1L;

    @Autowired
    public ApplicationUserServiceImpl(
            ApplicationUsersEntityRepository applicationUsersEntityRepository,
            UserPositionsEntityRepository userPositionsEntityRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.applicationUsersEntityRepository = applicationUsersEntityRepository;
        this.userPositionsEntityRepository = userPositionsEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApplicationUserResponseDto deleteCurrentAccount() {
        ApplicationUsersEntity user =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setEnabled(Boolean.FALSE);
        return ApplicationUsersMapper.mapToApplicationUserResponseDto(
                this.applicationUsersEntityRepository.save(user)
        );
    }

    @Override
    public ApplicationUserResponseDto deleteAccountById(Long userId)
            throws ApplicationUserNotFoundException,
            InvalidIdException
    {
        if(Objects.isNull(userId)) {
            throw new IllegalArgumentException("ID пользователя не может быть null!");
        }
        if(userId < MIN_USER_ID_VALUE) {
            throw new InvalidIdException("ID пользователя не может быть меньше 1!");
        }

        Optional<ApplicationUsersEntity> applicationUsersEntityOptional =
                this.applicationUsersEntityRepository.getApplicationUsersEntityById(userId);
        if(applicationUsersEntityOptional.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    String.format("Пользователь с ID %d не найден!", userId)
            );
        }

        ApplicationUsersEntity user = applicationUsersEntityOptional.get();
        user.setEnabled(Boolean.FALSE);

        return ApplicationUsersMapper.mapToApplicationUserResponseDto(
                this.applicationUsersEntityRepository.save(user)
        );
    }

    @Override
    public ApplicationUserResponseDto updateCurrentUsersInformation(
            ApplicationUserRequestDto applicationUserRequestDto
    ) {
        // TODO: 29.07.2023 Добавить валидацию дто
        ApplicationUsersEntity user =
                (ApplicationUsersEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user
                .setUsername(applicationUserRequestDto.getUsername())
                .setPassword(this.passwordEncoder.encode(applicationUserRequestDto.getPassword()))
                .setFullName(applicationUserRequestDto.getFullName());

        user = this.applicationUsersEntityRepository.save(user);
        return ApplicationUsersMapper.mapToApplicationUserResponseDto(user);
    }

    @Override
    public ApplicationUserResponseDto updateUsersInformation(
            ApplicationUserRequestDto applicationUserRequestDto,
            Long userId
    )
            throws ApplicationUserNotFoundException,
            UserPositionNotExists,
            InvalidIdException
    {
        if(Objects.isNull(userId)) {
            throw new IllegalArgumentException("ID пользователя не может быть null!");
        }
        if(userId < MIN_USER_ID_VALUE) {
            throw new InvalidIdException("ID пользователя не может быть меньше 1!");
        }

        // TODO: 29.07.2023 Добавить валидацию дто
        Optional<ApplicationUsersEntity> applicationUsersEntityOptional =
                this.applicationUsersEntityRepository.getApplicationUsersEntityById(userId);
        if(applicationUsersEntityOptional.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    String.format("Пользователь с ID %d не найден!", userId)
            );
        }
        ApplicationUsersEntity usersEntity = applicationUsersEntityOptional.get();

        Long positionId = applicationUserRequestDto.getPositionId();
        Optional<UserPositionsEntity> userPositionOptional =
                this.userPositionsEntityRepository.getUserPositionsEntityById(positionId);
        if(userPositionOptional.isEmpty()) {
            throw new UserPositionNotExists(
                    String.format("Позиции пользователя с ID %d не существует в системе!", positionId)
            );
        }

        usersEntity
                .setUserPosition(userPositionOptional.get())
                .setUsername(applicationUserRequestDto.getUsername())
                .setFullName(applicationUserRequestDto.getFullName())
                .setPassword(this.passwordEncoder.encode(applicationUserRequestDto.getPassword()));

        this.applicationUsersEntityRepository.save(usersEntity);
        return ApplicationUsersMapper.mapToApplicationUserResponseDto(usersEntity);
    }

    @Override
    public List<ApplicationUserResponseDto> getAllClients(
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter,
            Boolean isDeleted
    )
            throws ApplicationUserNotFoundException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildUsersCommonSearchPredicate(registeredBefore, registeredAfter, isDeleted)
        );
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        booleanBuilder.and(root.userPosition.positionTitle.eq("CLIENT"));

        Iterable<ApplicationUsersEntity> applicationUsersEntityIterable =
                this.applicationUsersEntityRepository.findAll(booleanBuilder.getValue());
        List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                StreamSupport
                        .stream(applicationUsersEntityIterable.spliterator(), false)
                        .map(ApplicationUsersMapper::mapToApplicationUserResponseDto)
                        .collect(Collectors.toList());

        if(applicationUserResponseDtoList.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    "Клиентов по указанным параметрам не найдено!"
            );
        }
        return applicationUserResponseDtoList;
    }

    @Override
    public List<ApplicationUserResponseDto> getAllEmployees(
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter,
            Boolean isFired,
            List<String> userPositions
    )
            throws ApplicationUserNotFoundException {
//        while (userPositions.contains("CLIENT")) {
//            userPositions.remove("CLIENT");
//        }

        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.buildUsersCommonSearchPredicate(registeredBefore, registeredAfter, isFired)
        );
        booleanBuilder.and(this.buildInEmployeePositionsSearchPredicate(userPositions));

        Iterable<ApplicationUsersEntity> applicationUsersEntityIterable =
                this.applicationUsersEntityRepository.findAll(booleanBuilder.getValue());
        List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                StreamSupport
                        .stream(applicationUsersEntityIterable.spliterator(), false)
                        .map(ApplicationUsersMapper::mapToApplicationUserResponseDto)
                        .collect(Collectors.toList());

        if(applicationUserResponseDtoList.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    "Сотрудников аэропорта по указанным параметрам не найдено!"
            );
        }
        return applicationUserResponseDtoList;
    }

    @Override
    public List<ApplicationUserResponseDto> getAllFreeCrewMembers()
            throws ApplicationUserNotFoundException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        booleanBuilder.and(
                this.buildInUserRolesSearchPredicate(List.of("PILOT", "CHIEF_STEWARD", "STEWARD"))
        );
        booleanBuilder.and(
                root
                        .userFlightsRegistartionsList.any().userStatus.eq(UserFlightsStatus.ARRIVED)
                        .or(root.userFlightsRegistartionsList.isEmpty())
        );
//        booleanBuilder.and(
//                root
//                        .userFlightsRegistartionsList.any().flightsEntity.status.eq(FlightStatus.ARRIVED)
//                        .or(root.userFlightsRegistartionsList.isEmpty())
//        );

        Iterable<ApplicationUsersEntity> applicationUsersEntityIterable =
                this.applicationUsersEntityRepository.findAll(booleanBuilder.getValue());
        List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                StreamSupport
                        .stream(applicationUsersEntityIterable.spliterator(), false)
                        .map(ApplicationUsersMapper::mapToApplicationUserResponseDto)
                        .collect(Collectors.toList());

        if(applicationUserResponseDtoList.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    "Свободных членов экипажа не найдено!"
            );
        }
        return applicationUserResponseDtoList;
    }

    @Override
    public List<ApplicationUserResponseDto> getAllFreeEngineers()
            throws ApplicationUserNotFoundException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        booleanBuilder.and(
                this.buildInUserRolesSearchPredicate(List.of("ENGINEER"))
        );

        Iterable<ApplicationUsersEntity> applicationUsersEntityIterable =
                this.applicationUsersEntityRepository.findAll(booleanBuilder.getValue());

        List<ApplicationUsersEntity> applicationUsersEntityList =
                StreamSupport
                        .stream(applicationUsersEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        applicationUsersEntityList.removeIf(user -> Objects.nonNull(user.getServicedAircraft()));

        if(applicationUsersEntityList.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    "Свободных инженеров не найдено!"
            );
        }
        return applicationUsersEntityList
                .stream()
                .map(ApplicationUsersMapper::mapToApplicationUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationUsersEntity> getUserEntitiesByIdList(List<Long> userIdList)
            throws ApplicationUserNotFoundException,
            InvalidIdException
    {
        if(Objects.isNull(userIdList) || userIdList.isEmpty()) {
            throw new IllegalArgumentException("Список ID пользователей не может быть null или пустым!");
        }
        for (Long userId : userIdList) {
            if (userId < 1) {
                throw new InvalidIdException("ID пользователя не может быть меньше 1!");
            }
        }

        List<ApplicationUsersEntity> applicationUsersEntities =
                this.applicationUsersEntityRepository.getApplicationUsersEntitiesByIdIn(userIdList);

        if(applicationUsersEntities.isEmpty()) {
            throw new ApplicationUserNotFoundException("Пользователей по заданному списку ID не найдено!");
        }
        return applicationUsersEntities;
    }

    @Override
    public ApplicationUsersEntity getEngineerEntityById(Long engineerId)
            throws InvalidIdException,
            ApplicationUserNotFoundException
    {
        if(Objects.isNull(engineerId)) {
            throw new IllegalArgumentException("ID инженера не может быть null!");
        }
        if(engineerId < 1L) {
            throw new InvalidIdException("ID инженера не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        booleanBuilder.and(root.userRolesEntityList.any().roleTitle.eq("ENGINEER"));
        booleanBuilder.and(root.id.eq(engineerId));

        Optional<ApplicationUsersEntity> applicationUsersEntityOptional =
                this.applicationUsersEntityRepository.findOne(booleanBuilder.getValue());
        if(applicationUsersEntityOptional.isEmpty()) {
            throw new ApplicationUserNotFoundException(
                    String.format("Инженера с ID[%d] не найдено!", engineerId)
            );
        }
        return applicationUsersEntityOptional.get();
    }

    private Predicate buildUsersCommonSearchPredicate(
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter,
            Boolean isDeleted
    ) {
        // TODO: 30.07.2023 добавить проверку на бефор после афтер
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        if(Objects.nonNull(registeredBefore)) {
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }
        if(Objects.nonNull(registeredBefore)) {
            booleanBuilder.and(root.registeredAt.loe(registeredBefore));
        }
        if(Objects.nonNull(isDeleted)) {
            booleanBuilder.and(root.isEnabled.eq(isDeleted));
        }

        return booleanBuilder;
    }

    private Predicate buildInEmployeePositionsSearchPredicate(List<String> employeePositions) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        booleanBuilder.and(root.userPosition.positionTitle.ne("CLIENT"));
        if(Objects.nonNull(employeePositions) && !employeePositions.isEmpty()) {
            booleanBuilder.and(root.userPosition.positionTitle.in(employeePositions));
        }

        return booleanBuilder.getValue();
    }

    private Predicate buildInUserRolesSearchPredicate(List<String> roleTitles) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApplicationUsersEntity root = QApplicationUsersEntity.applicationUsersEntity;

        if((!roleTitles.isEmpty())) {
            booleanBuilder.and(root.userRolesEntityList.any().roleTitle.in(roleTitles));
        }

        return booleanBuilder.getValue();
    }
}
