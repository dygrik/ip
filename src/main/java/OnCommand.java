import java.time.LocalDate;
import java.util.List;

/**
 * Displays tasks scheduled on a particular date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that searches a date.
     *
     * @param date Date to search for.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the tasks scheduled on this command's date.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the results.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> scheduledTasks = tasks.findTasksOn(date);
        if (scheduledTasks.isEmpty()) {
            ui.showMessage("Nothing is scheduled on " + TaskDateTime.format(date) + ".");
            return;
        }

        ui.showMessage("Here's what's scheduled on " + TaskDateTime.format(date) + ":");
        for (int i = 0; i < scheduledTasks.size(); i++) {
            ui.showMessage((i + 1) + "." + scheduledTasks.get(i));
        }
    }
}
