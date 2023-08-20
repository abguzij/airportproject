package kg.airport.airportproject.mapper;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.dto.PartRequestDto;
import kg.airport.airportproject.dto.PartResponseDto;
import kg.airport.airportproject.dto.PartTypesResponseDto;
import kg.airport.airportproject.dto.PartsTestDtoProvider;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.PartsTestEntityProvider;
import kg.airport.airportproject.entity.attributes.PartType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PartsMapperTest {
    @Test
    public void testMapPartRequestDtoToEntity_OK() {
        PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
        PartsEntity result = PartsMapper.mapPartRequestDtoToEntity(requestDto);

        Assertions.assertEquals(result.getTitle(), requestDto.getTitle());
        Assertions.assertEquals(result.getPartType(), requestDto.getPartType());
        Assertions.assertEquals(result.getAircraftType(), requestDto.getAircraftType());
    }

    @Test
    public void testMapToPartResponseDto_OK() {
        PartsEntity partsEntity = PartsTestEntityProvider.getTestPartsEntity();
        partsEntity.setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_REGISTRATION_DATE);

        PartResponseDto result = PartsMapper.mapToPartResponseDto(partsEntity);

        Assertions.assertEquals(partsEntity.getId(), result.getId());
        Assertions.assertEquals(partsEntity.getPartType(), result.getPartType());
        Assertions.assertEquals(partsEntity.getAircraftType(), result.getAircraftType());
        Assertions.assertEquals(partsEntity.getTitle(), result.getTitle());
        Assertions.assertEquals(partsEntity.getRegisteredAt(), result.getRegisteredAt());
    }

    @Test
    public void testMapToPartResponseDtoList_OK() {
        List<PartsEntity> partsEntities = PartsTestEntityProvider.getListOfTestPartsEntities();
        List<PartResponseDto> resultList = PartsMapper.mapToPartResponseDtoList(partsEntities);

        Assertions.assertEquals(partsEntities.size(), resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            Assertions.assertEquals(partsEntities.get(i).getId(), resultList.get(i).getId());
            Assertions.assertEquals(partsEntities.get(i).getPartType(), resultList.get(i).getPartType());
            Assertions.assertEquals(partsEntities.get(i).getAircraftType(), resultList.get(i).getAircraftType());
            Assertions.assertEquals(partsEntities.get(i).getTitle(), resultList.get(i).getTitle());
            Assertions.assertEquals(partsEntities.get(i).getRegisteredAt(), resultList.get(i).getRegisteredAt());
        }
    }

    @Test
    public void testMapToPartTypesResponseDto_OK() {
        List<PartType> partTypeList = List.of(PartType.values());

        PartTypesResponseDto result = PartsMapper.mapToPartTypesResponseDto(List.of(PartType.values()));
        for (int i = 0; i < partTypeList.size(); i++) {
            Assertions.assertEquals(result.getPartTypeList().get(i), partTypeList.get(i));
        }
    }
}