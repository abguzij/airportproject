package kg.airport.airportproject.dto;

public class JwtTokenResponseDto {
    private String jwtToken;

    public JwtTokenResponseDto() {
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public JwtTokenResponseDto setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        return this;
    }
}
