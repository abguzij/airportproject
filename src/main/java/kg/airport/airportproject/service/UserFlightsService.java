package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserFlightRequestDto;
import kg.airport.airportproject.dto.UserFlightRegistrationResponseDto;
import kg.airport.airportproject.entity.UserFlightsEntity;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserFlightsService {
    @Transactional
    List<UserFlightRegistrationResponseDto> registerEmployeesForFlight(List<UserFlightRequestDto> requestDtoList)
            throws InvalidIdException,
            FlightsNotFoundException,
            WrongFlightException,
            ApplicationUserNotFoundException,
            NotEnoughRolesForCrewRegistrationException,
            InvalidUserRoleException;

    @Transactional
    UserFlightRegistrationResponseDto registerClientForFlight(UserFlightRequestDto requestDto)
            throws InvalidIdException,
            FlightsNotFoundException,
            WrongFlightException,
            AircraftSeatNotFoundException,
            SeatReservationException;

    @Transactional
    UserFlightRegistrationResponseDto cancelClientRegistration(Long registrationId)
            throws InvalidIdException,
            UserFlightsNotFoundException,
            TicketCancelingException,
            AircraftSeatNotFoundException,
            SeatReservationException,
            FlightsNotFoundException;

    @Transactional
    UserFlightRegistrationResponseDto checkClient(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException, StatusChangeException;

    @Transactional
    UserFlightRegistrationResponseDto distributeClientsFood(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException, StatusChangeException;

    @Transactional
    UserFlightRegistrationResponseDto conductClientsBriefing(Long clientRegistrationId)
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    @Transactional
    UserFlightRegistrationResponseDto confirmReadinessForFlight()
            throws UserFlightsNotFoundException,
            InvalidIdException,
            StatusChangeException;

    List<UserFlightRegistrationResponseDto> getAllUserRegistrations(
            Long flightId,
            UserFlightsStatus status,
            Long userId,
            Boolean isClient
    ) throws UserFlightsNotFoundException, InvalidIdException;

    List<UserFlightRegistrationResponseDto> getAllClientRegistrations(Long flightId, UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException;

    List<UserFlightRegistrationResponseDto> getAllEmployeesRegistrations(Long flightId, UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException;

    List<UserFlightRegistrationResponseDto> getAllClientRegistrationsForCurrentFLight(UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException;

    List<UserFlightRegistrationResponseDto> getClientsFlightRegistrationHistory(UserFlightsStatus status)
            throws UserFlightsNotFoundException,
            InvalidIdException;

    UserFlightRegistrationResponseDto getCurrentFlight()
            throws UserFlightsNotFoundException,
            InvalidIdException;

    UserFlightsEntity getClientFlightRegistrationByClientIdAndUserFlightId(Long registrationId, Long userId)
            throws InvalidIdException,
            UserFlightsNotFoundException;

    UserFlightsEntity getClientFlightRegistrationById(Long registrationId)
            throws InvalidIdException,
            UserFlightsNotFoundException;

    UserFlightsEntity getUserFlightRegistrationByUserId(Long userId)
            throws InvalidIdException,
            UserFlightsNotFoundException;

    boolean checkIfAllPassengersOfFlightHaveStatus(Long flightId, UserFlightsStatus status) throws InvalidIdException;

    boolean checkIfAllCrewMembersIsReadyForFlight(Long flightId) throws InvalidIdException;
}
