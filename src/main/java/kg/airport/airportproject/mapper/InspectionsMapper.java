package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.PartInspectionsRequestDto;
import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.dto.PartStatesResponseDto;
import kg.airport.airportproject.entity.PartInspectionsEntity;
import kg.airport.airportproject.entity.attributes.PartState;

import java.util.List;
import java.util.stream.Collectors;

public class InspectionsMapper {
    public static PartInspectionsEntity mapPartInspectionsRequestDtoToEntity(PartInspectionsRequestDto source) {
        return new PartInspectionsEntity().setPartState(source.getPartState());
    }

    public static PartInspectionsResponseDto mapToPartInspectionsResponseDto(PartInspectionsEntity source) {
        return new PartInspectionsResponseDto()
                .setId(source.getId())
                .setPartId(source.getPartsEntity().getId())
                .setPartTitle(source.getPartsEntity().getTitle())
                .setPartState(source.getPartState())
                .setPartType(source.getPartsEntity().getPartType())
                .setAircraftId(source.getAircraftsEntity().getId())
                .setAircraftTitle(source.getPartsEntity().getTitle())
                .setInspectionCode(source.getInspectionCode())
                .setRegisteredAt(source.getRegisteredAt());
    }

    public static List<PartInspectionsResponseDto> mapToPartInspectionsResponseDtoList(
            List<PartInspectionsEntity> sourceList
    ) {
        return sourceList
                .stream()
                .map(InspectionsMapper::mapToPartInspectionsResponseDto)
                .collect(Collectors.toList());
    }

    public static PartStatesResponseDto mapToPartStatesResponseDto(List<PartState> partStates) {
        return new PartStatesResponseDto().setPartStates(partStates);
    }
}
