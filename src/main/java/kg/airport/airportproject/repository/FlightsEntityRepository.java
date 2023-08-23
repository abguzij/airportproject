package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FlightsEntityRepository
    extends JpaRepository<FlightsEntity, Long>,
        QuerydslPredicateExecutor<FlightsEntity>
{
    List<FlightsEntity> getFlightsEntitiesByStatus(FlightStatus status);

    Optional<FlightsEntity> getFlightsEntityById(Long flightId);

    @Query(value = "select distinct destination from public.flights;", nativeQuery = true)
    List<String> getDistinctDestinationValues();

    @Query(
            value = "select count(public.flights.id)" +
                    " from public.flights" +
                    " where destination in :destinations" +
                    " group by destination;",
            nativeQuery = true
    )
    List<Integer> getDestinationsFlightsNumbersByDestinationIn(
            @Param(value = "destinations") List<String> destinations
    );

    @Query(
            value = "select count(public.flights.id)" +
                    " from public.flights" +
                    " where (public.flights.destination in :destinations)" +
                    " and (public.flights.registered_at >= :startDate)" +
                    " and (public.flights.registered_at <= :endDate)" +
                    " group by public.flights.destination;",
            nativeQuery = true
    )
    List<Integer> getDestinationsFlightsNumbersByDateFiltersAndDestinationIn(
            @Param(value = "destinations") List<String> destinations,
            @Param(value = "startDate") LocalDateTime startDate,
            @Param(value = "endDate") LocalDateTime endDate
    );
}
