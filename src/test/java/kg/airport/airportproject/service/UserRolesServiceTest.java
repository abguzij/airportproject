package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.entity.UserRolesTestEntityProvider;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.service.impl.UserRolesServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
public class UserRolesServiceTest {
    @Mock
    private UserRolesEntityRepository userRolesEntityRepository;

    private UserRolesService userRolesService;

    @BeforeEach
    public void beforeEach() {
        this.userRolesService = new UserRolesServiceImpl(this.userRolesEntityRepository);
    }

    @Test
    public void testGetAllUserRoles_OK() {
        List<UserRolesEntity> foundUserRolesEntities = UserRolesTestEntityProvider.getAllUserRolesTestEntities();
        Mockito
                .when(this.userRolesEntityRepository.findAll())
                .thenReturn(foundUserRolesEntities);
        try {
            List<UserRoleResponseDto> resultList = this.userRolesService.getAllUserRoles();

            Assertions.assertEquals(foundUserRolesEntities.size(), resultList.size());
            for (int i = 0; i < foundUserRolesEntities.size(); i++) {
                Assertions.assertEquals(foundUserRolesEntities.get(i).getId(), resultList.get(i).getId());
                Assertions.assertEquals(foundUserRolesEntities.get(i).getRoleTitle(), resultList.get(i).getRoleTitle());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllUserRoles_UserRolesNotFound() {
        Mockito
                .when(this.userRolesEntityRepository.findAll())
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                UserRolesNotFoundException.class,
                () -> this.userRolesService.getAllUserRoles()
        );
        Assertions.assertEquals(
                "В системе не было создано ни одной роли!",
                exception.getMessage()
        );
    }
}