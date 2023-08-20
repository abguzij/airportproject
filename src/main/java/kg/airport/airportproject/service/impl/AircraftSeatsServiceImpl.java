package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.entity.QAircraftSeatsEntity;
import kg.airport.airportproject.exception.AircraftSeatNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.SeatReservationException;
import kg.airport.airportproject.mapper.AircraftsMapper;
import kg.airport.airportproject.repository.AircraftSeatsEntityRepository;
import kg.airport.airportproject.service.AircraftSeatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AircraftSeatsServiceImpl implements AircraftSeatsService {
    private final AircraftSeatsEntityRepository aircraftSeatsEntityRepository;

    @Autowired
    public AircraftSeatsServiceImpl(
            AircraftSeatsEntityRepository aircraftSeatsEntityRepository
    ) {
        this.aircraftSeatsEntityRepository = aircraftSeatsEntityRepository;
    }

    @Override
    public AircraftSeatsEntity reserveSeat(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException,
            SeatReservationException
    {
        AircraftSeatsEntity aircraftSeatsEntity = this.getAircraftSeatEntityById(seatId);
        if(aircraftSeatsEntity.getReserved()) {
            throw new SeatReservationException(
                    String.format("Ошибка! Место с ID [%d] уже забронировано!", seatId)
            );
        }
        aircraftSeatsEntity.setReserved(Boolean.TRUE);

        return this.aircraftSeatsEntityRepository.save(aircraftSeatsEntity);
    }

    @Override
    public AircraftSeatsEntity cancelSeatReservation(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException,
            SeatReservationException
    {
        AircraftSeatsEntity aircraftSeatsEntity = this.getAircraftSeatEntityById(seatId);
        if(!aircraftSeatsEntity.getReserved()) {
            throw new SeatReservationException(
                    String.format("Ошибка! Место с ID [%d] свободно!", seatId)
            );
        }
        aircraftSeatsEntity.setReserved(Boolean.FALSE);

        return this.aircraftSeatsEntityRepository.save(aircraftSeatsEntity);
    }

    @Override
    public List<AircraftSeatsEntity> generateAircraftSeats(
            Integer rowsNumber,
            Integer numberOfSeatsInRow
    ) {
        if(Objects.isNull(rowsNumber) || Objects.isNull(numberOfSeatsInRow)) {
            throw new IllegalArgumentException("Количество рядов и количество мест в ряду не может быть null!");
        }
        if(rowsNumber < 1 || numberOfSeatsInRow < 1) {
            throw new IllegalArgumentException("Количество рядов и количество мест в ряду не может быть меньше 1!");
        }

        List<AircraftSeatsEntity> aircraftSeatsEntities = new ArrayList<>();
        for (int i = 0; i < (rowsNumber * numberOfSeatsInRow); i++) {
            aircraftSeatsEntities.add(
                    new AircraftSeatsEntity()
                            .setRowNumber(this.evaluateRowNumber(rowsNumber, numberOfSeatsInRow, i))
                            .setNumberInRow(this.evaluateNumberOfSeatInRow(numberOfSeatsInRow, i))
                            .setReserved(Boolean.FALSE)
            );
        }


//        for (int rowNumber = 1; rowNumber <= rowsNumber; rowNumber++) {
//            for (int numberInRow = 1; numberInRow <= numberOfSeatsInRow; numberInRow++) {
//
//            }
//        }
        return aircraftSeatsEntities;
    }

    @Override
    public List<AircraftSeatResponseDto> getAllAircraftSeats(Long aircraftId, Boolean isReserved)
            throws InvalidIdException,
            AircraftSeatNotFoundException
    {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета для поиска мест для бронирования не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета для поиска мест для бронирования не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QAircraftSeatsEntity root = QAircraftSeatsEntity.aircraftSeatsEntity;

        booleanBuilder.and(root.aircraftsEntity.id.eq(aircraftId));
        if(Objects.nonNull(isReserved)) {
            booleanBuilder.and(root.isReserved.eq(isReserved));
        }

        Iterable<AircraftSeatsEntity> aircraftSeatsEntityIterable =
                this.aircraftSeatsEntityRepository.findAll(booleanBuilder.getValue());
        List<AircraftSeatResponseDto> aircraftSeatResponseDtoList =
                StreamSupport
                        .stream(aircraftSeatsEntityIterable.spliterator(), false)
                        .map(AircraftsMapper::mapToAircraftSeatResponseDto)
                        .collect(Collectors.toList());

        if(aircraftSeatResponseDtoList.isEmpty()) {
            throw new AircraftSeatNotFoundException("Мест для бронирования по заданным параметрам не найдено");
        }
        return aircraftSeatResponseDtoList;
    }

    @Override
    public AircraftSeatsEntity getAircraftSeatEntityById(Long seatId)
            throws InvalidIdException,
            AircraftSeatNotFoundException
    {
        if(Objects.isNull(seatId)) {
            throw new IllegalArgumentException("ID места в самолете не может быть null!");
        }
        if(seatId < 1L) {
            throw new InvalidIdException("ID места в самолете не может быть меньше 1!");
        }

        Optional<AircraftSeatsEntity> aircraftSeatsEntityOptional =
                this.aircraftSeatsEntityRepository.getAircraftSeatsEntityById(seatId);
        if(aircraftSeatsEntityOptional.isEmpty()) {
            throw new AircraftSeatNotFoundException(
                    String.format("Места в самолете с ID[%d] не найдено!", seatId)
            );
        }
        return aircraftSeatsEntityOptional.get();
    }

    @Override
    public Integer getNumberOfFreeSeatsByAircraftId(Long aircraftId)
            throws InvalidIdException
    {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("ID самолета не может быть null!");
        }
        if(aircraftId < 1L) {
            throw new InvalidIdException("ID самолета не может быть меньше 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QAircraftSeatsEntity root = QAircraftSeatsEntity.aircraftSeatsEntity;

        booleanBuilder.and(root.aircraftsEntity.id.eq(aircraftId));
        booleanBuilder.and(root.isReserved.eq(Boolean.FALSE));

        Iterable<AircraftSeatsEntity> aircraftSeatsEntityIterable =
                this.aircraftSeatsEntityRepository.findAll(booleanBuilder.getValue());
        List<AircraftSeatsEntity> aircraftSeatsEntities =
                StreamSupport
                        .stream(aircraftSeatsEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        return aircraftSeatsEntities.size();
    }

    private Integer evaluateRowNumber(Integer rowsNumber, Integer numberOfSeatsInRow, int index) {
        return rowsNumber - (rowsNumber - (index / numberOfSeatsInRow + 1));
    }

    private Integer evaluateNumberOfSeatInRow(Integer numberOfSeatsInRow, int index) {
        return (index % numberOfSeatsInRow) + 1;
    }
}
