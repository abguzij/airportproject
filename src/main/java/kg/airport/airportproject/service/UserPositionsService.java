package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserPositionResponseDto;

import java.util.List;

public interface UserPositionsService {
    List<UserPositionResponseDto> getAllEmployeePositions();
}
