package kg.airport.airportproject.configuration;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
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
    public static final String DEFAULT_CLIENT_USERNAME = "client";
    public static final String DEFAULT_CLIENT_RAW_PASSWORD = "client";
    public static final String DEFAULT_CHIEF_ENGINEERS_USERNAME = "chief_eng";
    public static final String DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD = "chief_eng";
    public static final Long ENGINEERS_DEFAULT_ID = 3L;
    public static final String DEFAULT_ENGINEERS_USERNAME = "eng";
    public static final String DEFAULT_ENGINEERS_RAW_PASSWORD = "eng";
    public static final String DEFAULT_MANAGER_USERNAME = "manager";
    public static final String DEFAULT_MANAGER_RAW_PASSWORD = "manager";

    @Bean
    public UserDetailsService applicationUserDetailsServiceImpl() {
        return new InMemoryUserDetailsManager(
                this.client(),
                this.chiefEngineer(),
                this.engineer(),
                this.manager()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    private ApplicationUsersEntity client() {
        ApplicationUsersEntity client = new ApplicationUsersEntity()
                .setUsername(DEFAULT_CLIENT_USERNAME)
                .setPassword(this.passwordEncoder().encode(DEFAULT_CLIENT_RAW_PASSWORD))
                .setFullName("Default Client")
                .setId(1L)
                .setEnabled(true);
        client.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));
        return client;
    }

    private ApplicationUsersEntity chiefEngineer() {
        ApplicationUsersEntity chiefEngineer = new ApplicationUsersEntity()
                .setUsername(DEFAULT_CHIEF_ENGINEERS_USERNAME)
                .setPassword(this.passwordEncoder().encode(DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Chief Engineer")
                .setId(2L)
                .setEnabled(true);
        chiefEngineer.getUserRolesEntityList().add(new UserRolesEntity().setId(6L).setRoleTitle("CHIEF_ENGINEER"));
        return chiefEngineer;
    }

    public ApplicationUsersEntity engineer() {
        ApplicationUsersEntity engineer = new ApplicationUsersEntity()
                .setUsername(DEFAULT_ENGINEERS_USERNAME)
                .setPassword(this.passwordEncoder().encode(DEFAULT_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Engineer")
                .setId(ENGINEERS_DEFAULT_ID)
                .setEnabled(true);
        engineer.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        return engineer;
    }

    public ApplicationUsersEntity manager() {
        ApplicationUsersEntity manager = new ApplicationUsersEntity()
                .setUsername(DEFAULT_MANAGER_USERNAME)
                .setPassword(this.passwordEncoder().encode(DEFAULT_MANAGER_RAW_PASSWORD))
                .setFullName("Default Manager")
                .setId(4L)
                .setEnabled(true);
        manager.getUserRolesEntityList().add(new UserRolesEntity().setId(3L).setRoleTitle("MANAGER"));
        return manager;
    }
}