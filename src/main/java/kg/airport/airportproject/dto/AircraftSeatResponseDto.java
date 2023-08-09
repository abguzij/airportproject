package kg.airport.airportproject.dto;

public class AircraftSeatResponseDto {
    private Long id;
    private Integer numberInRow;
    private Integer rowNumber;
    private Boolean isReserved;

    public AircraftSeatResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public AircraftSeatResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getNumberInRow() {
        return numberInRow;
    }

    public AircraftSeatResponseDto setNumberInRow(Integer numberInRow) {
        this.numberInRow = numberInRow;
        return this;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public AircraftSeatResponseDto setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
        return this;
    }

    public Boolean getReserved() {
        return isReserved;
    }

    public AircraftSeatResponseDto setReserved(Boolean reserved) {
        isReserved = reserved;
        return this;
    }
}
