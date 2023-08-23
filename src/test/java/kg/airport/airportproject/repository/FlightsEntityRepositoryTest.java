package kg.airport.airportproject.repository;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.FlightsTestEntityProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@TestPropertySource(value = "classpath:test.properties")
public class FlightsEntityRepositoryTest {
    private static final String FIRST_DESTINATION = "first";
    private static final String SECOND_DESTINATION = "second";
    private static final String THIRD_DESTINATION = "third";

    @Autowired
    private FlightsEntityRepository flightsEntityRepository;

    @BeforeEach
    public void beforeEach() {
        List<FlightsEntity> flightsEntities = new ArrayList<>();
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(1L, FIRST_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_DATE_BEFORE_START_FILTER)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(2L, FIRST_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(3L, FIRST_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(4L, SECOND_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_DATE_AFTER_END_FILTER)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(5L, SECOND_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_DATE_BEFORE_START_FILTER)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(6L, SECOND_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(7L, SECOND_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE)
        );
        flightsEntities.add(
                FlightsTestEntityProvider
                        .getTestFlightsEntity(8L, THIRD_DESTINATION)
                        .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE)
        );

        this.flightsEntityRepository.saveAll(flightsEntities);
    }

    @Test
    public void testGetDistinctDestinationValues_OK() {
        List<String> resultList = this.flightsEntityRepository.getDistinctDestinationValues();

        Assertions.assertEquals(3, resultList.size());
        Assertions.assertEquals(FIRST_DESTINATION, resultList.get(0));
        Assertions.assertEquals(SECOND_DESTINATION, resultList.get(1));
        Assertions.assertEquals(THIRD_DESTINATION, resultList.get(2));
    }

    @Test
    public void testGetDestinationsFlightsNumbersByDestinationIn_OK() {
        List<String> testDestinations = List.of(FIRST_DESTINATION, SECOND_DESTINATION);
        List<Integer> resultList =
                this.flightsEntityRepository.getDestinationsFlightsNumbersByDestinationIn(testDestinations);

        Assertions.assertEquals(2, resultList.size());
        Assertions.assertEquals(3, resultList.get(0));
        Assertions.assertEquals(4, resultList.get(1));
    }

    @Test
    public void testGetDestinationsFlightsNumbersByDateFiltersAndDestinationIn_OK() {
        List<String> testDestinations = List.of(FIRST_DESTINATION, SECOND_DESTINATION);
        List<Integer> resultList =
                this.flightsEntityRepository.getDestinationsFlightsNumbersByDateFiltersAndDestinationIn(
                        testDestinations,
                        RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER,
                        RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER
                );

        Assertions.assertEquals(2, resultList.size());
        Assertions.assertEquals(2, resultList.get(0));
        Assertions.assertEquals(2, resultList.get(1));
    }
}