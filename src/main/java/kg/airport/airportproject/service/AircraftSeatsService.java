package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.exception.AircraftSeatNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.SeatReservationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AircraftSeatsService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    AircraftSeatsEntity reserveSeat(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException,
            SeatReservationException;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    AircraftSeatsEntity cancelSeatReservation(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException,
            SeatReservationException;

    List<AircraftSeatsEntity> generateAircraftSeats(Integer rowsNumber, Integer numberOfSeatsInRow);

    List<AircraftSeatResponseDto> getAllAircraftSeats(Long aircraftId, Boolean isReserved)
            throws InvalidIdException,
            AircraftSeatNotFoundException;

    AircraftSeatsEntity getAircraftSeatEntityById(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException;

    Integer getNumberOfFreeSeatsByAircraftId(Long aircraftId) throws InvalidIdException;
}
