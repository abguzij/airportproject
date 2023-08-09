package kg.airport.airportproject.dto;

public class ApplicationUserRequestDto {
    private String username;
    private String password;
    private String fullName;
    private Long positionId;

    public ApplicationUserRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public ApplicationUserRequestDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ApplicationUserRequestDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public ApplicationUserRequestDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public Long getPositionId() {
        return positionId;
    }

    public ApplicationUserRequestDto setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }
}
