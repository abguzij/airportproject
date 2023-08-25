package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightResponseDto;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.exception.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightsService {
    @Transactional
    FlightResponseDto registerNewFlight(FlightRequestDto requestDto)
            throws AircraftNotFoundException,
            InvalidIdException,
            UnavailableAircraftException, InvalidDestinationException;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    FlightsEntity updateNumberOfRemainingTickets(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException;

    @Transactional
    void informThatAllCrewMembersIsReadyForFlight(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException,
            StatusChangeException;

    @Transactional
    void informThatAllClientsAreChecked(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    void informThatAllClientsAreBriefed(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    void informThatAllClientsFoodIsDistributed(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmFlightRegistration(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse initiateFlightDeparturePreparations(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse initiateCrewPreparation(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmAircraftRefueling(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException,
            AircraftNotReadyException;

    @Transactional
    StatusChangedResponse assignBriefing(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmClientReadiness(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse initiateDeparture(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmDeparture(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse startFlight(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse assignFoodDistribution(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse requestLanding(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse assignLanding(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse confirmLanding(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    StatusChangedResponse endFlight(Long flightId)
            throws FlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    List<FlightResponseDto> getAllFLights(
            LocalDateTime registeredAfter,
            LocalDateTime registeredBefore,
            FlightStatus flightStatus,
            Long flightId,
            Long aircraftId
    )
            throws IncorrectDateFiltersException,
            FlightsNotFoundException;

    List<FlightResponseDto> getFlightsForTicketReservation(
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            String flightDestination
    )
            throws FlightsNotFoundException,
            IncorrectDateFiltersException;

    FlightsEntity getFlightEntityByFlightId(Long flightId)
            throws InvalidIdException,
            FlightsNotFoundException;
}
