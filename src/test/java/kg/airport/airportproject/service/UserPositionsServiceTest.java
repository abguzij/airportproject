package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserPositionResponseDto;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.UserPositionsTestEntityProvider;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.service.impl.UserPositionsServiceImpl;
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
public class UserPositionsServiceTest {
    @Mock
    private UserPositionsEntityRepository userPositionsEntityRepository;
    private UserPositionsService userPositionsService;

    @BeforeEach
    public void beforeEach() {
        this.userPositionsService = new UserPositionsServiceImpl(this.userPositionsEntityRepository);
    }
    @Test
    public void testGetAllEmployeePositions_OK() {
        List<UserPositionsEntity> userPositionsEntities =
                UserPositionsTestEntityProvider.getAllTestEmployeesPositionsEntities();
        Mockito
                .when(this.userPositionsEntityRepository.getUserPositionsEntitiesByPositionTitleNot(
                        Mockito.eq("CLIENT")
                ))
                .thenReturn(userPositionsEntities);
        try {
            List<UserPositionResponseDto> resultList = this.userPositionsService.getAllEmployeePositions();
            for (int i = 0; i < resultList.size(); i++) {
                Assertions.assertEquals(userPositionsEntities.get(i).getId(), resultList.get(i).getId());
                Assertions.assertEquals(userPositionsEntities.get(i).getPositionTitle(), resultList.get(i).getTitle());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}