package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.QPartsEntity;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.exception.IncompatiblePartException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartsNotFoundException;
import kg.airport.airportproject.mapper.PartsMapper;
import kg.airport.airportproject.repository.PartsEntityRepository;
import kg.airport.airportproject.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PartsServiceImpl implements PartsService {
    private final PartsEntityRepository partsEntityRepository;

    @Autowired
    public PartsServiceImpl(
            PartsEntityRepository partsEntityRepository
    ) {
        this.partsEntityRepository = partsEntityRepository;
    }

    @Override
    public PartResponseDto registerNewPart(PartRequestDto requestDto) {
        if(Objects.isNull(requestDto)) {
            throw new IllegalArgumentException("Создаваемая деталь не может быть null");
        }

        PartsEntity partsEntity = PartsMapper.mapPartRequestDtoToEntity(requestDto);
        partsEntity = this.partsEntityRepository.save(partsEntity);
        return PartsMapper.mapToPartResponseDto(partsEntity);
    }

    @Override
    public List<PartResponseDto> registerNewParts(List<PartRequestDto> partRequestDtoList) {
        if(partRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("Список создаваемых деталей не может быть пустым!");
        }

        List<PartsEntity> partsEntities = new ArrayList<>();
        for (PartRequestDto partRequestDto : partRequestDtoList) {
            // TODO: 30.07.2023 Здесь валидировать дто
            partsEntities.add(PartsMapper.mapPartRequestDtoToEntity(partRequestDto));
        }
        partsEntities = this.partsEntityRepository.saveAll(partsEntities);
        return PartsMapper.mapToPartResponseDtoList(partsEntities);
    }

    @Override
    public List<PartResponseDto> getAllParts(
            AircraftType aircraftType,
            PartType partType,
            Long aircraftId,
            Long partId,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws PartsNotFoundException,
            InvalidIdException
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPartsEntity root = QPartsEntity.partsEntity;

        if(Objects.nonNull(aircraftType)) {
            booleanBuilder.and(root.aircraftType.eq(aircraftType));
        }
        if(Objects.nonNull(partType)) {
            booleanBuilder.and(root.partType.eq(partType));
        }
        if(Objects.nonNull(aircraftId)) {
            if(aircraftId < 1L) {
                throw new InvalidIdException("ID самолета не может быть меньше 1!");
            }
            booleanBuilder.and(root.aircraftsEntities.any().id.eq(aircraftId));
        }
        if(Objects.nonNull(partId)) {
            if(partId < 1L) {
                throw new InvalidIdException("ID детали не может быть меньше 1!");
            }
            booleanBuilder.and(root.id.eq(partId));
        }
        if(Objects.nonNull(registeredBefore)) {
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }
        if(Objects.nonNull(registeredBefore)) {
            booleanBuilder.and(root.registeredAt.loe(registeredBefore));
        }

        Iterable<PartsEntity> partsEntityIterable = this.partsEntityRepository.findAll(booleanBuilder.getValue());
        List<PartResponseDto> partResponseDtoList =
                StreamSupport
                        .stream(partsEntityIterable.spliterator(), false)
                        .map(PartsMapper::mapToPartResponseDto)
                        .collect(Collectors.toList());

        if(partResponseDtoList.isEmpty()) {
            throw new PartsNotFoundException("Деталей по заданным параметрам не найдено!");
        }
        return partResponseDtoList;
    }

    @Override
    public List<PartsEntity> getPartEntitiesByPartsIdListAndAircraftType(
            List<Long> partsIdList,
            AircraftType aircraftType
    )
            throws PartsNotFoundException,
            IncompatiblePartException,
            InvalidIdException
    {
        if(Objects.isNull(partsIdList) || partsIdList.isEmpty()) {
            throw new IllegalArgumentException("Список ID деталей не может быть null или пустым!");
        }
        if(Objects.isNull(aircraftType)) {
            throw new IllegalArgumentException("Тип самолета не может быть null!");
        }
        for (Long partId : partsIdList) {
            if(partId < 1) {
                throw new InvalidIdException("ID детали не может быть меньше 1!");
            }
        }

        List<PartsEntity> partsEntities = this.partsEntityRepository.getPartsEntitiesByIdIn(partsIdList);
        if (partsEntities.isEmpty()) {
            throw new PartsNotFoundException("Деталей по заданным ID не найдено!");
        }

        for (PartsEntity part : partsEntities) {
            if(!part.getAircraftType().equals(aircraftType)) {
                throw new IncompatiblePartException(
                        String.format(
                                "Деталь %s [%s] не подходит к самолетам типа %s!",
                                part.getTitle(),
                                part.getPartType(),
                                aircraftType.toString()
                        )
                );
            }
        }
        return partsEntities;
    }

    @Override
    public List<PartsEntity> getPartEntitiesByPartsIdListAndAircraftId(
            List<Long> partsIdList,
            Long aircraftId
    )
            throws InvalidIdException,
            PartsNotFoundException
    {
        if(Objects.isNull(partsIdList) || partsIdList.isEmpty()) {
            throw new IllegalArgumentException("Список ID деталей не может быть null или пустым!");
        }
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета не может быть меньше 1!");
        }
        for (Long partId : partsIdList) {
            if(partId < 1) {
                throw new InvalidIdException("ID детали не может быть меньше 1!");
            }
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPartsEntity root = QPartsEntity.partsEntity;

        booleanBuilder.and(root.id.in(partsIdList));
        booleanBuilder.and(root.aircraftsEntities.any().id.eq(aircraftId));

        Iterable<PartsEntity> partsEntityIterable =
                this.partsEntityRepository.findAll(booleanBuilder.getValue());
        List<PartsEntity> partsEntityList =
                StreamSupport
                        .stream(partsEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        if(partsEntityList.isEmpty()) {
            throw new PartsNotFoundException("Деталей самолета по заданным ID деталей и ID самолета не найдено!");
        }
        return partsEntityList;
    }

    @Override
    public PartTypesResponseDto getAllPartTypes() {
        return PartsMapper.mapToPartTypesResponseDto(List.of(PartType.values()));
    }
}
