package rem.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that happens between a start and end date/time.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description Description of the event.
     * @param from Date and time at which the event starts.
     * @param to Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether this event overlaps a given date.
     *
     * @param date Date to check.
     * @return True if the event occurs at any time on that date.
     */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDateTime.format(from)
                + " to: " + TaskDateTime.format(to) + ")";
    }
}
