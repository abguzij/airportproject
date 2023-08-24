package kg.airport.airportproject.entity;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.entity.attributes.PartState;

public class PartInspectionsTestEntityProvider {
    public static final PartState TEST_PART_STATE = PartState.CORRECT;

    public static PartInspectionsEntity getTestPartInspectionEntity(
            Long id,
            AircraftsEntity aircraft,
            Long inspectionCode,
            PartState partState
    ) {
        return new PartInspectionsEntity()
                .setId(id)
                .setAircraftsEntity(aircraft)
                .setInspectionCode(inspectionCode)
                .setPartState(partState)
                .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE);
    }
}
