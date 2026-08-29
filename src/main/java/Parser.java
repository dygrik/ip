import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Interprets user commands and converts their arguments into application values.
 */
public class Parser {
    /**
     * Identifies the command type from the first word of the input.
     *
     * @param input Trimmed user input.
     * @return The matching command type, or {@code UNKNOWN} if the command is not supported.
     */
    public static CommandType getCommandType(String input) {
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

    /**
     * Reads and validates a task number from a command.
     *
     * @param command Full user command.
     * @param commandWord Command word preceding the number.
     * @param taskCount Current number of tasks.
     * @return The validated one-based task number.
     * @throws InvalidTaskNumberException If the argument is not a number for an existing task.
     */
    public static int parseTaskNumber(String command, String commandWord,
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

    /**
     * Reads the date supplied to an {@code on} command.
     *
     * @param command Full user command.
     * @return The parsed date.
     * @throws InvalidDateException If the date is missing or has an invalid format.
     */
    public static LocalDate parseDate(String command) throws InvalidDateException {
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

    /**
     * Creates the task subtype requested by an add command.
     *
     * @param command Full user command.
     * @return The task described by the command.
     * @throws RemException If the task description or date-time arguments are invalid.
     */
    public static Task createTask(String command) throws RemException {
        String lowerCommand = command.toLowerCase();
        if (isCommand(command, "todo")) {
            String description = command.substring(4).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            return new Todo(description);
        }

        if (isCommand(command, "deadline")) {
            return createDeadline(command, lowerCommand);
        }
        return createEvent(command, lowerCommand);
    }

    private static boolean isCommand(String input, String commandWord) {
        String lowerInput = input.toLowerCase();
        return lowerInput.equals(commandWord)
                || lowerInput.startsWith(commandWord + " ");
    }

    private static Deadline createDeadline(String command, String lowerCommand) throws RemException {
        String details = command.substring(8).trim();
        if (details.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        int byIndex = lowerCommand.indexOf(" /by");
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

    private static Event createEvent(String command, String lowerCommand) throws RemException {
        String details = command.substring(5).trim();
        if (details.isEmpty()) {
            throw new EmptyDescriptionException();
        }

        int fromIndex = lowerCommand.indexOf(" /from");
        int toIndex = lowerCommand.indexOf(" /to", Math.max(fromIndex, 0) + 6);
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
