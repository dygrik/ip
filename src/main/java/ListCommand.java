/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the tasks in their current order.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showMessage("Hmm... what to do now?");
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            ui.showMessage(taskNumber + "." + tasks.getTask(taskNumber));
        }
    }
}
