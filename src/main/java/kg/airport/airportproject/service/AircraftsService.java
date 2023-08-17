package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AircraftsService {
    @Transactional
    AircraftResponseDto registerNewAircraft(AircraftRequestDto requestDto)
            throws PartsNotFoundException,
            IncompatiblePartException,
            InvalidIdException, InvalidAircraftTypeException, InvalidAircraftTitleException;

    @Transactional
    StatusChangedResponse refuelAircraft(Long aircraftId)
            throws InvalidIdException,
            AircraftNotFoundException,
            StatusChangeException,
            WrongEngineerException;

    @Transactional
    StatusChangedResponse assignAircraftInspection(Long aircraftId, Long engineersId)
            throws AircraftNotFoundException,
            InvalidIdException,
            ApplicationUserNotFoundException,
            StatusChangeException,
            EngineerIsBusyException;

    @Transactional
    StatusChangedResponse confirmAircraftServiceability(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            PartInspectionsNotFoundException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse assignAircraftRepairs(Long aircraftId, Long engineersId)
            throws EngineerIsBusyException,
            StatusChangeException,
            AircraftNotFoundException,
            InvalidIdException,
            ApplicationUserNotFoundException,
            PartInspectionsNotFoundException;

    @Transactional
    StatusChangedResponse sendAircraftToRegistrationConfirmation(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmAircraftRegistration(Long aircraftId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse assignAircraftRefueling(Long aircraftId, Long engineerId)
            throws AircraftNotFoundException,
            InvalidIdException,
            StatusChangeException,
            ApplicationUserNotFoundException,
            EngineerIsBusyException;

    @Transactional
    List<PartInspectionsResponseDto> inspectAircraft(
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
            IncompatiblePartException;

    List<AircraftResponseDto> getAllAircrafts(
            AircraftType aircraftType,
            AircraftStatus aircraftStatus,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException;

    List<AircraftResponseDto> getAircraftsForRepairs(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException;

    List<AircraftResponseDto> getNewAircrafts(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException;

    List<AircraftResponseDto> getAircraftsForRefueling(
            AircraftType aircraftType,
            LocalDateTime registeredBefore,
            LocalDateTime registeredAfter
    )
            throws IncorrectDateFiltersException,
            AircraftNotFoundException;

    AircraftsEntity findAircraftsEntityById(Long aircraftId)
            throws InvalidIdException,
            AircraftNotFoundException;

    AircraftTypesResponseDto getAllAircraftTypes();

}
