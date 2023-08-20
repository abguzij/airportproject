package kg.airport.airportproject.entity;

public class AircraftSeatsTestEntityProvider {
    public static final Long TEST_SEAT_ID = 1L;
    public static final Integer TEST_ROW_NUMBER = 1;
    public static final Integer TEST_NUMBER_IN_ROW = 1;
    public static final Boolean TEST_SEAT_RESERVED_VALUE = Boolean.TRUE;
    public static final Boolean TEST_SEAT_NOT_RESERVED_VALUE = Boolean.FALSE;

    public static AircraftSeatsEntity getNotReservedAircraftSeatsTestEntity() {
        return new AircraftSeatsEntity()
                .setId(TEST_SEAT_ID)
                .setRowNumber(TEST_ROW_NUMBER)
                .setNumberInRow(TEST_NUMBER_IN_ROW)
                .setReserved(TEST_SEAT_NOT_RESERVED_VALUE);
    }

    public static AircraftSeatsEntity getReservedAircraftSeatsTestEntity() {
        return new AircraftSeatsEntity()
                .setId(TEST_SEAT_ID)
                .setRowNumber(TEST_ROW_NUMBER)
                .setNumberInRow(TEST_NUMBER_IN_ROW)
                .setReserved(TEST_SEAT_RESERVED_VALUE);
    }
}
