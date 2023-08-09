package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.UserPositionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPositionsEntityRepository
    extends JpaRepository<UserPositionsEntity, Long>,
        QuerydslPredicateExecutor<UserPositionsEntity>
{
    List<UserPositionsEntity> getUserPositionsEntitiesByPositionTitleNot(String positionTitle);

    Optional<UserPositionsEntity> getUserPositionsEntityByPositionTitle(String positionTitle);

    Optional<UserPositionsEntity> getUserPositionsEntityById(Long id);
}
