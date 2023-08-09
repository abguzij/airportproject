package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.ClientFeedbacksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientFeedbacksEntityRepository
    extends JpaRepository<ClientFeedbacksEntity, Long>,
        QuerydslPredicateExecutor<ClientFeedbacksEntity>
{
}
