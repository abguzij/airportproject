package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.exception.IncompatiblePartException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PartsService {
    @Transactional
    PartResponseDto registerNewPart(PartRequestDto requestDto);

    @Transactional
    List<PartResponseDto> registerNewParts(List<PartRequestDto> partRequestDtoList);

    List<PartResponseDto> getAllParts(
            AircraftType aircraftType,
            PartType partType,
            Long aircraftId,
            Long partId,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws PartsNotFoundException,
            InvalidIdException;

    List<PartsEntity> getPartEntitiesByPartsIdListAndAircraftType(
            List<Long> partsIdList,
            AircraftType aircraftType
    )
            throws PartsNotFoundException,
            IncompatiblePartException, InvalidIdException;

    List<PartsEntity> getPartEntitiesByPartsIdListAndAircraftId(List<Long> partsIdList, Long aircraftId)
            throws InvalidIdException, PartsNotFoundException;

    PartTypesResponseDto getAllPartTypes();
}
