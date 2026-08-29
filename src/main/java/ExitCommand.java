/**
 * Ends the current Rem session.
 */
public class ExitCommand extends Command {
    /**
     * Displays Rem's farewell message.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the farewell.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showMessage("[Yawn] Need more sleep. Time for bed...");
    }

    /**
     * Indicates that this command ends the application.
     *
     * @return Always true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
