package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.AircraftsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftsEntityRepository
    extends JpaRepository<AircraftsEntity, Long>,
    QuerydslPredicateExecutor<AircraftsEntity>
{
    Optional<AircraftsEntity> getAircraftsEntityById(Long aircraftId);
}
