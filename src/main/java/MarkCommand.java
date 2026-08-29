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
     * @param storage Storage used to save the updated task list.
     * @throws IOException If the updated task list cannot be saved.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, InvalidTaskNumberException {
        Task task = tasks.mark(taskNumber);
        storage.saveTasks(tasks.getTasks());
        ui.showMessage("We did it! I've marked this task as done:");
        ui.showMessage(task.toString());
    }
}
