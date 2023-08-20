package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartsTestDtoProvider;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.PartsTestEntityProvider;
import kg.airport.airportproject.exception.InvalidPartTitleException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}