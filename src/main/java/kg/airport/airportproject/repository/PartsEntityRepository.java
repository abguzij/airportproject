package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.PartsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartsEntityRepository
    extends JpaRepository<PartsEntity, Long>,
        QuerydslPredicateExecutor<PartsEntity>
{
    List<PartsEntity> getPartsEntitiesByIdIn(List<Long> partsIdList);

    List<PartsEntity> getPartsEntitiesByIdInAndAircraftsEntitiesContains(
            List<Long> partsIdList,
            AircraftsEntity aircraftsEntity
    );
}
