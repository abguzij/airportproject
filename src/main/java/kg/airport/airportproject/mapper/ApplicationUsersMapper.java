package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserPositionsEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationUsersMapper {
    public static ApplicationUsersEntity mapApplicationUserRequestDtoToEntity(ApplicationUserRequestDto source) {
        return new ApplicationUsersEntity()
                .setUsername(source.getUsername())
                .setPassword(source.getPassword())
                .setFullName(source.getFullName());
    }

    public static ApplicationUserResponseDto mapToApplicationUserResponseDto(ApplicationUsersEntity source) {
        return new ApplicationUserResponseDto()
                .setId(source.getId())
                .setUsername(source.getUsername())
                .setFullName(source.getFullName())
                .setRegisteredAt(source.getRegisteredAt())
                .setPositionTitle(source.getUserPosition().getPositionTitle())
                .setEnabled(source.isEnabled());
    }

    public static List<ApplicationUserResponseDto> mapToApplicationUserResponseDtoList(
            List<ApplicationUsersEntity> sourceList
    ) {
        return sourceList
                .stream()
                .map(ApplicationUsersMapper::mapToApplicationUserResponseDto)
                .collect(Collectors.toList());
    }

    public static UserPositionResponseDto mapToUserPositionResponseDto(UserPositionsEntity source) {
        return new UserPositionResponseDto()
                .setId(source.getId())
                .setTitle(source.getPositionTitle());
    }

    public static List<UserPositionResponseDto> mapToUserPositionResponseDtoList(
            List<UserPositionsEntity> sourceList
    ) {
        return sourceList
                .stream()
                .map(ApplicationUsersMapper::mapToUserPositionResponseDto)
                .collect(Collectors.toList());
    }
}
