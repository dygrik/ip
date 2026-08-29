import java.io.IOException;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a selected task.
     *
     * @param taskNumber One-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task, saves the task list, and displays the result.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the result.
     * @throws IOException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        Task task = tasks.mark(taskNumber);
        Storage.saveTasks(tasks.getTasks());
        ui.showMessage("We did it! I've marked this task as done:");
        ui.showMessage(task.toString());
    }
}
