package rem.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.TaskDateTime;
import rem.task.Todo;

/**
 * Loads and saves Rem's tasks using a local data file.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
    }

    /**
     * Loads all tasks from the data file.
     *
     * @return Tasks reconstructed from the saved data, or an empty list if no data file exists.
     * @throws IOException If the data file cannot be read or contains an unknown task type.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            return tasks;
        }

        List<String> taskLines = Files.readAllLines(dataFile);
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
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(toDataLine(task));
        }
        Files.write(dataFile, taskLines);
    }

    /**
     * Converts a task into one line of Rem's storage format.
     *
     * @param task Task to serialize.
     * @return Machine-readable representation of the task.
     */
    private static String toDataLine(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + deadline.getDescription() + " | "
                    + TaskDateTime.formatForStorage(deadline.getBy());
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + event.getDescription() + " | "
                    + TaskDateTime.formatForStorage(event.getFrom()) + " | "
                    + TaskDateTime.formatForStorage(event.getTo());
        }
        return "T | " + status + " | " + task.getDescription();
    }

    /**
     * Reconstructs a task from the fields in one storage line.
     *
     * @param taskParts Fields from the storage line.
     * @param lineNumber One-based line number used in error messages.
     * @return Task represented by the fields.
     * @throws IOException If the fields do not represent a valid task.
     */
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

    /**
     * Checks that a storage line has the required number of non-empty fields.
     *
     * @param taskParts Fields from the storage line.
     * @param expectedCount Required number of fields.
     * @param lineNumber One-based line number used in error messages.
     * @throws IOException If a field is missing or empty.
     */
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

    /**
     * Creates a consistent exception for a malformed storage line.
     *
     * @param lineNumber One-based number of the malformed line.
     * @return Exception describing where invalid data was found.
     */
    private static IOException invalidDataLine(int lineNumber) {
        return new IOException("Invalid task data on line " + lineNumber + ".");
    }
}
