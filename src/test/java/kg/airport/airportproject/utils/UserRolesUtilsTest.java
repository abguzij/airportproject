package kg.airport.airportproject.utils;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.PackageUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRolesUtilsTest {
    private List<ApplicationUsersEntity> usersEntities;
    @BeforeEach
    public void beforeEach(){
        this.usersEntities = new ArrayList<>();
    }

    @Test
    public void testCheckIfApplicationUsersListContainsSuchUserRolesTitles_True() {
        this.usersEntities.addAll(
                List.of(
                        this.createDefaultPilot(),
                        this.createDefaultChiefSteward(),
                        this.createDefaultSteward()
                )
        );

        Assertions.assertTrue(
                UserRolesUtils.checkIfApplicationUsersListContainsSuchUserRolesTitles(
                        this.usersEntities,
                        "PILOT", "STEWARD", "CHIEF_STEWARD"
                )
        );
    }

    @Test
    public void testCheckIfApplicationUsersListContainsSuchUserRolesTitles_False() {
        this.usersEntities.addAll(
                List.of(
                        this.createDefaultPilot(),
                        this.createDefaultChiefSteward(),
                        this.createDefaultSteward()
                )
        );

        Assertions.assertFalse(
                UserRolesUtils.checkIfApplicationUsersListContainsSuchUserRolesTitles(
                        this.usersEntities,
                        "PILOT", "STEWARD", "CHIEF_STEWARD", "CLIENT"
                )
        );
    }

    @Test
    public void testCheckIfEachApplicationUserInListContainsRequiredRoles_True() {
        this.usersEntities.addAll(
                List.of(
                        this.createDefaultPilot(),
                        this.createDefaultChiefSteward(),
                        this.createDefaultSteward()
                )
        );

        Assertions.assertTrue(
                UserRolesUtils.checkIfEachApplicationUserInListContainsRequiredRoles(
                        this.usersEntities,
                        "PILOT", "STEWARD", "CHIEF_STEWARD"
                )
        );
    }

    @Test
    public void testCheckIfEachApplicationUserInListContainsRequiredRoles_False() {
        this.usersEntities.addAll(
                List.of(
                        this.createDefaultPilot(),
                        this.createDefaultChiefSteward(),
                        this.createDefaultSteward(),
                        this.createDefaultClient()
                )
        );

        Assertions.assertFalse(
                UserRolesUtils.checkIfEachApplicationUserInListContainsRequiredRoles(
                        this.usersEntities,
                        "PILOT", "STEWARD", "CHIEF_STEWARD"
                )
        );
    }

    private ApplicationUsersEntity createDefaultPilot() {
        List<UserRolesEntity> userRolesEntityList = new ArrayList<>();
        userRolesEntityList.add(
                new UserRolesEntity().setRoleTitle("PILOT")
        );

        return new ApplicationUsersEntity()
                .setUserRolesEntityList(userRolesEntityList);
    }

    private ApplicationUsersEntity createDefaultSteward() {
        List<UserRolesEntity> userRolesEntityList = new ArrayList<>();
        userRolesEntityList.add(
                new UserRolesEntity().setRoleTitle("STEWARD")
        );

        return new ApplicationUsersEntity()
                .setUserRolesEntityList(userRolesEntityList);
    }
    private ApplicationUsersEntity createDefaultChiefSteward() {
        List<UserRolesEntity> userRolesEntityList = new ArrayList<>();
        userRolesEntityList.add(
                new UserRolesEntity().setRoleTitle("CHIEF_STEWARD")
        );

        return new ApplicationUsersEntity()
                .setUserRolesEntityList(userRolesEntityList);
    }

    private ApplicationUsersEntity createDefaultClient() {
        List<UserRolesEntity> userRolesEntityList = new ArrayList<>();
        userRolesEntityList.add(
                new UserRolesEntity().setRoleTitle("CLIENT")
        );

        return new ApplicationUsersEntity()
                .setUserRolesEntityList(userRolesEntityList);
    }
}