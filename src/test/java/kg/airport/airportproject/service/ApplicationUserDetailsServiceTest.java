package kg.airport.airportproject.service;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.service.impl.ApplicationUserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(value = MockitoExtension.class)
public class ApplicationUserDetailsServiceTest {
    @Mock
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    private ApplicationUserDetailsService applicationUserDetailsService;

    @BeforeEach
    private void beforeEach() {
        this.applicationUserDetailsService =
                new ApplicationUserDetailsServiceImpl(this.applicationUsersEntityRepository);
    }
    @Test
    public void testLoadUserByUsername_OK() {
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(
                        Mockito.eq(ApplicationUsersTestEntityProvider.TEST_CLIENT_USERNAME)
                ))
                .thenAnswer(invocationOnMock -> Optional.of(ApplicationUsersTestEntityProvider.getTestClientEntity()));
        try {
            UserDetails userDetails = this.applicationUserDetailsService
                    .loadUserByUsername(ApplicationUsersTestEntityProvider.TEST_CLIENT_USERNAME);

            Assertions.assertTrue(userDetails instanceof ApplicationUsersEntity);
            Assertions.assertEquals(ApplicationUsersTestEntityProvider.TEST_CLIENT_USERNAME, userDetails.getUsername());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testLoadUserByUsername_UsernameNotFound() {
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(
                        Mockito.eq(ApplicationUsersTestEntityProvider.TEST_CLIENT_USERNAME)
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Assertions.assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> this.applicationUserDetailsService.loadUserByUsername(
                        ApplicationUsersTestEntityProvider.TEST_CLIENT_USERNAME
                ),
                "Пользователь с таким именем не найден или был удален!"
        );
    }

    @Test
    public void testLoadUserByUsername_NullUsername() {
        Assertions.assertThrowsExactly(
                IllegalArgumentException.class,
                () -> this.applicationUserDetailsService.loadUserByUsername(null),
                "Ошибка: Имя пользователя не может быть null или пустым!"
        );
    }
}