package kg.airport.airportproject.configuration;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

@TestConfiguration
@TestPropertySource(value = "classpath:test.properties")
public class SecurityConfigurationTest {
    public static final String DEFAULT_CLIENT_USERNAME = "client";
    public static final String DEFAULT_CLIENT_RAW_PASSWORD = "client";
    public static final String DEFAULT_CHIEF_ENGINEERS_USERNAME = "chief_eng";
    public static final String DEFAULT_CHIEF_ENGINEERS_RAW_PASSWORD = "chief_eng";
    @Bean
    public UserDetailsService applicationUserDetailsServiceImpl() {
        return new InMemoryUserDetailsManager(
                this.client(),
                this.chiefEngineer()
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
}