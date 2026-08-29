import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Creates a deadline.
     *
     * @param description Description of the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Checks whether this deadline is due on a given date.
     *
     * @param date Date to check.
     * @return True if the deadline is due on that date.
     */
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDateTime.format(by) + ")";
    }
}
