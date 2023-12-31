package kg.airport.airportproject.dto;

import kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider;

import java.time.LocalDateTime;

import static kg.airport.airportproject.entity.ApplicationUsersTestEntityProvider.*;

public class ApplicationUserTestDtoProvider {

    public static ApplicationUserRequestDto getTestApplicationUserRequestDto(Long positionId) {
        return new ApplicationUserRequestDto()
                .setUsername(TEST_CLIENT_USERNAME)
                .setPassword(TEST_CLIENT_RAW_PASSWORD)
                .setFullName(TEST_CLIENT_FULL_NAME)
                .setPositionId(positionId);
    }

    public static ApplicationUserResponseDto getTestApplicationUserResponseDto(String positionTitle) {
        return new ApplicationUserResponseDto()
                .setId(ApplicationUsersTestEntityProvider.TEST_CLIENT_USER_ID)
                .setUsername(TEST_CLIENT_USERNAME)
                .setFullName(TEST_CLIENT_FULL_NAME)
                .setPositionTitle(positionTitle)
                .setEnabled(true)
                .setRegisteredAt(LocalDateTime.now());
    }
}
