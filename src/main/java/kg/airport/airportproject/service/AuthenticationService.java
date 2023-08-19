package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.dto.JwtTokenResponseDto;
import kg.airport.airportproject.exception.*;
import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {
    @Transactional
    ApplicationUserResponseDto registerNewClient(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExistsException,
            UsernameAlreadyExistsException,
            InvalidUserInfoException,
            InvalidCredentialsException,
            InvalidIdException;

    @Transactional
    ApplicationUserResponseDto registerNewEmployee(ApplicationUserRequestDto requestDto)
            throws UserRolesNotAssignedException,
            UserPositionNotExistsException,
            UsernameAlreadyExistsException,
            InvalidUserInfoException,
            InvalidCredentialsException,
            InvalidIdException;

    JwtTokenResponseDto login(ApplicationUserCredentialsRequestDto requestDto);
}
