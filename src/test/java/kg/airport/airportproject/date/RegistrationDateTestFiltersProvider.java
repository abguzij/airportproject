package kg.airport.airportproject.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistrationDateTestFiltersProvider {
    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public static final LocalDateTime TEST_START_DATE_FILTER =
            LocalDateTime.parse("2001-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_END_DATE_FILTER =
            LocalDateTime.parse("2010-04-20T12:00:00", isoFormatter);
    public static final LocalDateTime TEST_REGISTRATION_DATE =
            LocalDateTime.parse("2020-04-20T12:00:00", isoFormatter);
}
