package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(value = "classpath:test.properties")
public class ApplicationUserDetailsServiceTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationUserDetailsService applicationUserDetailsService;
    @Autowired
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Test
    void testLoadUserByUsername_OK() {
        ApplicationUsersEntity client = this.createClientEntityByParameters("test", "test");
        this.applicationUsersEntityRepository.save(client);

        try {
            UserDetails result = this.applicationUserDetailsService.loadUserByUsername("test");

            Assertions.assertEquals("test", result.getUsername());
            Assertions.assertEquals(client.getPassword(), result.getPassword());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private ApplicationUsersEntity createClientEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(10L).setPositionTitle("CLIENT"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));

        return applicationUsersEntity;
    }
}