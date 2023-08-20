package kg.airport.airportproject.entity;

import java.util.List;
import java.util.Objects;

public class UserPositionsTestEntityProvider {
    public static final Long TEST_CLIENT_POSITION_ID = 10L;
    public static final String TEST_CLIENT_POSITION_TITLE = "CLIENT";
    public static final Long TEST_STEWARD_POSITION_ID = 9L;
    public static final String TEST_STEWARD_POSITION_TITLE = "STEWARD";
    public static final Long TEST_CHIEF_STEWARD_POSITION_ID = 8L;
    public static final String TEST_CHIEF_STEWARD_POSITION_TITLE = "CHIEF_STEWARD";
    public static final Long TEST_PILOT_POSITION_ID = 7L;
    public static final String TEST_PILOT_POSITION_TITLE = "PILOT";
    public static final Long TEST_ENGINEER_POSITION_ID = 6L;
    public static final String TEST_ENGINEER_POSITION_TITLE = "ENGINEER";
    public static final Long TEST_CHIEF_ENGINEER_POSITION_ID = 5L;
    public static final String TEST_CHIEF_ENGINEER_POSITION_TITLE = "CHIEF_ENGINEER";
    public static final Long TEST_DISPATCHER_POSITION_ID = 4L;
    public static final String TEST_DISPATCHER_POSITION_TITLE = "DISPATCHER";
    public static final Long TEST_CHIEF_DISPATCHER_POSITION_ID = 3L;
    public static final String TEST_CHIEF_DISPATCHER_POSITION_TITLE = "CHIEF_DISPATCHER";
    public static final Long TEST_AIRPORT_MANAGER_POSITION_ID = 2L;
    public static final String TEST_AIRPORT_MANAGER_POSITION_TITLE = "AIRPORT_MANAGER";
    public static final Long TEST_SYSTEM_ADMINISTRATOR_POSITION_ID = 1L;
    public static final String TEST_SYSTEM_ADMINISTRATOR_POSITION_TITLE = "SYSTEM_ADMINISTRATOR";
    
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
        if (positionId.equals(TEST_CHIEF_STEWARD_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_CHIEF_STEWARD_POSITION_ID)
                    .setPositionTitle(TEST_CHIEF_STEWARD_POSITION_TITLE);
        }
        if (positionId.equals(TEST_PILOT_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_PILOT_POSITION_ID)
                    .setPositionTitle(TEST_PILOT_POSITION_TITLE);
        }
        if (positionId.equals(TEST_ENGINEER_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_ENGINEER_POSITION_ID)
                    .setPositionTitle(TEST_ENGINEER_POSITION_TITLE);
        }
        if (positionId.equals(TEST_CHIEF_ENGINEER_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_CHIEF_ENGINEER_POSITION_ID)
                    .setPositionTitle(TEST_CHIEF_ENGINEER_POSITION_TITLE);
        }
        if (positionId.equals(TEST_DISPATCHER_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_DISPATCHER_POSITION_ID)
                    .setPositionTitle(TEST_DISPATCHER_POSITION_TITLE);
        }
        if (positionId.equals(TEST_CHIEF_DISPATCHER_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_CHIEF_DISPATCHER_POSITION_ID)
                    .setPositionTitle(TEST_CHIEF_DISPATCHER_POSITION_TITLE);
        }
        if (positionId.equals(TEST_AIRPORT_MANAGER_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_AIRPORT_MANAGER_POSITION_ID)
                    .setPositionTitle(TEST_AIRPORT_MANAGER_POSITION_TITLE);
        }
        if (positionId.equals(TEST_SYSTEM_ADMINISTRATOR_POSITION_ID)) {
            return new UserPositionsEntity()
                    .setId(TEST_SYSTEM_ADMINISTRATOR_POSITION_ID)
                    .setPositionTitle(TEST_SYSTEM_ADMINISTRATOR_POSITION_TITLE);
        }
        throw new RuntimeException("Позиций пользователей с таким ID не существует в системе!");
    }

    public static List<UserPositionsEntity> getAllTestEmployeesPositionsEntities() {
        return List.of(
                new UserPositionsEntity()
                        .setId(TEST_STEWARD_POSITION_ID)
                        .setPositionTitle(TEST_STEWARD_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_CHIEF_STEWARD_POSITION_ID)
                        .setPositionTitle(TEST_CHIEF_STEWARD_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_PILOT_POSITION_ID)
                        .setPositionTitle(TEST_PILOT_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_ENGINEER_POSITION_ID)
                        .setPositionTitle(TEST_ENGINEER_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_CHIEF_ENGINEER_POSITION_ID)
                        .setPositionTitle(TEST_CHIEF_ENGINEER_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_DISPATCHER_POSITION_ID)
                        .setPositionTitle(TEST_DISPATCHER_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_CHIEF_DISPATCHER_POSITION_ID)
                        .setPositionTitle(TEST_CHIEF_DISPATCHER_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_AIRPORT_MANAGER_POSITION_ID)
                        .setPositionTitle(TEST_AIRPORT_MANAGER_POSITION_TITLE),
                new UserPositionsEntity()
                        .setId(TEST_SYSTEM_ADMINISTRATOR_POSITION_ID)
                        .setPositionTitle(TEST_SYSTEM_ADMINISTRATOR_POSITION_TITLE)
        );
    }
}
