package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRolesEntityRepository
    extends JpaRepository<UserRolesEntity, Long>,
        QuerydslPredicateExecutor<UserRolesEntity>
{
    List<UserRolesEntity> getUserRolesEntitiesByUserPositions(UserPositionsEntity userPositionsEntity);

    List<UserRolesEntity> getUserRolesEntitiesByRoleTitle(String roleTitle);
}
