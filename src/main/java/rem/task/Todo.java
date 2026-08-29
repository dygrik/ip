package rem.task;

//A task without a date/ time
public class Todo extends Task {
    //Creates a to-do
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
