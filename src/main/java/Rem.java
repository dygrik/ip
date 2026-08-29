import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
                CommandType commandType = getCommandType(trimmedCommand);
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
                    LocalDate date = parseDate(trimmedCommand);
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
                    int taskNumber = parseTaskNumber(trimmedCommand, "mark", added.size());
                    Task task = added.get(taskNumber - 1);
                    task.markAsDone();
                    Storage.saveTasks(added);
                    ui.showMessage("We did it! I've marked this task as done:");
                    ui.showMessage(task.toString());
                    break;
                case UNMARK:
                    taskNumber = parseTaskNumber(trimmedCommand, "unmark", added.size());
                    task = added.get(taskNumber - 1);
                    task.markAsNotDone();
                    Storage.saveTasks(added);
                    ui.showMessage("Aww ok... I've marked this task as not done yet:");
                    ui.showMessage(task.toString());
                    break;
                case DELETE:
                    taskNumber = parseTaskNumber(trimmedCommand, "delete", added.size());
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
                    task = createTask(trimmedCommand);
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

    //Identifies the appropriate CommandType from the first word of the user's input
    private static CommandType getCommandType(String input) {
        if (input.isBlank()) {
            return CommandType.UNKNOWN;
        }

        String commandWord = input.split("\\s+", 2)[0].toUpperCase();
        try {
            return CommandType.valueOf(commandWord);
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    //Checks whether the input begins with the given command word
    private static boolean isCommand(String input, String commandWord) {
        String lowerInput = input.toLowerCase();
        return lowerInput.equals(commandWord)
                || lowerInput.startsWith(commandWord + " ");
    }

    //Reads and validates the task number
    private static int parseTaskNumber(String command, String commandWord,
            int taskCount) throws InvalidTaskNumberException {
        String numberText = command.substring(commandWord.length()).trim();
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new InvalidTaskNumberException();
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException();
        }
    }

    // Reads the date supplied to the on command.
    private static LocalDate parseDate(String command) throws InvalidDateException {
        String dateText = command.substring(2).trim();
        if (dateText.isEmpty() || dateText.contains(" ")) {
            throw new InvalidDateException();
        }
        try {
            return TaskDateTime.parse(dateText).toLocalDate();
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
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

    //Creates the task subtype requested by a command word
    private static Task createTask(String command) throws RemException {
        String c = command.toLowerCase();
        if (isCommand(command, "todo")) {
            String description = command.substring(4).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            return new Todo(description);
        }

        if (isCommand(command, "deadline")) {
            String details = command.substring(8).trim();
            if (details.isEmpty()) {
                throw new EmptyDescriptionException();
            }

            int byIndex = c.indexOf(" /by");
            if (byIndex < 0) {
                throw new InvalidDeadlineFormatException();
            }

            String description = command.substring(8, byIndex).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }

            String by = command.substring(byIndex + 4).trim();
            if (by.isEmpty()) {
                throw new InvalidDeadlineFormatException();
            }
            try {
                return new Deadline(description, TaskDateTime.parse(by));
            } catch (DateTimeParseException e) {
                throw new InvalidDeadlineFormatException();
            }
        }

        String details = command.substring(5).trim();
        if (details.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        int fromIndex = c.indexOf(" /from");
        int toIndex = c.indexOf(" /to", Math.max(fromIndex, 0) + 6);
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new InvalidEventFormatException();
        }

        if (fromIndex <= 5) {
            throw new EmptyDescriptionException();
        }
        String description = command.substring(6, fromIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        String from = command.substring(fromIndex + 6, toIndex).trim();
        String to = command.substring(toIndex + 4).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidEventFormatException();
        }
        try {
            LocalDateTime startDateTime = TaskDateTime.parse(from);
            LocalDateTime endDateTime = TaskDateTime.parse(to);
            if (endDateTime.isBefore(startDateTime)) {
                throw new InvalidEventFormatException();
            }
            return new Event(description, startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            throw new InvalidEventFormatException();
        }
    }
}
