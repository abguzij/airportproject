package kg.airport.airportproject.entity;

import java.util.Objects;

public class UserPositionsTestEntityProvider {
    public static final Long TEST_CLIENT_POSITION_ID = 10L;
    public static final String TEST_CLIENT_POSITION_TITLE = "CLIENT";
    public static final Long TEST_STEWARD_POSITION_ID = 9L;
    public static final String TEST_STEWARD_POSITION_TITLE = "STEWARD";
    public static UserPositionsEntity getTestUserPositionsEntity(Long positionId) {
        if (Objects.isNull(positionId)) {
            throw new IllegalArgumentException("ID позиции пользователя не может быть null или пустым!");
        }

        if (positionId.equals(TEST_CLIENT_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_CLIENT_POSITION_ID)
                    .setPositionTitle(TEST_CLIENT_POSITION_TITLE);
        }
        if (positionId.equals(TEST_STEWARD_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_STEWARD_POSITION_ID)
                    .setPositionTitle(TEST_STEWARD_POSITION_TITLE);
        }
        throw new RuntimeException("Позиций пользователей с таким ID не существует в системе!");
    }
}
