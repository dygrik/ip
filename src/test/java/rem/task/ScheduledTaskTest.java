package rem.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for deadlines and events.
 */
public class ScheduledTaskTest {
    @Test
    public void deadlineOccursOn_dueDate_trueReturned() {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 29, 23, 59));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 8, 29)));
    }

    @Test
    public void deadlineOccursOn_otherDate_falseReturned() {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 29, 23, 59));

        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void eventOccursOn_startMiddleAndEndDates_trueReturned() {
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 23, 0),
                LocalDateTime.of(2026, 8, 30, 1, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 28)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void eventOccursOn_beforeOrAfterEvent_falseReturned() {
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 23, 0),
                LocalDateTime.of(2026, 8, 30, 1, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 27)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
    }
}
