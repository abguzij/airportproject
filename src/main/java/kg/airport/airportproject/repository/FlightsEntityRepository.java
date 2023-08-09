package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.attributes.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightsEntityRepository
    extends JpaRepository<FlightsEntity, Long>,
        QuerydslPredicateExecutor<FlightsEntity>
{
    List<FlightsEntity> getFlightsEntitiesByStatus(FlightStatus status);

    Optional<FlightsEntity> getFlightsEntityById(Long flightId);
}
