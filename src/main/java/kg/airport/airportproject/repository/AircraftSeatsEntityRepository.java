package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.AircraftSeatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftSeatsEntityRepository
        extends JpaRepository<AircraftSeatsEntity, Long>,
        QuerydslPredicateExecutor<AircraftSeatsEntity>
{
    Optional<AircraftSeatsEntity> getAircraftSeatsEntityById(Long seatId);
}
