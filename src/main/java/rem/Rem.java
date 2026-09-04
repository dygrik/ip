package rem;

import java.io.IOException;

import rem.command.Command;
import rem.exception.RemException;
import rem.parser.Parser;
import rem.storage.Storage;
import rem.task.TaskList;
import rem.ui.Ui;

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
        while (!isExit) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }

            isExit = execute(command, ui);
            ui.showSeparator();
        }

        ui.close();
    }

    /**
     * Returns the greeting and any warning about loading saved tasks.
     *
     * @return Initial graphical conversation message.
     */
    public String getWelcome() {
        return "Hello! I'm Rem!\nNo more sleeping. Need help?"
                + (hasLoadError ? "\nRem found nothing... Guess I'll start a new one!" : "");
    }

    /**
     * Processes one graphical input using the same commands as the console.
     *
     * @param input User command.
     * @return Collected response and whether the conversation should end.
     */
    public Response getResponse(String input) {
        StringBuilder messages = new StringBuilder();
        Ui responseUi = new Ui(message -> {
            if (!messages.isEmpty()) {
                messages.append('\n');
            }
            messages.append(message);
        });
        boolean isExit = execute(input, responseUi);
        return new Response(messages.toString(), isExit);
    }

    /**
     * Executes a command and translates validation or storage errors into messages.
     */
    private boolean execute(String input, Ui targetUi) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, targetUi, storage);
            return command.isExit();
        } catch (RemException e) {
            targetUi.showMessage(e.getMessage());
        } catch (IOException e) {
            targetUi.showMessage("Rem couldn't save the tasks... Could you check the data folder?");
        }
        return false;
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
