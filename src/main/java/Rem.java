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

        commandLoop:
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            String trimmedCommand = command.trim();

            try {
                CommandType commandType = Parser.getCommandType(trimmedCommand);
                switch (commandType) {
                case BYE:
                    Command exitCommand = new ExitCommand();
                    exitCommand.execute(tasks, ui);
                    ui.showSeparator();
                    if (exitCommand.isExit()) {
                        break commandLoop;
                    }
                    break;
                case LIST:
                    Command listCommand = new ListCommand();
                    listCommand.execute(tasks, ui);
                    break;
                case ON:
                    Command onCommand = new OnCommand(Parser.parseDate(trimmedCommand));
                    onCommand.execute(tasks, ui);
                    break;
                case MARK:
                    int taskNumber = Parser.parseTaskNumber(trimmedCommand, "mark", tasks.size());
                    Command markCommand = new MarkCommand(taskNumber);
                    markCommand.execute(tasks, ui);
                    break;
                case UNMARK:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "unmark", tasks.size());
                    Command unmarkCommand = new UnmarkCommand(taskNumber);
                    unmarkCommand.execute(tasks, ui);
                    break;
                case DELETE:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "delete", tasks.size());
                    Command deleteCommand = new DeleteCommand(taskNumber);
                    deleteCommand.execute(tasks, ui);
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    Task task = Parser.createTask(trimmedCommand);
                    Command addCommand = new AddCommand(task);
                    addCommand.execute(tasks, ui);
                    break;
                case UNKNOWN:
                default:
                    throw new UnknownCommandException();
                }
            } catch (RemException e) {
                ui.showMessage(e.getMessage());
            } catch (IOException e) {
                ui.showMessage("Rem is having trouble saving your tasks... "
                        + "Something might be wrong with your data folder");
            }

            ui.showSeparator();
        }

        ui.close();
    }

}
