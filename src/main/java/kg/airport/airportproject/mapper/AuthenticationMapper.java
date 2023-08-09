package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.JwtTokenResponseDto;

public class AuthenticationMapper {
    public static JwtTokenResponseDto mapToJwtTokenResponseDto(String jwtTokenValue) {
        return new JwtTokenResponseDto().setJwtToken(jwtTokenValue);
    }
}
