package kg.airport.airportproject.entity;

import java.util.List;

public class UserRolesTestEntityProvider {
    public static final Long TEST_CLIENT_ROLE_ID = 1L;
    public static final String TEST_CLIENT_ROLE_TITLE = "CLIENT";
    public static final Long TEST_STEWARD_ROLE_ID = 10L;
    public static final String TEST_STEWARD_ROLE_TITLE = "STEWARD";
    public static List<UserRolesEntity> getTestClientRoleEntity(String roleTitle) {
        if(roleTitle.equals(TEST_CLIENT_ROLE_TITLE)) {
            return List.of(new UserRolesEntity().setId(TEST_CLIENT_ROLE_ID).setRoleTitle(TEST_CLIENT_ROLE_TITLE));
        }
        if(roleTitle.equals(TEST_STEWARD_ROLE_TITLE)) {
            return List.of(new UserRolesEntity().setId(TEST_STEWARD_ROLE_ID).setRoleTitle(TEST_STEWARD_ROLE_TITLE));
        }
        throw new RuntimeException("Роли с указанным названием не существует в системе!");
    }
}
