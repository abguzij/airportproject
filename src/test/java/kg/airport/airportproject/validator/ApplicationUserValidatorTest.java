package kg.airport.airportproject.validator;

import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserTestDtoProvider;
import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;
import kg.airport.airportproject.entity.UserPositionsTestEntityProvider;
import kg.airport.airportproject.exception.InvalidCredentialsException;
import kg.airport.airportproject.exception.UsernameAlreadyExistsException;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.validator.impl.ApplicationUserValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(value = MockitoExtension.class)
public class ApplicationUserValidatorTest {
    @Mock
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    private ApplicationUserValidator applicationUserValidator;

    @BeforeEach
    private void beforeEach() {
        this.applicationUserValidator = new ApplicationUserValidatorImpl(this.applicationUsersEntityRepository);
    }

    @Test
    public void testValidateUserRequestDto_OK() {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider.getTestApplicationUserRequestDto(
                UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID
        );

        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(
                        Mockito.eq(requestDto.getUsername())
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        try {
            this.applicationUserValidator.validateUserRequestDto(requestDto);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateUserRequestDto_InvalidCredentials() {
        ApplicationUserRequestDto requestDto = ApplicationUserTestDtoProvider.getTestApplicationUserRequestDto(
                UserPositionsTestEntityProvider.TEST_CLIENT_POSITION_ID
        );
        requestDto.setUsername("");

        Exception exception = Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> this.applicationUserValidator.validateUserRequestDto(requestDto)
        );
        Assertions.assertEquals(
                "Имя пользователя не может быть null или пустым!",
                exception.getMessage()
        );
    }

    @Test
    public void testValidateUserRequestDto_NullRequestDto() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.applicationUserValidator.validateUserRequestDto(null)
        );
        Assertions.assertEquals(
                "Создаваемый пользователь не может быть null!",
                exception.getMessage()
        );
    }

    @Test
    public void testCheckUsernameForDuplicates_OK() {
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(
                        Mockito.eq(ApplicationUsersTestEntityProvider.TEST_USERNAME)
                ))
                .thenAnswer(invocationOnMock -> Optional.empty());

        try {
            this.applicationUserValidator.checkUsernameForDuplicates(ApplicationUsersTestEntityProvider.TEST_USERNAME);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    // UsernameAlreadyExistsException
    @Test
    public void testCheckUsernameForDuplicates_UsernameAlreadyExists() {
        Mockito
                .when(this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(
                        Mockito.eq(ApplicationUsersTestEntityProvider.TEST_USERNAME)
                ))
                .thenAnswer(invocationOnMock -> Optional.of(ApplicationUsersTestEntityProvider.getTestClientEntity()));

        Exception exception = Assertions.assertThrowsExactly(
                UsernameAlreadyExistsException.class,
                () -> this.applicationUserValidator.checkUsernameForDuplicates(
                        ApplicationUsersTestEntityProvider.TEST_USERNAME
                ),
                "Пользователь с таким именем уже существует в системе!"
        );
    }
}