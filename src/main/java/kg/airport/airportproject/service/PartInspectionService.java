package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.dto.PartStatesResponseDto;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartInspectionsEntity;
import kg.airport.airportproject.entity.attributes.PartState;
import kg.airport.airportproject.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PartInspectionService {
    @Transactional
    List<PartInspectionsResponseDto> registerPartInspections(
            AircraftsEntity aircraft,
            List<PartInspectionsRequestDto> requestDtoList
    )
            throws PartsNotFoundException,
            InvalidIdException,
            AircraftNotFoundException,
            IncompatiblePartException,
            AircraftIsNotOnServiceException, WrongAircraftException;

    List<PartInspectionsResponseDto> getPartInspectionsHistory(
            Long aircraftId,
            Long inspectionCode
    )
            throws InvalidIdException,
            PartInspectionsNotFoundException;

    List<PartInspectionsEntity> getLastAircraftInspectionEntities(Long aircraftId)
            throws InvalidIdException,
            PartInspectionsNotFoundException;

    List<PartInspectionsResponseDto> getLastAircraftInspection(Long aircraftId)
            throws InvalidIdException,
            PartInspectionsNotFoundException;

    PartState getLastAircraftInspectionResult(Long aircraftId)
            throws InvalidIdException,
            PartInspectionsNotFoundException;

    PartStatesResponseDto getAllPartStates();
}
