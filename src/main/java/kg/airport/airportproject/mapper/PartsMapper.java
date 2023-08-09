package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.attributes.PartType;

import java.util.List;
import java.util.stream.Collectors;

public class PartsMapper {
    public static PartsEntity mapPartRequestDtoToEntity(PartRequestDto source) {
        return new PartsEntity()
                .setTitle(source.getTitle())
                .setAircraftType(source.getAircraftType())
                .setPartType(source.getPartType());
    }

    public static PartResponseDto mapToPartResponseDto(PartsEntity source) {
        return new PartResponseDto()
                .setId(source.getId())
                .setTitle(source.getTitle())
                .setAircraftType(source.getAircraftType())
                .setPartType(source.getPartType())
                .setRegisteredAt(source.getRegisteredAt());
    }

    public static List<PartResponseDto> mapToPartResponseDtoList(List<PartsEntity> sourceList) {
        return sourceList
                .stream()
                .map(PartsMapper::mapToPartResponseDto)
                .collect(Collectors.toList());
    }

    public static PartTypesResponseDto mapToPartTypesResponseDto(List<PartType> partTypeList) {
        return new PartTypesResponseDto().setPartTypeList(partTypeList);
    }
}
