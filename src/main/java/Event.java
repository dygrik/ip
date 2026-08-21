//A task that happens between a start and end date/time
public class Event extends Task {
    protected String from;
    protected String to;

    //Creates an event
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
