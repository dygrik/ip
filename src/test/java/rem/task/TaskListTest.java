package rem.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import rem.exception.InvalidTaskNumberException;

/**
 * Tests the core task mutation and scheduling operations of {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void add_multipleTasks_tasksAppendedInOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.getTask(1));
        assertSame(second, tasks.getTask(2));
    }

    @Test
    public void delete_validTaskNumber_taskRemovedAndReturned() throws InvalidTaskNumberException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertSame(first, tasks.delete(1));
        assertEquals(List.of(second), tasks.getTasks());
    }

    @Test
    public void delete_outOfRangeTaskNumbers_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(2));
    }

    @Test
    public void mark_validTaskNumber_taskMarkedAndReturned() throws InvalidTaskNumberException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.mark(1));
        assertTrue(todo.isDone());
    }

    @Test
    public void unmark_completedTask_taskMarkedNotDoneAndReturned() throws InvalidTaskNumberException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.unmark(1));
        assertFalse(todo.isDone());
    }

    @Test
    public void markAndUnmark_outOfRangeTaskNumbers_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> tasks.mark(1));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.unmark(1));
    }

    @Test
    public void findTasksOn_matchingScheduledTasks_matchesInOriginalOrder() {
        LocalDate target = LocalDate.of(2026, 8, 29);
        Todo todo = new Todo("not scheduled");
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 9, 0),
                LocalDateTime.of(2026, 8, 30, 17, 0));
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 29, 18, 0));
        Deadline otherDeadline = new Deadline("later", LocalDateTime.of(2026, 8, 31, 18, 0));
        TaskList tasks = new TaskList(List.of(todo, event, deadline, otherDeadline));

        assertEquals(List.of(event, deadline), tasks.findTasksOn(target));
    }

    @Test
    public void getTasks_modifyReturnedSnapshot_originalListUnchanged() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write")));
        assertEquals(1, tasks.size());
    }
}
