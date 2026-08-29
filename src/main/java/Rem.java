import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
                    ui.showMessage("Hmm... what to do now?");
                    for (int i = 1; i <= tasks.size(); i++) {
                        ui.showMessage(i + "." + tasks.getTask(i));
                    }
                    break;
                case ON:
                    LocalDate date = Parser.parseDate(trimmedCommand);
                    List<Task> scheduledTasks = tasks.findTasksOn(date);
                    if (scheduledTasks.isEmpty()) {
                        ui.showMessage("You are free on " + TaskDateTime.format(date) + "!");
                        break;
                    }
                    ui.showMessage("Here's what's to do on " + TaskDateTime.format(date) + ":");
                    for (int i = 0; i < scheduledTasks.size(); i++) {
                        ui.showMessage((i + 1) + "." + scheduledTasks.get(i));
                    }
                    break;
                case MARK:
                    int taskNumber = Parser.parseTaskNumber(trimmedCommand, "mark", tasks.size());
                    Task task = tasks.mark(taskNumber);
                    Storage.saveTasks(tasks.getTasks());
                    ui.showMessage("We did it! I've marked this task as done:");
                    ui.showMessage(task.toString());
                    break;
                case UNMARK:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "unmark", tasks.size());
                    task = tasks.unmark(taskNumber);
                    Storage.saveTasks(tasks.getTasks());
                    ui.showMessage("Aww ok... I've marked this task as not done yet:");
                    ui.showMessage(task.toString());
                    break;
                case DELETE:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "delete", tasks.size());
                    Task removedTask = tasks.delete(taskNumber);
                    Storage.saveTasks(tasks.getTasks());
                    ui.showMessage("One less thing to do! Removed:");
                    ui.showMessage(removedTask.toString());
                    ui.showMessage("Now we are only left with " + tasks.size()
                            + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    task = Parser.createTask(trimmedCommand);
                    tasks.add(task);
                    Storage.saveTasks(tasks.getTasks());
                    ui.showMessage("Ok! I've added this task:");
                    ui.showMessage(task.toString());
                    ui.showMessage("Now you have " + tasks.size()
                            + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
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
