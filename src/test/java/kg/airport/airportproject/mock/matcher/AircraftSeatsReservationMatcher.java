package kg.airport.airportproject.mock.matcher;

import kg.airport.airportproject.entity.AircraftSeatsEntity;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class AircraftSeatsReservationMatcher implements ArgumentMatcher<AircraftSeatsEntity> {
    private AircraftSeatsEntity comparativeAircraftSeatsEntity;

    public AircraftSeatsReservationMatcher(AircraftSeatsEntity comparativeAircraftSeatsEntity) {
        this.comparativeAircraftSeatsEntity = comparativeAircraftSeatsEntity;
    }

    @Override
    public boolean matches(AircraftSeatsEntity aircraftSeatsEntity) {
        if(
                Objects.nonNull(this.comparativeAircraftSeatsEntity.getReserved()) &&
                Objects.nonNull(aircraftSeatsEntity.getReserved())
        ) {
            return comparativeAircraftSeatsEntity.getReserved().equals(aircraftSeatsEntity.getReserved());
        }
        return false;
    }
}
