package rem.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests the supported date and time inputs parsed by {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_isoDate_dateAtMidnightReturned() {
        assertEquals(LocalDateTime.of(2026, 8, 29, 0, 0),
                TaskDateTime.parse("2026-08-29"));
    }

    @Test
    public void parse_isoDateWithTime_dateAndTimeReturned() {
        assertEquals(LocalDateTime.of(2026, 8, 29, 14, 30),
                TaskDateTime.parse("2026-08-29 1430"));
    }

    @Test
    public void parse_slashSeparatedDate_dateAtMidnightReturned() {
        assertEquals(LocalDateTime.of(2026, 8, 29, 0, 0),
                TaskDateTime.parse("29/8/2026"));
    }

    @Test
    public void parse_slashSeparatedDateWithTime_dateAndTimeReturned() {
        assertEquals(LocalDateTime.of(2026, 8, 29, 9, 5),
                TaskDateTime.parse("29/8/2026 0905"));
    }

    @Test
    public void parse_validLeapDay_leapDayReturned() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0),
                TaskDateTime.parse("2024-02-29"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_impossibleDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("2023-13-01"));
    }

    @Test
    public void parse_invalidTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("2026-08-29 2500"));
    }

    @Test
    public void parse_unsupportedFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("29 Aug 2026"));
    }

    @Test
    public void parse_trailingText_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("2026-08-29 tomorrow"));
    }
}
