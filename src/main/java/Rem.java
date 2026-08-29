import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Rem {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String LOAD_ERROR_MESSAGE =
            "Rem: Rem couldn't find any tasks... That means we get to start a new one!";
    private static final String SAVE_ERROR_MESSAGE =
            "Rem: Rem is having trouble saving your tasks... "
                    + "Something might eb wrong with your data folder";

    public static void main(String[] args) {
        ArrayList<Task> added;
        boolean hasLoadError = false;
        try {
            added = Storage.loadTasks();
        } catch (IOException e) {
            added = new ArrayList<>();
            hasLoadError = true;
        }

        String banner = " ____                      \n" //Used Codex to generate ASCII
                + "|  _ \\    ___    _ __ ___  \n"
                + "| |_) |  / _ \\  | '_ ` _ \\ \n"
                + "|  _ <  |  __/  | | | | | |\n"
                + "|_| \\_\\  \\___|  |_| |_| |_|\n";

        // Greets the user
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Rem: Hello! I'm Rem!");
        System.out.println("Rem: No more sleeping. Need help?");
        if (hasLoadError) {
            System.out.println(LOAD_ERROR_MESSAGE);
        }
        System.out.println(SEPARATOR);

        //Scanner picks up and responds to user input
        Scanner scanner = new Scanner(System.in);
        commandLoop:
        while (scanner.hasNextLine()) {
            System.out.print("Me: ");
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);
            String trimmedCommand = command.trim();

            try {
                CommandType commandType = getCommandType(trimmedCommand);
                switch (commandType) {
                case BYE:
                    System.out.println("Rem: [Yawn] Need more sleep. Time for bed...");
                    System.out.println(SEPARATOR);
                    break commandLoop;
                case LIST:
                    System.out.println("Rem: Hmm... what to do now?");
                    for (int i = 0; i < added.size(); i++) {
                        System.out.println("Rem: " + (i + 1) + "." + added.get(i));
                    }
                    break;
                case MARK:
                    int taskNumber = parseTaskNumber(trimmedCommand, "mark", added.size());
                    Task task = added.get(taskNumber - 1);
                    task.markAsDone();
                    Storage.saveTasks(added);
                    System.out.println("Rem: We did it! I've marked this task as done:");
                    System.out.println("Rem: " + task);
                    break;
                case UNMARK:
                    taskNumber = parseTaskNumber(trimmedCommand, "unmark", added.size());
                    task = added.get(taskNumber - 1);
                    task.markAsNotDone();
                    Storage.saveTasks(added);
                    System.out.println("Rem: Aww ok... I've marked this task as not done yet:");
                    System.out.println("Rem: " + task);
                    break;
                case DELETE:
                    taskNumber = parseTaskNumber(trimmedCommand, "delete", added.size());
                    Task removedTask = added.remove(taskNumber - 1);
                    Storage.saveTasks(added);
                    System.out.println("Rem: One less thing to do! Removed:");
                    System.out.println("Rem: " + removedTask);
                    System.out.println("Rem: Now we are only left with " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    task = createTask(trimmedCommand);
                    added.add(task);
                    Storage.saveTasks(added);
                    System.out.println("Rem: Ok! I've added this task:");
                    System.out.println("Rem: " + task);
                    System.out.println("Rem: Now you have " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
                    break;
                case UNKNOWN:
                default:
                    throw new UnknownCommandException();
                }
            } catch (RemException e) {
                System.out.println("Rem: " + e.getMessage());
            } catch (IOException e) {
                System.out.println(SAVE_ERROR_MESSAGE);
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
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
