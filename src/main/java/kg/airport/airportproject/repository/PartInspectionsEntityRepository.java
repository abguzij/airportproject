package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.PartInspectionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartInspectionsEntityRepository
    extends JpaRepository<PartInspectionsEntity, Long>,
        QuerydslPredicateExecutor<PartInspectionsEntity>
{
    @Query(value = "SELECT MAX(inspection_code) FROM public.part_inspections", nativeQuery = true)
    Long getCurrentMaxInspectionCode();

    @Query(
            value = "with aircrafts_inspections as (select * from part_inspections where aircraft_id = 1)\n" +
                    "select * " +
                    "from aircrafts_inspections" +
                    " where inspection_code = (select max(inspection_code) from aircrafts_inspections);",
            nativeQuery = true
    )
    List<PartInspectionsEntity> getLastAircraftInspectionByAircraftId(Long aircraftId);
}
