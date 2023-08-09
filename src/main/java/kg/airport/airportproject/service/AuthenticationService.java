package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.dto.JwtTokenResponseDto;
import kg.airport.airportproject.exception.IncorrectUserPositionException;
import kg.airport.airportproject.exception.UserPositionNotExists;
import kg.airport.airportproject.exception.UserRolesNotAssignedException;
import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {
    @Transactional
    ApplicationUserResponseDto registerNewClient(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExists;

    @Transactional
    ApplicationUserResponseDto registerNewEmployee(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExists;

    JwtTokenResponseDto login(ApplicationUserCredentialsRequestDto requestDto);
}
