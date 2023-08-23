package kg.airport.airportproject.entity;

import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.security.TestCredentialsProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ApplicationUsersTestEntityProvider {
    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    public static final String TEST_CLIENT_USERNAME = "client";
    public static final String TEST_CLIENT_RAW_PASSWORD = "client";
    public static final String TEST_CLIENT_FULL_NAME = "Test Client Full Name";
    public static final Long TEST_CLIENT_USER_ID = 1L;
    public static final String TEST_ADMIN_FULL_NAME = "Test Admin Full Name";

    // TODO: 21.08.2023 Перенести константы клиента с паролем и un в CredentialsProvider
    public static ApplicationUsersEntity getTestClientEntity() {
        return new ApplicationUsersEntity()
                .setId(TEST_CLIENT_USER_ID)
                .setUsername(TEST_CLIENT_USERNAME)
                .setPassword(passwordEncoder.encode(TEST_CLIENT_RAW_PASSWORD))
                .setFullName(TEST_CLIENT_FULL_NAME)
                .setUserRolesEntityList(UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                        UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE
                ))
                .setUserPosition(UserPositionsTestEntityProvider.getTestUserPositionsEntity(
                        UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID
                ))
                .setEnabled(Boolean.TRUE)
                .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE);
    }

    public static ApplicationUsersEntity getTestAdminsEntity() {
        return new ApplicationUsersEntity()
                .setId(TestCredentialsProvider.TEST_ADMIN_ID)
                .setUsername(TestCredentialsProvider.TEST_ADMIN_USERNAME)
                .setPassword(passwordEncoder.encode(TestCredentialsProvider.TEST_ADMIN_RAW_PASSWORD))
                .setFullName(TEST_ADMIN_FULL_NAME)
                .setUserRolesEntityList(UserRolesTestEntityProvider.getTestClientRoleTestEntityByRoleTitle(
                        UserRolesTestEntityProvider.TEST_ADMIN_ROLE_TITLE
                ))
                .setUserPosition(UserPositionsTestEntityProvider.getTestUserPositionsEntity(
                        UserPositionsTestEntityProvider.TEST_SYSTEM_ADMINISTRATOR_POSITION_ID
                ))
                .setEnabled(Boolean.TRUE)
                .setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_VALID_REGISTRATION_DATE);
    }

}
