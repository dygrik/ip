import java.io.IOException;

public class Rem {
    public static void main(String[] args) {
        TaskList tasks;
        boolean hasLoadError = false;
        try {
            tasks = new TaskList(Storage.loadTasks());
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
                parsedCommand.execute(tasks, ui);
                isExit = parsedCommand.isExit();
            } catch (RemException e) {
                ui.showMessage(e.getMessage());
            } catch (IOException e) {
                ui.showMessage("Rem is having trouble saving your tasks... "
                        + "Something might be wrong with your data folder");
            } finally {
                ui.showSeparator();
            }
        }

        ui.close();
    }

}
