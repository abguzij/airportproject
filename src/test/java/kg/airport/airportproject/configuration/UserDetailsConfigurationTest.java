package kg.airport.airportproject.configuration;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.security.DefaultCredentialsProvider;
import kg.airport.airportproject.adapter.InMemoryUserDetailsManagerAdapter;
import kg.airport.airportproject.security.TestAuthoritiesFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource(value = "classpath:test.properties")
public class UserDetailsConfigurationTest {

    @Bean
    public UserDetailsService applicationUserDetailsServiceImpl() {
        return new InMemoryUserDetailsManagerAdapter(
                this.client(),
                this.chiefEngineer(),
                this.engineer(),
                this.manager(),
                this.dispatcher()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    private ApplicationUsersEntity client() {
        ApplicationUsersEntity client = new ApplicationUsersEntity()
                .setUsername(DefaultCredentialsProvider.DEFAULT_CLIENT_USERNAME)
                .setPassword(this.passwordEncoder().encode(DefaultCredentialsProvider.DEFAULT_CLIENT_RAW_PASSWORD))
                .setFullName("Default Client")
                .setId(1L)
                .setEnabled(true);
        client.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));
        return client;
    }

    private ApplicationUsersEntity chiefEngineer() {
        ApplicationUsersEntity chiefEngineer = new ApplicationUsersEntity()
                .setUsername(DefaultCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_USERNAME)
                .setPassword(
                        this.passwordEncoder().encode(DefaultCredentialsProvider.DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD)
                )
                .setFullName("Default Chief Engineer")
                .setId(2L)
                .setEnabled(true);
        chiefEngineer.getUserRolesEntityList().add(new UserRolesEntity().setId(6L).setRoleTitle("CHIEF_ENGINEER"));
        return chiefEngineer;
    }

    public ApplicationUsersEntity engineer() {
        ApplicationUsersEntity engineer = new ApplicationUsersEntity()
                .setUsername(DefaultCredentialsProvider.DEFAULT_ENGINEERS_USERNAME)
                .setPassword(this.passwordEncoder().encode(DefaultCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Engineer")
                .setId(DefaultCredentialsProvider.ENGINEERS_DEFAULT_ID)
                .setEnabled(true);
        engineer.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        return engineer;
    }

    public ApplicationUsersEntity manager() {
        ApplicationUsersEntity manager = new ApplicationUsersEntity()
                .setUsername(DefaultCredentialsProvider.DEFAULT_MANAGER_USERNAME)
                .setPassword(this.passwordEncoder().encode(DefaultCredentialsProvider.DEFAULT_MANAGER_RAW_PASSWORD))
                .setFullName("Default Manager")
                .setId(4L)
                .setEnabled(true);
        manager.getUserRolesEntityList().add(new UserRolesEntity().setId(3L).setRoleTitle("MANAGER"));
        return manager;
    }

    public ApplicationUsersEntity dispatcher() {
        try {
            return new ApplicationUsersEntity()
                    .setUsername(DefaultCredentialsProvider.DEFAULT_DISPATCHER_USERNAME)
                    .setPassword(this.passwordEncoder().encode(DefaultCredentialsProvider.DEFAULT_DISPATCHER_RAW_PASSWORD))
                    .setFullName("Default Dispatcher")
                    .setId(DefaultCredentialsProvider.DISPATCHER_DEFAULT_ID)
                    .setEnabled(true)
                    .setUserRolesEntityList(TestAuthoritiesFactory.getUserRolesByUserRoleTitle("DISPATCHER"));
        } catch (UserRolesNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}