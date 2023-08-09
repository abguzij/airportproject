package kg.airport.airportproject.dto;

public class ApplicationUserCredentialsRequestDto {
    private String username;
    private String password;

    public ApplicationUserCredentialsRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public ApplicationUserCredentialsRequestDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ApplicationUserCredentialsRequestDto setPassword(String password) {
        this.password = password;
        return this;
    }
}
