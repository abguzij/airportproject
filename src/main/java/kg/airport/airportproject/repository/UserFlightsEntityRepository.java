package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.FlightsEntity;
import kg.airport.airportproject.entity.UserFlightsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFlightsEntityRepository
    extends JpaRepository<UserFlightsEntity, Long>,
        QuerydslPredicateExecutor<UserFlightsEntity>
{
    Optional<UserFlightsEntity> getUserFlightsEntityById(Long userFlightId);

    Optional<UserFlightsEntity> getUserFlightsEntityByApplicationUsersEntityId(Long userId);
}
