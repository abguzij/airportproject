package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.AircraftRequestDto;
import kg.airport.airportproject.dto.AircraftResponseDto;
import kg.airport.airportproject.dto.AircraftSeatResponseDto;
import kg.airport.airportproject.dto.AircraftTypesResponseDto;
import kg.airport.airportproject.entity.AircraftSeatsEntity;
import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.attributes.AircraftType;

import java.util.List;
import java.util.stream.Collectors;

public class AircraftsMapper {
    public static AircraftsEntity mapAircraftRequestDtoToEntity(AircraftRequestDto source) {
        return new AircraftsEntity()
                .setAircraftType(source.getAircraftType())
                .setTitle(source.getTitle());
    }

    public static AircraftResponseDto mapToAircraftResponseDto(AircraftsEntity source) {
        return new AircraftResponseDto()
                .setId(source.getId())
                .setTitle(source.getTitle())
                .setAircraftType(source.getAircraftType())
                .setStatus(source.getStatus())
                .setRegisteredAt(source.getRegisteredAt());
    }

    public static List<AircraftResponseDto> mapToAircraftResponseDtoList(List<AircraftsEntity> sourceList) {
        return sourceList
                .stream()
                .map(AircraftsMapper::mapToAircraftResponseDto)
                .collect(Collectors.toList());
    }

    public static AircraftSeatResponseDto mapToAircraftSeatResponseDto(AircraftSeatsEntity source) {
        return new AircraftSeatResponseDto()
                .setId(source.getId())
                .setRowNumber(source.getRowNumber())
                .setNumberInRow(source.getNumberInRow())
                .setReserved(source.getReserved());
    }

    public static List<AircraftSeatResponseDto> mapToAircraftSeatResponseDtoList(List<AircraftSeatsEntity> sourceList) {
        return sourceList
                .stream()
                .map(AircraftsMapper::mapToAircraftSeatResponseDto)
                .collect(Collectors.toList());
    }

    public static AircraftTypesResponseDto mapToAircraftTypesResponseDto(List<AircraftType> aircraftTypes) {
        return new AircraftTypesResponseDto().setAircraftTypes(aircraftTypes);
    }
}
