package kg.airport.airportproject.entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class ApplicationUsersTestEntityProvider {
    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    public static final String TEST_USERNAME = "test";
    public static final String TEST_RAW_PASSWORD = "test";
    public static final String TEST_FULL_NAME = "Test Full Name";
    public static final Long TEST_USER_ID = 1L;

    public static ApplicationUsersEntity getTestClientEntity() {
        return new ApplicationUsersEntity()
                .setId(TEST_USER_ID)
                .setUsername(TEST_USERNAME)
                .setPassword(passwordEncoder.encode(TEST_RAW_PASSWORD))
                .setFullName(TEST_FULL_NAME)
                .setUserRolesEntityList(UserRolesTestEntityProvider.getTestClientRoleEntity(
                        UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
                ))
                .setUserPosition(UserPositionsTestEntityProvider.getTestUserPositionsEntity(
                        UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID
                ))
                .setEnabled(Boolean.TRUE)
                .setRegisteredAt(LocalDateTime.now());
    }

}
