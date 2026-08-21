public class Task {
    protected String description;
    protected boolean isDone;

    //Creates a task with a given description, by default Task is not done
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    //Returns the icon used to show whether this task is complete
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    //Marks task as complete
    public void markAsDone() {
        this.isDone = true;
    }

    //Marks task as incomplete
    public void markAsNotDone() {
        this.isDone = false;
    }

    //Prints status and description of task
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
