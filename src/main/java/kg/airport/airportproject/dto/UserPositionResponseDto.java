package kg.airport.airportproject.dto;

public class UserPositionResponseDto {
    private Long id;
    private String title;

    public UserPositionResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public UserPositionResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public UserPositionResponseDto setTitle(String title) {
        this.title = title;
        return this;
    }
}
