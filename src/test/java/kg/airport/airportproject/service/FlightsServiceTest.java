package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.repository.FlightsEntityRepository;
import kg.airport.airportproject.response.StatusChangedResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class FlightsServiceTest {
    @Autowired
    private FlightsEntityRepository flightsEntityRepository;
    @Autowired
    private FlightsService flightsService;

    @Test
    public void testRequestLanding_OK() {
        FlightsEntity flight = new FlightsEntity();
        flight
                .setStatus(FlightStatus.FLIGHT_FOOD_DISTRIBUTED)
                .setDestination("test")
                .setTicketsLeft(2);
        flight = this.flightsEntityRepository.save(flight);

        try {
            StatusChangedResponse statusChangedResponse = this.flightsService.requestLanding(flight.getId());
            Assertions.assertTrue(statusChangedResponse.getMessage().endsWith("[LANDING_REQUESTED]"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRequestLanding_NullFlightId() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.flightsService.requestLanding(null)
        );
        Assertions.assertEquals("ID рейса не может быть null!", exception.getMessage());
    }
}