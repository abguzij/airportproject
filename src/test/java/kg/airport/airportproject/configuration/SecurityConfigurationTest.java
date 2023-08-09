package kg.airport.airportproject.configuration;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestConfiguration
@TestPropertySource(value = "classpath:test.properties")
public class SecurityConfigurationTest {
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new InMemoryUserDetailsManager(
//                this.client()
//        );
//    }
//
//    private ApplicationUsersEntity client() {
//        ApplicationUsersEntity client = new ApplicationUsersEntity()
//                .setUsername("client")
//                .setPassword("client")
//                .setFullName("Default Client")
//                .setId(1L)
//                .setEnabled(true);
//        client.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));
//        return client;
//    }
}