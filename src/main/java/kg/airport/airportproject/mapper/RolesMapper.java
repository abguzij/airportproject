package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.UserRolesEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RolesMapper {
    public static UserRoleResponseDto mapEntityToUserRoleResponseDto(UserRolesEntity source) {
        return new UserRoleResponseDto()
                .setId(source.getId())
                .setRoleTitle(source.getRoleTitle());
    }

    public static List<UserRoleResponseDto> mapEntityListToUserRoleResponseDtoList(List<UserRolesEntity> sourceList) {
        return sourceList
                .stream()
                .map(RolesMapper::mapEntityToUserRoleResponseDto)
                .collect(Collectors.toList());
    }
}
