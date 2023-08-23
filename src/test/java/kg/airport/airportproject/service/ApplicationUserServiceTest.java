package kg.airport.airportproject.service;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.service.impl.ApplicationUserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = MockitoExtension.class)
public class ApplicationUserServiceTest {
    private static final PasswordEncoder mockingPasswordEncoder = new BCryptPasswordEncoder(8);
    @Mock
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Mock
    private UserPositionsEntityRepository userPositionsEntityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ApplicationUserService applicationUserService;

    @BeforeEach
    public void beforeEach() {
        this.applicationUserService = new ApplicationUserServiceImpl(
                this.applicationUsersEntityRepository,
                this.userPositionsEntityRepository,
                this.passwordEncoder
        );
    }
    @Test
    public void testGetApplicationUserById_OK() {
        ApplicationUsersEntity usersEntity = ApplicationUsersTestEntityProvider.getTestClientEntity();
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityById(
                        Mockito.eq(usersEntity.getId())
                ))
                .thenAnswer(invocationOnMock -> Optional.of(usersEntity));
        try {
            ApplicationUsersEntity result =
                    this.applicationUserService.getApplicationUserById(usersEntity.getId());

            Assertions.assertEquals(usersEntity.getId(), result.getId());
            Assertions.assertEquals(usersEntity.getUsername(), result.getUsername());
            Assertions.assertEquals(usersEntity.getUserRolesEntityList(), result.getUserRolesEntityList());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetApplicationUserById_UserNotFound() {
        ApplicationUsersEntity usersEntity = ApplicationUsersTestEntityProvider.getTestClientEntity();
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityById(
                        Mockito.eq(usersEntity.getId())
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Exception exception = Assertions.assertThrows(
                ApplicationUserNotFoundException.class,
                () -> this.applicationUserService.getApplicationUserById(usersEntity.getId())
        );
        Assertions.assertEquals(
                String.format("Пользователь с ID[%d] не найден в системе", usersEntity.getId()),
                exception.getMessage()
        );
    }

    @Test
    public void testGetApplicationUserById_InvalidId() {
        Exception exception = Assertions.assertThrows(
                InvalidIdException.class,
                () -> this.applicationUserService.getApplicationUserById(0L)
        );
        Assertions.assertEquals(
                "ID пользователя не может быть меньше 1!",
                exception.getMessage()
        );
    }
}