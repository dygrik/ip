import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Rem {
    public static void main(String[] args) {
        ArrayList<Task> added;
        boolean hasLoadError = false;
        try {
            added = Storage.loadTasks();
        } catch (IOException e) {
            added = new ArrayList<>();
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
                    ui.showMessage("[Yawn] Need more sleep. Time for bed...");
                    ui.showSeparator();
                    break commandLoop;
                case LIST:
                    ui.showMessage("Hmm... what to do now?");
                    for (int i = 0; i < added.size(); i++) {
                        ui.showMessage((i + 1) + "." + added.get(i));
                    }
                    break;
                case ON:
                    LocalDate date = Parser.parseDate(trimmedCommand);
                    ArrayList<Task> scheduledTasks = findTasksOn(added, date);
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
                    int taskNumber = Parser.parseTaskNumber(trimmedCommand, "mark", added.size());
                    Task task = added.get(taskNumber - 1);
                    task.markAsDone();
                    Storage.saveTasks(added);
                    ui.showMessage("We did it! I've marked this task as done:");
                    ui.showMessage(task.toString());
                    break;
                case UNMARK:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "unmark", added.size());
                    task = added.get(taskNumber - 1);
                    task.markAsNotDone();
                    Storage.saveTasks(added);
                    ui.showMessage("Aww ok... I've marked this task as not done yet:");
                    ui.showMessage(task.toString());
                    break;
                case DELETE:
                    taskNumber = Parser.parseTaskNumber(trimmedCommand, "delete", added.size());
                    Task removedTask = added.remove(taskNumber - 1);
                    Storage.saveTasks(added);
                    ui.showMessage("One less thing to do! Removed:");
                    ui.showMessage(removedTask.toString());
                    ui.showMessage("Now we are only left with " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    task = Parser.createTask(trimmedCommand);
                    added.add(task);
                    Storage.saveTasks(added);
                    ui.showMessage("Ok! I've added this task:");
                    ui.showMessage(task.toString());
                    ui.showMessage("Now you have " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
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

    // Finds deadlines due and events occurring on the given date.
    private static ArrayList<Task> findTasksOn(ArrayList<Task> tasks, LocalDate date) {
        ArrayList<Task> scheduledTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline && deadline.occursOn(date)
                    || task instanceof Event event && event.occursOn(date)) {
                scheduledTasks.add(task);
            }
        }
        return scheduledTasks;
    }

}
