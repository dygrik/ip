package rem.command;

import java.util.List;

import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks with descriptions matching this command's keyword.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the results.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findTasks(keyword);
        ui.showMessage("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            ui.showMessage((i + 1) + "." + matchingTasks.get(i));
        }
    }
}
