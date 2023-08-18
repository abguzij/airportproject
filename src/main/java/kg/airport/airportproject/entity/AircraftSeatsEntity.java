package kg.airport.airportproject.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "aircraft_seats")
public class AircraftSeatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "number_in_row")
    private Integer numberInRow;
    @Column(name = "row_number")
    private Integer rowNumber;
    @Column(name = "is_reserved")
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftsEntity aircraftsEntity;
    @OneToMany(mappedBy = "aircraftSeatsEntity")
    private List<UserFlightsEntity> userFlightsEntities;

    public AircraftSeatsEntity() {
        this.userFlightsEntities = new ArrayList<>();
        this.numberInRow = null;
        this.rowNumber = null;
    }

    public Long getId() {
        return id;
    }

    public AircraftSeatsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getNumberInRow() {
        return numberInRow;
    }

    public AircraftSeatsEntity setNumberInRow(Integer numberInRow) {
        this.numberInRow = numberInRow;
        return this;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public AircraftSeatsEntity setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
        return this;
    }

    public Boolean getReserved() {
        return isReserved;
    }

    public AircraftSeatsEntity setReserved(Boolean reserved) {
        isReserved = reserved;
        return this;
    }

    public AircraftsEntity getAircraftsEntity() {
        return aircraftsEntity;
    }

    public AircraftSeatsEntity setAircraftsEntity(AircraftsEntity aircraftsEntity) {
        this.aircraftsEntity = aircraftsEntity;
        return this;
    }

    public List<UserFlightsEntity> getUserFlightsEntities() {
        return userFlightsEntities;
    }

    public AircraftSeatsEntity setUserFlightsEntities(List<UserFlightsEntity> userFlightsEntities) {
        this.userFlightsEntities = userFlightsEntities;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AircraftSeatsEntity that = (AircraftSeatsEntity) o;
        return Objects.equals(numberInRow, that.numberInRow) && Objects.equals(rowNumber, that.rowNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberInRow, rowNumber);
    }
}