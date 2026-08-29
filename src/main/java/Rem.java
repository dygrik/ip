import java.io.IOException;

public class Rem {
    public static void main(String[] args) {
        Storage storage = new Storage("data/rem.txt");
        TaskList tasks;
        boolean hasLoadError = false;
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (IOException e) {
            tasks = new TaskList();
            hasLoadError = true;
        }

        Ui ui = new Ui();
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

}
