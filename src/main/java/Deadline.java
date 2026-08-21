//A task that must be completed by a specific date/time
public class Deadline extends Task {
    protected String by;

    //Creates a deadline
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
