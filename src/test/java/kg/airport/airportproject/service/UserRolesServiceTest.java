package kg.airport.airportproject.service;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
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

@ExtendWith(value = MockitoExtension.class)
public class UserRolesServiceTest {
    @Mock
    private UserRolesEntityRepository userRolesEntityRepository;
    @Mock
    private ApplicationUserService applicationUserService;

    private UserRolesService userRolesService;

    @BeforeEach
    public void beforeEach() {
        this.userRolesService = new UserRolesServiceImpl(
                this.userRolesEntityRepository,
                this.applicationUserService
        );
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

    @Test
    public void testGetUserRoles_OK() {
        List<UserRolesEntity> foundRoles = UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
        );
        ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
        try {
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            List<UserRoleResponseDto> resultList = this.userRolesService.getUserRoles(client.getId());

            Assertions.assertEquals(foundRoles.size(), resultList.size());

            for (int i = 0; i < foundRoles.size(); i++) {
                Assertions.assertEquals(foundRoles.get(i).getId(), resultList.get(i).getId());
                Assertions.assertEquals(foundRoles.get(i).getRoleTitle(), resultList.get(i).getRoleTitle());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetUserRoles_UserRolesNotFound() throws Exception {
        ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
        Mockito
                .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                .thenAnswer(invocationOnMock -> client.setUserRolesEntityList(new ArrayList<>()));

        Exception exception = Assertions.assertThrows(
                UserRolesNotFoundException.class,
                () -> this.userRolesService.getUserRoles(client.getId())
        );
        Assertions.assertEquals(
                String.format("Для пользователя с ID[%d] не задано ни одной роли!", client.getId()),
                exception.getMessage()
        );
    }

    @Test
    public void testUpdateUsersRoles_OK() {
        try {
            ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
            Mockito
                    .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                    .thenReturn(client);

            List<Long> testIdList = List.of(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_ID);
            List<UserRolesEntity> foundRolesList = UserRolesTestEntityProvider
                    .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_TITLE);
            Mockito
                    .when(this.userRolesEntityRepository.getUserRolesEntitiesByIdIn(Mockito.eq(testIdList)))
                    .thenReturn(foundRolesList);
            Mockito
                    .when(this.userRolesEntityRepository.saveAll(Mockito.eq(foundRolesList)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            List<UserRoleResponseDto> resultList = this.userRolesService.updateUsersRoles(testIdList, client.getId());

            List<UserRolesEntity> requiredRolesList = new ArrayList<>();
            requiredRolesList.addAll(
                    UserRolesTestEntityProvider
                            .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE)
            );
            requiredRolesList.addAll(foundRolesList);

            Assertions.assertEquals(requiredRolesList.size(), resultList.size());
            for (int i = 0; i < resultList.size(); i++) {
                Assertions.assertEquals(requiredRolesList.get(i).getId(), resultList.get(i).getId());
                Assertions.assertEquals(requiredRolesList.get(i).getRoleTitle(), resultList.get(i).getRoleTitle());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersRoles_UserRolesNotFound() throws Exception {
        ApplicationUsersEntity client = ApplicationUsersTestEntityProvider.getTestClientEntity();
        Mockito
                .when(this.applicationUserService.getApplicationUserById(Mockito.eq(client.getId())))
                .thenReturn(client);

        List<Long> testIdList = List.of(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_ID);
        List<UserRolesEntity> foundRolesList = UserRolesTestEntityProvider
                .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_STEWARD_ROLE_TITLE);
        Mockito
                .when(this.userRolesEntityRepository.getUserRolesEntitiesByIdIn(Mockito.eq(testIdList)))
                .thenAnswer(invocationOnMock -> new ArrayList<>());

        Exception exception = Assertions.assertThrows(
                UserRolesNotFoundException.class,
                () -> this.userRolesService.updateUsersRoles(testIdList, client.getId())
        );
        Assertions.assertEquals(
                "По заданным ID не найдено ни одной роли!",
                exception.getMessage()
        );
    }

    @Test
    public void testUpdateUsersRoles_NullIdList() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.userRolesService.updateUsersRoles(
                        null,
                        UserRolesTestEntityProvider.TEST_CLIENT_ROLE_ID
                )
        );
        Assertions.assertEquals(
                "Список ID добавляемых пользователю ролей не может быть null или пустым!",
                exception.getMessage()
        );
    }
}