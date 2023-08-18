package kg.airport.airportproject.mock.matcher;

import kg.airport.airportproject.entity.AircraftsEntity;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class AircraftsStatusChangedMatcher implements ArgumentMatcher<AircraftsEntity> {
    private final AircraftsEntity requiredAircraftsEntityHandlingResult;

    public AircraftsStatusChangedMatcher(AircraftsEntity requiredAircraftsEntityHandlingResult) {
        this.requiredAircraftsEntityHandlingResult = requiredAircraftsEntityHandlingResult;
    }

    @Override
    public boolean matches(AircraftsEntity aircraftsEntity) {
        if(!requiredAircraftsEntityHandlingResult.getStatus().equals(aircraftsEntity.getStatus())) {
            return false;
        }
        if(Objects.isNull(requiredAircraftsEntityHandlingResult.getServicedBy())) {
            return Objects.isNull(aircraftsEntity.getServicedBy());
        }
        return requiredAircraftsEntityHandlingResult.getServicedBy().equals(aircraftsEntity.getServicedBy());
    }
}
