package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.UserFlightRegistrationResponseDto;
import kg.airport.airportproject.dto.FlightRequestDto;
import kg.airport.airportproject.dto.FlightResponseDto;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.UserFlightsEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlightsMapper {
    public static FlightsEntity mapFlightRequestDtoToEntity(FlightRequestDto source) {
        return new FlightsEntity().setDestination(source.getDestination());
    }

    public static FlightResponseDto mapToFlightResponseDto(FlightsEntity source) {
        return new FlightResponseDto()
                .setId(source.getId())
                .setDestination(source.getDestination())
                .setRegisteredAt(source.getRegisteredAt())
                .setStatus(source.getStatus())
                .setTicketsLeft(source.getTicketsLeft())
                .setAircraftId(source.getAircraftsEntity().getId());
    }

    public static List<FlightResponseDto> mapToFlightResponseDtoList(List<FlightsEntity> sourceList) {
        return sourceList
                .stream()
                .map(FlightsMapper::mapToFlightResponseDto)
                .collect(Collectors.toList());
    }

    public static UserFlightRegistrationResponseDto mapToUserFlightRegistrationResponseDto(
            UserFlightsEntity source
    ) {
        UserFlightRegistrationResponseDto responseDto =
                new UserFlightRegistrationResponseDto()
                        .setId(source.getId())
                        .setFlightId(source.getFlightsEntity().getId())
                        .setFlightDestination(source.getFlightsEntity().getDestination())
                        .setEmployeeId(source.getId())
                        .setEmployeeFullName(source.getApplicationUsersEntity().getFullName())
                        .setEmployeePositionTitle(
                                source.getApplicationUsersEntity().getUserPosition().getPositionTitle()
                        )
                        .setRegisteredAt(source.getRegisteredAt())
                        .setUserStatus(source.getUserStatus());

        if(Objects.nonNull(source.getAircraftSeatsEntity())) {
            responseDto
                    .setSeatsRowNumber(source.getAircraftSeatsEntity().getRowNumber())
                    .setSeatNumberInRow(source.getAircraftSeatsEntity().getNumberInRow());
        }

        return responseDto;
    }
}
