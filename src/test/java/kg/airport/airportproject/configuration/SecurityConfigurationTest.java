package kg.airport.airportproject.configuration;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.security.TestCredentialsProvider;
import kg.airport.airportproject.adapter.InMemoryUserDetailsManagerAdapter;
import kg.airport.airportproject.security.TestAuthoritiesFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource(value = "classpath:test.properties")
public class SecurityConfigurationTest {

    @Bean
    public UserDetailsService applicationUserDetailsServiceImpl() {
        return new InMemoryUserDetailsManagerAdapter(
                this.client(),
                this.chiefEngineer(),
                this.engineer(),
                this.manager(),
                this.dispatcher(),
                this.chiefDispatcher(),
                this.admin()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    private ApplicationUsersEntity client() {
        ApplicationUsersEntity client = new ApplicationUsersEntity()
                .setUsername(TestCredentialsProvider.DEFAULT_CLIENT_USERNAME)
                .setPassword(this.passwordEncoder().encode(TestCredentialsProvider.DEFAULT_CLIENT_RAW_PASSWORD))
                .setFullName("Default Client")
                .setId(1L)
                .setEnabled(true);
        client.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));
        return client;
    }

    private ApplicationUsersEntity chiefEngineer() {
        ApplicationUsersEntity chiefEngineer = new ApplicationUsersEntity()
                .setUsername(TestCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_USERNAME)
                .setPassword(
                        this.passwordEncoder().encode(TestCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD)
                )
                .setFullName("Default Chief Engineer")
                .setId(2L)
                .setEnabled(true);
        chiefEngineer.getUserRolesEntityList().add(new UserRolesEntity().setId(6L).setRoleTitle("CHIEF_ENGINEER"));
        return chiefEngineer;
    }

    public ApplicationUsersEntity engineer() {
        ApplicationUsersEntity engineer = new ApplicationUsersEntity()
                .setUsername(TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME)
                .setPassword(this.passwordEncoder().encode(TestCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Engineer")
                .setId(TestCredentialsProvider.ENGINEERS_DEFAULT_ID)
                .setEnabled(true);
        engineer.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        return engineer;
    }

    public ApplicationUsersEntity manager() {
        ApplicationUsersEntity manager = new ApplicationUsersEntity()
                .setUsername(TestCredentialsProvider.DEFAULT_MANAGER_USERNAME)
                .setPassword(this.passwordEncoder().encode(TestCredentialsProvider.DEFAULT_MANAGER_RAW_PASSWORD))
                .setFullName("Default Manager")
                .setId(4L)
                .setEnabled(true);
        manager.getUserRolesEntityList().add(new UserRolesEntity().setId(3L).setRoleTitle("MANAGER"));
        return manager;
    }

    public ApplicationUsersEntity dispatcher() {
        try {
            return new ApplicationUsersEntity()
                    .setUsername(TestCredentialsProvider.DEFAULT_DISPATCHER_USERNAME)
                    .setPassword(this.passwordEncoder().encode(TestCredentialsProvider.DEFAULT_DISPATCHER_RAW_PASSWORD))
                    .setFullName("Default Dispatcher")
                    .setId(TestCredentialsProvider.DISPATCHER_DEFAULT_ID)
                    .setEnabled(true)
                    .setUserRolesEntityList(TestAuthoritiesFactory.getUserRolesByUserRoleTitle("DISPATCHER"));
        } catch (UserRolesNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ApplicationUsersEntity chiefDispatcher() {
        try {
            return new ApplicationUsersEntity()
                    .setUsername(TestCredentialsProvider.DEFAULT_CHIEF_DISPATCHER_USERNAME)
                    .setPassword(this.passwordEncoder().encode(
                            TestCredentialsProvider.DEFAULT_CHIEF_DISPATCHER_RAW_PASSWORD
                    ))
                    .setFullName("Default Chief Dispatcher")
                    .setId(TestCredentialsProvider.CHIEF_DISPATCHER_DEFAULT_ID)
                    .setEnabled(true)
                    .setUserRolesEntityList(TestAuthoritiesFactory.getUserRolesByUserRoleTitle("CHIEF_DISPATCHER"));
        } catch (UserRolesNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ApplicationUsersEntity admin() {
        ApplicationUsersEntity admin = ApplicationUsersTestEntityProvider.getTestAdminsEntity();
        admin.setPassword(this.passwordEncoder().encode(TestCredentialsProvider.TEST_ADMIN_RAW_PASSWORD));
        return admin;
    }

}