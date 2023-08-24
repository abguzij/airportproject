package kg.airport.airportproject.entity;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.entity.attributes.FlightStatus;

public class FlightsTestEntityProvider {
    public static final Long TEST_FLIGHT_ID = 1L;
    public static final FlightStatus TEST_FLIGHT_REGISTERED = FlightStatus.REGISTERED;
    public static final Integer TEST_FLIGHT_1_TICKET_LEFT = 1;
    public static FlightsEntity getTestFlightsEntity(Long id, String destination) {
        return new FlightsEntity()
                .setId(id)
                .setDestination(destination)
                .setStatus(TEST_FLIGHT_REGISTERED)
                .setTicketsLeft(TEST_FLIGHT_1_TICKET_LEFT)
                .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE);
    }
}
