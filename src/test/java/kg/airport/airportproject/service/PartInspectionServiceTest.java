package kg.airport.airportproject.service;

import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.dto.PartInspectionsResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.repository.PartInspectionsEntityRepository;
import kg.airport.airportproject.service.impl.PartInspectionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
class PartInspectionServiceTest {
    private static final Long INVALID_AIRCRAFT_ID = 0L;

    @Mock
    private PartInspectionsEntityRepository partInspectionsEntityRepository;
    @Spy
    private PartsService partsService;
    private PartInspectionService partInspectionService;

    @BeforeEach
    public void beforeEach() {
        this.partInspectionService =
                new PartInspectionServiceImpl(this.partInspectionsEntityRepository, this.partsService);
    }
    @Test
    public void testGetPartInspectionsHistory_OK() {
        Long inputAircraftId = 1L;
        Long inputInspectionCode = 1L;
        Long higherInputInspectionCode = 2L;
        Answer<Iterable<PartInspectionsEntity>> answer = invocationOnMock -> {
            return List.of(
                    new PartInspectionsEntity()
                            .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                            .setInspectionCode(inputInspectionCode)
                            .setPartsEntity(new PartsEntity().setId(1L)),
                    new PartInspectionsEntity()
                            .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                            .setInspectionCode(inputInspectionCode)
                            .setPartsEntity(new PartsEntity().setId(2L)),
                    new PartInspectionsEntity()
                            .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                            .setInspectionCode(higherInputInspectionCode)
                            .setPartsEntity(new PartsEntity().setId(2L))
            );
        };
        Mockito
                .when(this.partInspectionsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(answer);
        try {
            List<PartInspectionsResponseDto> partInspectionsResponseDtoList =
                    this.partInspectionService.getPartInspectionsHistory(inputAircraftId, inputInspectionCode);

            Assertions.assertAll(
                    "Проверки для ID самоелета",
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsResponseDtoList.get(0).getAircraftId()
                    ),
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsResponseDtoList.get(1).getAircraftId()
                    ),
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsResponseDtoList.get(2).getAircraftId()
                    )
            );
            Assertions.assertAll(
                    "Проверки для ID деталей",
                    () -> Assertions.assertEquals(2L, partInspectionsResponseDtoList.get(0).getPartId()),
                    () -> Assertions.assertEquals(1L, partInspectionsResponseDtoList.get(1).getPartId()),
                    () -> Assertions.assertEquals(2L, partInspectionsResponseDtoList.get(2).getPartId())
            );
            Assertions.assertAll(
                    "Проверки для кода осмотра",
                    () -> Assertions.assertEquals(
                            higherInputInspectionCode,
                            partInspectionsResponseDtoList.get(0).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            inputInspectionCode,
                            partInspectionsResponseDtoList.get(1).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            inputInspectionCode,
                            partInspectionsResponseDtoList.get(2).getInspectionCode()
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetPartInspectionsHistory_InvalidAircraftId() {
        Exception e = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.partInspectionService.getPartInspectionsHistory(INVALID_AIRCRAFT_ID, null)
        );
        Assertions.assertEquals("ID самолета не может быть меньше 1!", e.getMessage());
    }

    @Test
    public void testGetPartInspectionsHistory_InspectionsNotFound() {
        Mockito
                .when(this.partInspectionsEntityRepository.findAll(Mockito.any(Predicate.class)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception e = Assertions.assertThrows(
                PartInspectionsNotFoundException.class,
                () -> this.partInspectionService.getPartInspectionsHistory(1L, null)
        );
        Assertions.assertEquals(
                "Для самолета с ID[1] по заданным параметрам не найдено ни одного техосмотра!",
                e.getMessage()
        );
    }

    @Test
    public void testGetLastAircraftInspectionEntities_OK() {
        Long inputAircraftId = 1L;
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(inputAircraftId))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                                        .setPartsEntity(new PartsEntity().setId(1L))
                                        .setInspectionCode(1L),
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                                        .setPartsEntity(new PartsEntity().setId(2L))
                                        .setInspectionCode(1L)
                        )
                );

        try {
            List<PartInspectionsEntity> partInspectionsEntityList =
                    this.partInspectionService.getLastAircraftInspectionEntities(inputAircraftId);

            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsEntityList.get(0).getAircraftsEntity().getId()
                    ),
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsEntityList.get(1).getAircraftsEntity().getId()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID деталей",
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsEntityList.get(0).getPartsEntity().getId()
                    ),
                    () -> Assertions.assertEquals(
                            2L,
                            partInspectionsEntityList.get(1).getPartsEntity().getId()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID кода техосмотра",
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsEntityList.get(0).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsEntityList.get(1).getInspectionCode()
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetLastAircraftInspectionEntities_InvalidAircraftId() {
        Exception e = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.partInspectionService.getLastAircraftInspectionEntities(INVALID_AIRCRAFT_ID)
        );
        Assertions.assertEquals(
                "ID самолета не может быть меньше 1!",
                e.getMessage()
        );
    }

    @Test
    public void testGetLastAircraftInspectionEntities_InspectionsNotFound() {
        Long inputAircraftId = 1L;
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(inputAircraftId))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception e = Assertions.assertThrows(
                PartInspectionsNotFoundException.class,
                () -> this.partInspectionService.getLastAircraftInspectionEntities(inputAircraftId)
        );
        Assertions.assertEquals(
                String.format(
                        "Для самолета с ID[%d] по заданным параметрам не найдено ни одного техосмотра!",
                        inputAircraftId
                ),
                e.getMessage()
        );
    }

    @Test
    public void testGetLastAircraftInspection_OK() {
        Long inputAircraftId = 1L;
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(inputAircraftId))
                .thenAnswer(
                        invocationOnMock -> List.of(
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                                        .setPartsEntity(new PartsEntity().setId(1L))
                                        .setInspectionCode(1L),
                                new PartInspectionsEntity()
                                        .setAircraftsEntity(new AircraftsEntity().setId(inputAircraftId))
                                        .setPartsEntity(new PartsEntity().setId(2L))
                                        .setInspectionCode(1L)
                        )
                );

        try {
            List<PartInspectionsResponseDto> partInspectionsResponseDtoList =
                    this.partInspectionService.getLastAircraftInspection(inputAircraftId);

            Assertions.assertAll(
                    "Проверка ID самолета",
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsResponseDtoList.get(0).getAircraftId()
                    ),
                    () -> Assertions.assertEquals(
                            inputAircraftId,
                            partInspectionsResponseDtoList.get(1).getAircraftId()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID деталей",
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsResponseDtoList.get(0).getPartId()
                    ),
                    () -> Assertions.assertEquals(
                            2L,
                            partInspectionsResponseDtoList.get(1).getPartId()
                    )
            );
            Assertions.assertAll(
                    "Проверка ID кода техосмотра",
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsResponseDtoList.get(0).getInspectionCode()
                    ),
                    () -> Assertions.assertEquals(
                            1L,
                            partInspectionsResponseDtoList.get(1).getInspectionCode()
                    )
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetLastAircraftInspection_InvalidAircraftId() {
        Exception e = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.partInspectionService.getLastAircraftInspection(INVALID_AIRCRAFT_ID)
        );
        Assertions.assertEquals(
                "ID самолета не может быть меньше 1!",
                e.getMessage()
        );
    }

    @Test
    public void testGetLastAircraftInspection_InspectionsNotFound() {
        Long inputAircraftId = 1L;
        Mockito
                .when(this.partInspectionsEntityRepository.getLastAircraftInspectionByAircraftId(inputAircraftId))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception e = Assertions.assertThrows(
                PartInspectionsNotFoundException.class,
                () -> this.partInspectionService.getLastAircraftInspection(inputAircraftId)
        );
        Assertions.assertEquals(
                String.format(
                        "Для самолета с ID[%d] по заданным параметрам не найдено ни одного техосмотра!",
                        inputAircraftId
                ),
                e.getMessage()
        );
    }
}