import java.io.IOException;

/**
 * Represents an instruction that can be executed by Rem.
 */
public abstract class Command {
    /**
     * Executes this command.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display command results.
     * @param storage Storage used to save task-list changes.
     * @throws IOException If the command cannot save a task-list change.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws IOException;

    /**
     * Returns whether this command should end the application.
     *
     * @return True if Rem should stop after executing this command.
     */
    public boolean isExit() {
        return false;
    }
}
