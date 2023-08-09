package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.dto.PartStatesResponseDto;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartInspectionsEntity;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.QPartInspectionsEntity;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.mapper.InspectionsMapper;
import kg.airport.airportproject.repository.PartInspectionsEntityRepository;
import kg.airport.airportproject.service.AircraftsService;
import kg.airport.airportproject.service.PartInspectionService;
import kg.airport.airportproject.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PartInspectionServiceImpl implements PartInspectionService {
    private static final Long MIN_INSPECTION_CODE_VALUE = 1L;

    private final PartInspectionsEntityRepository partInspectionsEntityRepository;
    private final PartsService partsService;

    private Long currentMaxInspectionCode;

    @Autowired
    public PartInspectionServiceImpl(
            PartInspectionsEntityRepository partInspectionsEntityRepository,
            PartsService partsService
    ) {
        this.partInspectionsEntityRepository = partInspectionsEntityRepository;
        this.partsService = partsService;
    }

    @PostConstruct
    private void init() {
        this.currentMaxInspectionCode = this.partInspectionsEntityRepository.getCurrentMaxInspectionCode();
        if(Objects.isNull(this.currentMaxInspectionCode)) {
            this.currentMaxInspectionCode = MIN_INSPECTION_CODE_VALUE;
        }
    }

    @Override
    public List<PartInspectionsResponseDto> registerPartInspections(
            AircraftsEntity aircraft,
            List<PartInspectionsRequestDto> requestDtoList
    )
            throws PartsNotFoundException,
            InvalidIdException, IncompatiblePartException, AircraftIsNotOnServiceException, WrongAircraftException {
        if(Objects.isNull(requestDtoList)) {
            throw new IllegalArgumentException("Список создаваемых осмотров деталей не может быть null!");
        }

        List<PartInspectionsEntity> partInspectionsEntities = new ArrayList<>();
        List<Long> partIdList = new ArrayList<>();
        Long aircraftId = requestDtoList.get(0).getAircraftId();

        for (PartInspectionsRequestDto requestDto : requestDtoList) {
            Long requestDtoAircraftId = requestDto.getAircraftId();
            if(requestDtoAircraftId < 1L) {
                throw new InvalidIdException("ID самолета не может быть меньше 1!");
            }
            if (!aircraftId.equals(requestDtoAircraftId)) {
                throw new InvalidIdException(
                        "Недопустимый ID самолета! ID самолета для всех деталей техосмотра должен быть одинаковым"
                );
            }
            partInspectionsEntities.add(InspectionsMapper.mapPartInspectionsRequestDtoToEntity(requestDto));
            partIdList.add(requestDto.getPartId());
        }
        if(!aircraft.getId().equals(aircraftId)) {
            throw new WrongAircraftException(
                    String.format("Ошибка! Обслуживание было назначено для другого самолета с ID[%d].", aircraftId)
            );
        }

        List<PartsEntity> partsEntities =
                this.partsService.getPartEntitiesByPartsIdListAndAircraftId(partIdList, aircraftId);

        if(Objects.isNull(aircraft.getServicedBy())){
            throw new AircraftIsNotOnServiceException(
                    String.format(
                            "Для обслуживания самолета с ID[%d] не было назначено ни одного инженера!",
                            aircraftId
                    )
            );
        }

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        for (int i = 0; i < partsEntities.size(); i++) {
            PartsEntity part = partsEntities.get(i);
            if(!part.getAircraftType().equals(aircraft.getAircraftType())) {
                throw new IncompatiblePartException(
                        String.format(
                                "Деталь %s [%s] не подходит к самолетам типа %s!",
                                part.getTitle(),
                                part.getPartType(),
                                aircraft.getAircraftType().toString()
                        )
                );
            }

            this.currentMaxInspectionCode += 1L;
            partInspectionsEntities.get(i)
                    .setPartsEntity(part)
                    .setRegisteredAt(localDateTimeNow)
                    .setAircraftsEntity(aircraft)
                    .setConductedBy(aircraft.getServicedBy())
                    .setInspectionCode(this.currentMaxInspectionCode);
        }

        partInspectionsEntities = this.partInspectionsEntityRepository.saveAll(partInspectionsEntities);
        return InspectionsMapper.mapToPartInspectionsResponseDtoList(partInspectionsEntities);
    }

    @Override
    public List<PartInspectionsResponseDto> getPartInspectionsHistory(Long aircraftId)
            throws InvalidIdException,
            PartInspectionsNotFoundException
    {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPartInspectionsEntity root = QPartInspectionsEntity.partInspectionsEntity;

        booleanBuilder.and(root.aircraftsEntity.id.eq(aircraftId));

        Iterable<PartInspectionsEntity> partInspectionsEntityIterable =
                this.partInspectionsEntityRepository.findAll(booleanBuilder.getValue());
        List<PartInspectionsResponseDto> partInspectionsResponseDtoList =
                StreamSupport
                        .stream(partInspectionsEntityIterable.spliterator(), false)
                        .map(InspectionsMapper::mapToPartInspectionsResponseDto)
                        .collect(Collectors.toList());

        if(partInspectionsResponseDtoList.isEmpty()) {
            throw new PartInspectionsNotFoundException(
                    String.format(
                            "По ID[%d] самолета технических осмотров не найдено!",
                            aircraftId
                    )
            );
        }
        return partInspectionsResponseDtoList;
    }

    @Override
    public List<PartInspectionsEntity> getLastAircraftInspectionEntities(Long aircraftId) throws InvalidIdException, PartInspectionsNotFoundException {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета не может быть меньше 1!");
        }

        List<PartInspectionsEntity> lastInspection =
                this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(aircraftId);
        if(lastInspection.isEmpty()) {
            throw new PartInspectionsNotFoundException(
                    String.format("Для самолета с ID[%d] не найдено ни одного техосмотра!", aircraftId)
            );
        }
        return lastInspection;
    }

    @Override
    public PartState getLastAircraftInspectionResult(Long aircraftId)
            throws InvalidIdException,
            PartInspectionsNotFoundException
    {
        List<PartInspectionsEntity> partInspectionsEntityList =
                this.getLastAircraftInspectionEntities(aircraftId);

        for (PartInspectionsEntity partInspection:
             partInspectionsEntityList) {
            if(partInspection.getPartState().equals(PartState.NEEDS_FIXING)) {
                return PartState.NEEDS_FIXING;
            }
        }
        return PartState.CORRECT;
    }

    @Override
    public PartStatesResponseDto getAllPartStates() {
        return InspectionsMapper.mapToPartStatesResponseDto(List.of(PartState.values()));
    }
}
