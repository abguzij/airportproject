package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUsersEntityRepository
    extends JpaRepository<ApplicationUsersEntity, Long>,
        QuerydslPredicateExecutor<ApplicationUsersEntity>
{
    Optional<ApplicationUsersEntity> getApplicationUsersEntityById(Long userId);

    Optional<ApplicationUsersEntity> getApplicationUsersEntityByUsernameAndIsEnabledTrue(String username);

    List<ApplicationUsersEntity> getApplicationUsersEntitiesByIdIn(List<Long> userIdList);
}
