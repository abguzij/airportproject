package kg.airport.airportproject.service;

import io.jsonwebtoken.Jwts;
import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserCredentialsRequestDto;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.dto.JwtTokenResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.security.JwtTokenHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Test
    public void testRegisterNewClient_OK() {
        ApplicationUserRequestDto requestDto = new ApplicationUserRequestDto();
        requestDto
                .setPassword("test_pw")
                .setUsername("test_un")
                .setFullName("test_fn")
                .setPositionId(10L);

        try {
            ApplicationUserResponseDto responseDto = this.authenticationService.registerNewClient(requestDto);

            Assertions.assertEquals("test_un", responseDto.getUsername());
            Assertions.assertEquals("test_fn", responseDto.getFullName());
            Assertions.assertEquals("CLIENT", responseDto.getPositionTitle());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterNewEmployee_OK() {
        ApplicationUserRequestDto requestDto = new ApplicationUserRequestDto();
        requestDto
                .setPassword("test_emp_pw")
                .setUsername("test_emp_un")
                .setFullName("test_emp_fn")
                .setPositionId(9L);

        try {
            ApplicationUserResponseDto responseDto = this.authenticationService.registerNewEmployee(requestDto);

            Assertions.assertEquals("test_emp_un", responseDto.getUsername());
            Assertions.assertEquals("test_emp_fn", responseDto.getFullName());
            Assertions.assertEquals("STEWARD", responseDto.getPositionTitle());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testLogin_OK() {

        ApplicationUserRequestDto requestDto = new ApplicationUserRequestDto();
        requestDto
                .setPassword("test_pw")
                .setUsername("test_un")
                .setFullName("test_fn")
                .setPositionId(10L);

        ApplicationUsersEntity applicationUser = new ApplicationUsersEntity();
        applicationUser.setUsername("test_un");

        ApplicationUserCredentialsRequestDto credentialsRequestDto = new ApplicationUserCredentialsRequestDto();
        credentialsRequestDto
                .setUsername("test_un")
                .setPassword("test_pw");

        try {
            this.authenticationService.registerNewEmployee(requestDto);

            JwtTokenResponseDto jwtTokenResponseDto = this.authenticationService.login(credentialsRequestDto);

            String jwtToken = this.jwtTokenHandler.generateToken(
                    UsernamePasswordAuthenticationToken.authenticated(
                            applicationUser,
                            null,
                            applicationUser.getAuthorities()
                    )
            );

            Assertions.assertEquals(jwtToken, jwtTokenResponseDto.getJwtToken());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}