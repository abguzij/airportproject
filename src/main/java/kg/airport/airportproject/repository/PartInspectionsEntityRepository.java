package kg.airport.airportproject.repository;

import kg.airport.airportproject.entity.PartInspectionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartInspectionsEntityRepository
    extends JpaRepository<PartInspectionsEntity, Long>,
        QuerydslPredicateExecutor<PartInspectionsEntity>
{
    @Query(value = "SELECT MAX(inspection_code) FROM public.part_inspections", nativeQuery = true)
    Long getCurrentMaxInspectionCode();

    @Query(
            value = "with aircrafts_inspections as (select * from part_inspections where aircraft_id = :aircraftId)\n" +
                    "select * " +
                    "from aircrafts_inspections" +
                    " where inspection_code = (select max(inspection_code) from aircrafts_inspections);",
            nativeQuery = true
    )
    List<PartInspectionsEntity> getLastAircraftInspectionByAircraftId(
            @Param(value = "aircraftId") Long aircraftId
    );

    @Query(
            value = "select a.aircraft_title\n" +
                    "from part_inspections\n" +
                    "join aircrafts a on a.id = part_inspections.aircraft_id\n" +
                    "where part_state = 'NEEDS_FIXING'\n" +
                    "group by a.aircraft_title;",
            nativeQuery = true
    )
    List<String> getDistinctServicedAircraftsTitles();

    @Query(
            value = "select count(part_inspections.id)\n" +
                    "from part_inspections\n" +
                    "join aircrafts a on a.id = part_inspections.aircraft_id\n" +
                    "where part_state = 'NEEDS_FIXING'\n" +
                    "group by a.aircraft_title;",
            nativeQuery = true
    )
    List<Integer> getNumbersOfRepairedPartsPerAircraft();
}
