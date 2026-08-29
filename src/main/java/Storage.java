import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Rem's tasks using a local data file.
 */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "rem.txt");

    /**
     * Loads all tasks from the data file.
     *
     * @return Tasks reconstructed from the saved data, or an empty list if no data file exists.
     * @throws IOException If the data file cannot be read or contains an unknown task type.
     */
    public static ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(DATA_FILE)) {
            return tasks;
        }

        List<String> taskLines = Files.readAllLines(DATA_FILE);
        for (int i = 0; i < taskLines.size(); i++) {
            String taskLine = taskLines.get(i);
            if (taskLine.isBlank()) {
                continue;
            }

            String[] taskParts = taskLine.split(" \\| ", -1);
            Task task = createTask(taskParts, i + 1);
            if (taskParts[1].equals("1")) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Writes the complete task list to disk, replacing the previously saved list.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public static void saveTasks(List<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(toDataLine(task));
        }
        Files.write(DATA_FILE, taskLines);
    }

    private static String toDataLine(Task task) {
        String status = task.isDone ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + deadline.description + " | "
                    + TaskDateTime.formatForStorage(deadline.by);
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + event.description + " | "
                    + TaskDateTime.formatForStorage(event.from) + " | "
                    + TaskDateTime.formatForStorage(event.to);
        }
        return "T | " + status + " | " + task.description;
    }

    private static Task createTask(String[] taskParts, int lineNumber) throws IOException {
        if (taskParts.length < 2 || (!taskParts[1].equals("0") && !taskParts[1].equals("1"))) {
            throw invalidDataLine(lineNumber);
        }

        return switch (taskParts[0]) {
        case "T" -> {
            validateParts(taskParts, 3, lineNumber);
            yield new Todo(taskParts[2]);
        }
        case "D" -> {
            validateParts(taskParts, 4, lineNumber);
            try {
                yield new Deadline(taskParts[2], TaskDateTime.parse(taskParts[3]));
            } catch (DateTimeParseException e) {
                throw invalidDataLine(lineNumber);
            }
        }
        case "E" -> {
            validateParts(taskParts, 5, lineNumber);
            try {
                yield new Event(taskParts[2], TaskDateTime.parse(taskParts[3]),
                        TaskDateTime.parse(taskParts[4]));
            } catch (DateTimeParseException e) {
                throw invalidDataLine(lineNumber);
            }
        }
        default -> throw invalidDataLine(lineNumber);
        };
    }

    private static void validateParts(String[] taskParts, int expectedCount,
            int lineNumber) throws IOException {
        if (taskParts.length != expectedCount) {
            throw invalidDataLine(lineNumber);
        }
        for (int i = 2; i < taskParts.length; i++) {
            if (taskParts[i].isBlank()) {
                throw invalidDataLine(lineNumber);
            }
        }
    }

    private static IOException invalidDataLine(int lineNumber) {
        return new IOException("Invalid task data on line " + lineNumber + ".");
    }
}
