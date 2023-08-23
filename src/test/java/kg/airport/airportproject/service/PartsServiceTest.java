package kg.airport.airportproject.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartsTestDtoProvider;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartsNotFoundException;
import kg.airport.airportproject.repository.PartsEntityRepository;
import kg.airport.airportproject.service.impl.PartsServiceImpl;
import kg.airport.airportproject.validator.PartsValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(value = MockitoExtension.class)
public class PartsServiceTest {
    @Mock
    private PartsEntityRepository partsEntityRepository;
    @Mock
    private PartsValidator partsValidator;

    private PartsService partsService;

    @BeforeEach
    public void beforeEach() {
        this.partsService = new PartsServiceImpl(this.partsEntityRepository, this.partsValidator);
    }

    @Test
    public void testRegisterNewPart_OK() {
        PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
        PartsEntity partsEntity = PartsTestEntityProvider.getTestPartsEntity();

        Mockito
                .when(this.partsEntityRepository.save(Mockito.eq(partsEntity)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        try {
            Mockito
                    .doNothing()
                    .when(this.partsValidator)
                    .validatePartRequestDto(Mockito.any(PartRequestDto.class));

            PartResponseDto result = this.partsService.registerNewPart(requestDto);

            Assertions.assertEquals(requestDto.getTitle(), result.getTitle());
            Assertions.assertEquals(requestDto.getAircraftType(), result.getAircraftType());
            Assertions.assertEquals(requestDto.getPartType(), result.getPartType());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewParts_OK() {
        List<PartRequestDto> requestDtoList = PartsTestDtoProvider.getListOfTestPartRequestDto();
        List<PartsEntity> partsEntities = PartsTestEntityProvider.getListOfTestPartsEntities();
        try {
            Mockito
                    .doNothing()
                    .when(this.partsValidator)
                    .validatePartRequestDto(Mockito.any(PartRequestDto.class));
            Mockito
                    .when(this.partsEntityRepository.saveAll(Mockito.eq(partsEntities)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            List<PartResponseDto> resultList = this.partsService.registerNewParts(requestDtoList);

            Assertions.assertEquals(requestDtoList.size(), resultList.size());
            for (int i = 0; i < resultList.size(); i++) {
                Assertions.assertEquals(requestDtoList.get(i).getPartType(), resultList.get(i).getPartType());
                Assertions.assertEquals(requestDtoList.get(i).getTitle(), resultList.get(i).getTitle());
                Assertions.assertEquals(requestDtoList.get(i).getAircraftType(), resultList.get(i).getAircraftType());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllParts_OK() {
        try {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            QPartsEntity root = QPartsEntity.partsEntity;
            booleanBuilder.and(root.aircraftType.eq(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE));
            booleanBuilder.and(root.partType.eq(PartsTestEntityProvider.TEST_PART_TYPE));
            booleanBuilder.and(root.aircraftsEntities.any().id.eq(AircraftsTestEntityProvider.TEST_AIRCRAFT_ID));
            booleanBuilder.and(root.id.eq(PartsTestEntityProvider.TEST_PART_ID));
            booleanBuilder.and(root.registeredAt.goe(RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER));
            booleanBuilder.and(root.registeredAt.loe(RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER));

            PartsEntity foundPartsEntity = PartsTestEntityProvider.getTestPartsEntity();
            foundPartsEntity.setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE);
            Mockito
                    .when(this.partsEntityRepository.findAll(Mockito.eq(booleanBuilder.getValue())))
                    .thenAnswer(invocationOnMock -> List.of(foundPartsEntity));

            List<PartResponseDto> resultList = this.partsService.getAllParts(
                    AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE,
                    PartsTestEntityProvider.TEST_PART_TYPE,
                    AircraftsTestEntityProvider.TEST_AIRCRAFT_ID,
                    PartsTestEntityProvider.TEST_PART_ID,
                    RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER,
                    RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER
            );

            Assertions.assertEquals(1, resultList.size());
            Assertions.assertEquals(foundPartsEntity.getTitle(), resultList.get(0).getTitle());
            Assertions.assertEquals(foundPartsEntity.getPartType(), resultList.get(0).getPartType());
            Assertions.assertEquals(foundPartsEntity.getAircraftType(), resultList.get(0).getAircraftType());
            Assertions.assertEquals(foundPartsEntity.getId(), resultList.get(0).getId());
            Assertions.assertEquals(foundPartsEntity.getRegisteredAt(), resultList.get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllParts_PartsNotFound() {
        Mockito
                .when(this.partsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                PartsNotFoundException.class,
                () -> this.partsService.getAllParts(
                        AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE,
                        PartsTestEntityProvider.TEST_PART_TYPE,
                        AircraftsTestEntityProvider.TEST_AIRCRAFT_ID,
                        PartsTestEntityProvider.TEST_PART_ID,
                        RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER,
                        RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER
                )
        );
        Assertions.assertEquals(
                "Деталей по заданным параметрам не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAllParts_InvalidPartId() {
        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.partsService.getAllParts(
                        AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE,
                        PartsTestEntityProvider.TEST_PART_TYPE,
                        AircraftsTestEntityProvider.TEST_AIRCRAFT_ID,
                        0L,
                        RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER,
                        RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER
                )
        );
        Assertions.assertEquals(
                "ID детали не может быть меньше 1!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftType_OK() {
        List<PartsEntity> foundParts = PartsTestEntityProvider.getListOfTestPartsEntities();
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);

        Mockito
                .when(this.partsEntityRepository.getPartsEntitiesByIdIn(Mockito.eq(testPartIdList)))
                .thenReturn(foundParts);
        try {
            List<PartsEntity> resultList = this.partsService.getPartEntitiesByPartsIdListAndAircraftType(
                    testPartIdList,
                    AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE
            );

            for (PartsEntity result : resultList) {
                Assertions.assertEquals(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE, result.getAircraftType());
                Assertions.assertTrue(testPartIdList.contains(result.getId()));
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftType_PartsNotFound() {
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);

        Mockito
                .when(this.partsEntityRepository.getPartsEntitiesByIdIn(Mockito.eq(testPartIdList)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                PartsNotFoundException.class,
                () -> this.partsService.getPartEntitiesByPartsIdListAndAircraftType(
                        testPartIdList,
                        AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE
                )
        );
        Assertions.assertEquals(
                "Деталей по заданным ID не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftType_NullAircraftType() {
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);

        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.partsService.getPartEntitiesByPartsIdListAndAircraftType(
                        testPartIdList,
                        null
                )
        );
        Assertions.assertEquals(
                "Тип самолета не может быть null!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftId_OK() {
        List<PartsEntity> foundParts = PartsTestEntityProvider.getListOfTestPartsEntities();
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);
        AircraftsEntity aircraft = AircraftsTestEntityProvider.getAircraftsTestEntity();
        for (PartsEntity foundPart : foundParts) {
            foundPart.getAircraftsEntities().add(aircraft);
            aircraft.getPartsEntities().add(foundPart);
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPartsEntity root = QPartsEntity.partsEntity;
        booleanBuilder.and(root.id.in(testPartIdList));
        booleanBuilder.and(root.aircraftsEntities.any().id.eq(aircraft.getId()));
        Mockito
                .when(this.partsEntityRepository.findAll(Mockito.eq(booleanBuilder.getValue())))
                .thenReturn(foundParts);
        try {
            List<PartsEntity> resultList = this.partsService.getPartEntitiesByPartsIdListAndAircraftId(
                    testPartIdList,
                    aircraft.getId()
            );

            for (int i = 0; i < resultList.size(); i++) {
                Assertions.assertEquals(foundParts.get(i).getId(), resultList.get(i).getId());
                Assertions.assertEquals(foundParts.get(i).getAircraftType(), resultList.get(i).getAircraftType());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftId_PartsNotFound() {
        List<PartsEntity> foundParts = PartsTestEntityProvider.getListOfTestPartsEntities();
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);
        AircraftsEntity aircraft = AircraftsTestEntityProvider.getAircraftsTestEntity();
        for (PartsEntity foundPart : foundParts) {
            foundPart.getAircraftsEntities().add(aircraft);
            aircraft.getPartsEntities().add(foundPart);
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPartsEntity root = QPartsEntity.partsEntity;
        booleanBuilder.and(root.id.in(testPartIdList));
        booleanBuilder.and(root.aircraftsEntities.any().id.eq(aircraft.getId()));
        Mockito
                .when(this.partsEntityRepository.findAll(Mockito.eq(booleanBuilder.getValue())))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                PartsNotFoundException.class,
                () -> this.partsService.getPartEntitiesByPartsIdListAndAircraftId(testPartIdList, aircraft.getId())
        );
        Assertions.assertEquals(
                "Деталей самолета по заданным ID деталей и ID самолета не найдено!",
                exception.getMessage()
        );
    }

    @Test
    public void testGetPartEntitiesByPartsIdListAndAircraftId_NullAircraftId() {
        List<Long> testPartIdList =
                List.of(PartsTestEntityProvider.TEST_PART_ID, PartsTestEntityProvider.TEST_SECOND_PART_ID);

        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.partsService.getPartEntitiesByPartsIdListAndAircraftId(testPartIdList, null)
        );
        Assertions.assertEquals("ID самолета не может быть null!", exception.getMessage());
    }


}