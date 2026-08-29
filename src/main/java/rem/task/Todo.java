package rem.task;

/**
 * Represents a task without a scheduled date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
