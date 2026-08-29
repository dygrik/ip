import java.io.IOException;

/**
 * Coordinates Rem's user interface, task list, storage, and commands.
 */
public class Rem {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final boolean hasLoadError;

    /**
     * Creates Rem and loads tasks from the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Rem(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();

        TaskList loadedTasks;
        boolean didLoadingFail = false;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (IOException e) {
            loadedTasks = new TaskList();
            didLoadingFail = true;
        }
        tasks = loadedTasks;
        hasLoadError = didLoadingFail;
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome(hasLoadError);

        boolean isExit = false;
        while (ui.hasNextCommand() && !isExit) {
            String command = ui.readCommand();

            try {
                Command parsedCommand = Parser.parse(command, tasks.size());
                parsedCommand.execute(tasks, ui, storage);
                isExit = parsedCommand.isExit();
            } catch (RemException e) {
                ui.showMessage(e.getMessage());
            } catch (IOException e) {
                ui.showMessage("I couldn't save your tasks. Please check the data folder.");
            } finally {
                ui.showSeparator();
            }
        }

        ui.close();
    }

    /**
     * Starts Rem using its default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Rem("data/rem.txt").run();
    }
}
