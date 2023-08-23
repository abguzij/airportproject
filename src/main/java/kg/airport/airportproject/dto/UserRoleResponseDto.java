package kg.airport.airportproject.dto;

public class UserRoleResponseDto {
    private Long id;
    private String roleTitle;

    public UserRoleResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public UserRoleResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public UserRoleResponseDto setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
        return this;
    }
}
