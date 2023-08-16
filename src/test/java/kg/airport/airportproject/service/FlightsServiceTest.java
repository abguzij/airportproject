package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.exception.StatusChangeException;
import kg.airport.airportproject.repository.*;
import kg.airport.airportproject.response.StatusChangedResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = UserDetailsConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class FlightsServiceTest {
    @Autowired
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Autowired
    private FlightsEntityRepository flightsEntityRepository;
    @Autowired
    private AircraftSeatsEntityRepository aircraftSeatsEntityRepository;
    @Autowired
    private UserFlightsEntityRepository userFlightsEntityRepository;
    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;
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

    @Test
    public void testAssignLanding_OK() {
        FlightsEntity flight = new FlightsEntity();
        flight
                .setStatus(FlightStatus.LANDING_REQUESTED)
                .setDestination("test")
                .setTicketsLeft(2);
        flight = this.flightsEntityRepository.save(flight);

        try {
            StatusChangedResponse statusChangedResponse = this.flightsService.assignLanding(flight.getId());
            Assertions.assertTrue(statusChangedResponse.getMessage().endsWith("[LANDING_PENDING_CONFIRMATION]"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAssignLanding_StatusChangeException() {
        FlightsEntity flight = new FlightsEntity();
        flight
                .setStatus(FlightStatus.REGISTERED)
                .setDestination("test")
                .setTicketsLeft(2);
        flight = this.flightsEntityRepository.save(flight);

        FlightsEntity finalFlight = flight;
        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.flightsService.requestLanding(finalFlight.getId())
        );
        Assertions.assertEquals(
                "Для запроса посадки раздача еды должна закончиться и все клиенты должны занять свои места!",
                exception.getMessage()
        );
    }

    @Test
    public void testConfirmLanding_OK() {
        FlightsEntity flight = new FlightsEntity();
        flight
                .setStatus(FlightStatus.LANDING_PENDING_CONFIRMATION)
                .setDestination("test")
                .setTicketsLeft(2);
        flight = this.flightsEntityRepository.save(flight);

        try {
            StatusChangedResponse statusChangedResponse = this.flightsService.confirmLanding(flight.getId());
            Assertions.assertTrue(statusChangedResponse.getMessage().endsWith("[LANDING_CONFIRMED]"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testConfirmLanding_StatusChangeException() {
        FlightsEntity flight = new FlightsEntity();
        flight
                .setStatus(FlightStatus.REGISTERED)
                .setDestination("test")
                .setTicketsLeft(2);
        flight = this.flightsEntityRepository.save(flight);

        FlightsEntity finalFlight = flight;
        Exception exception = Assertions.assertThrows(
                StatusChangeException.class,
                () -> this.flightsService.confirmLanding(finalFlight.getId())
        );
        Assertions.assertEquals(
                "Для подтверждения разрешения посадки она должна быть назначена диспетчером!",
                exception.getMessage()
        );
    }

    @Test
    public void testEndFlight_OK() {
        AircraftsEntity aircraft = new AircraftsEntity();
        aircraft
                .setStatus(AircraftStatus.IN_AIR)
                .setTitle("test")
                .setAircraftType(AircraftType.PLANE);
        aircraft = this.aircraftsEntityRepository.save(aircraft);

        AircraftSeatsEntity aircraftSeat = new AircraftSeatsEntity()
                .setReserved(Boolean.TRUE)
                .setNumberInRow(1)
                .setRowNumber(1)
                .setAircraftsEntity(aircraft);
        aircraftSeat = this.aircraftSeatsEntityRepository.save(aircraftSeat);

        List<AircraftSeatsEntity> aircraftSeatsEntities = new ArrayList<>();
        aircraftSeatsEntities.add(aircraftSeat);

        aircraft.setAircraftSeatsEntityList(aircraftSeatsEntities);

        FlightsEntity flight = new FlightsEntity();
        flight
                .setTicketsLeft(0)
                .setDestination("test")
                .setAircraftsEntity(aircraft)
                .setStatus(FlightStatus.LANDING_CONFIRMED);
        aircraft.getFlightsEntities().add(flight);
        flight = this.flightsEntityRepository.save(flight);

        ApplicationUsersEntity client = new ApplicationUsersEntity();
        client
                .setEnabled(Boolean.TRUE)
                .setUsername("test")
                .setPassword("test")
                .setFullName("test")
                .setUserPosition(new UserPositionsEntity().setId(10L).setPositionTitle("CLIENT"));
        client = this.applicationUsersEntityRepository.save(client);

        ApplicationUsersEntity steward = new ApplicationUsersEntity();
        steward
                .setEnabled(Boolean.TRUE)
                .setUsername("test")
                .setPassword("test")
                .setFullName("test")
                .setUserPosition(new UserPositionsEntity().setId(9L).setPositionTitle("STEWARD"));
        steward = this.applicationUsersEntityRepository.save(steward);

        List<UserFlightsEntity> userFlightsEntities = new ArrayList<>();
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.CLIENT_FOOD_DISTRIBUTED)
                        .setFlightsEntity(flight)
                        .setAircraftSeatsEntity(aircraftSeat)
                        .setApplicationUsersEntity(client)
        );
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.CREW_MEMBER_READY)
                        .setFlightsEntity(flight)
                        .setApplicationUsersEntity(steward)
        );
        userFlightsEntities = this.userFlightsEntityRepository.saveAll(userFlightsEntities);

        flight.setUserFlightsEntities(userFlightsEntities);
        flight = this.flightsEntityRepository.save(flight);

        try {
            StatusChangedResponse statusChangedResponse = this.flightsService.endFlight(flight.getId());
            Assertions.assertTrue(statusChangedResponse.getMessage().endsWith("[ARRIVED]"));

            flight = this.flightsService.getFlightEntityByFlightId(flight.getId());
            Assertions.assertEquals(
                    AircraftStatus.NEEDS_INSPECTION,
                    flight.getAircraftsEntity().getStatus()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
        System.out.println(flight);
    }
}