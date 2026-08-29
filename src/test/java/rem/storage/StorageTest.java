package rem.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.Todo;

/**
 * Tests persistence and validation of Rem's task data.
 */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_emptyListReturned() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypesAndStatuses_roundTripPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 29, 18, 0));
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 30, 9, 0),
                LocalDateTime.of(2026, 8, 31, 17, 0));

        storage.saveTasks(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals(deadline.getBy(), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
    }

    @Test
    public void saveTasks_existingFile_previousContentsReplaced() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.saveTasks(List.of(new Todo("old"), new Todo("extra")));

        storage.saveTasks(List.of(new Todo("new")));

        assertEquals(List.of("T | 0 | new"), Files.readAllLines(dataFile));
    }

    @Test
    public void loadTasks_blankLines_blankLinesIgnored() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | read book\n\n");
        Storage storage = new Storage(dataFile.toString());

        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    public void loadTasks_malformedLines_exceptionIdentifiesLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | valid\nD | maybe | broken\n");
        Storage storage = new Storage(dataFile.toString());

        IOException exception = assertThrows(IOException.class, storage::loadTasks);
        assertEquals("Invalid task data on line 2.", exception.getMessage());
    }

    @Test
    public void loadTasks_unknownTypeOrInvalidDate_exceptionThrown() throws IOException {
        Path unknownTypeFile = temporaryDirectory.resolve("unknown.txt");
        Files.writeString(unknownTypeFile, "X | 0 | task\n");
        Path invalidDateFile = temporaryDirectory.resolve("invalid-date.txt");
        Files.writeString(invalidDateFile, "D | 0 | task | tomorrow\n");

        assertThrows(IOException.class, () -> new Storage(unknownTypeFile.toString()).loadTasks());
        assertThrows(IOException.class, () -> new Storage(invalidDateFile.toString()).loadTasks());
    }
}
