package kg.airport.airportproject.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RegistrationDateTestFiltersProvider {
    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public static final LocalDateTime TEST_START_DATE_FILTER =
            LocalDateTime.parse("2001-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_END_DATE_FILTER =
            LocalDateTime.parse("2020-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_VALID_REGISTRATION_DATE =
            LocalDateTime.parse("2010-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_DATE_BEFORE_START_FILTER =
            LocalDateTime.parse("2000-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_DATE_AFTER_END_FILTER =
            LocalDateTime.parse("2022-04-20T12:00:00", isoFormatter);

    public static LocalDateTime getDateTimeFromString(String localDateTimeStr) {
        if(Objects.isNull(localDateTimeStr) || localDateTimeStr.isEmpty()) {
            throw new IllegalArgumentException("Строка с датой не может быть null или пустой!");
        }
        return LocalDateTime.parse(localDateTimeStr, isoFormatter);
    }
}
