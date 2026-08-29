package rem.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rem.exception.InvalidTaskNumberException;

/**
 * Stores Rem's tasks and provides operations for managing them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks to store.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task using its one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return The selected task.
     */
    public Task getTask(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task using its one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task mark(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    public Task unmark(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Finds deadlines due and events occurring on a date.
     *
     * @param date Date to search for.
     * @return Scheduled tasks in their task-list order.
     */
    public List<Task> findTasksOn(LocalDate date) {
        ArrayList<Task> scheduledTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline && deadline.occursOn(date)
                    || task instanceof Event event && event.occursOn(date)) {
                scheduledTasks.add(task);
            }
        }
        return scheduledTasks;
    }

    /**
     * Finds tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their task-list order.
     */
    public List<Task> findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns a read-only snapshot of all tasks.
     *
     * @return Current tasks in task-list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Checks that a one-based task number identifies a stored task.
     *
     * @param taskNumber One-based task number to validate.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException();
        }
    }
}
