package kg.airport.airportproject.dto;

import java.time.LocalDateTime;

public class ApplicationUserResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private LocalDateTime registeredAt;
    private Boolean isEnabled;
    private String positionTitle;

    public ApplicationUserResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public ApplicationUserResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ApplicationUserResponseDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public ApplicationUserResponseDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public ApplicationUserResponseDto setRegisteredAt(LocalDateTime registered_at) {
        this.registeredAt = registered_at;
        return this;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public ApplicationUserResponseDto setEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public ApplicationUserResponseDto setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }
}
