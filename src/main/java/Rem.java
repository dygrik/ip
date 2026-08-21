import java.util.ArrayList;
import java.util.Scanner;

public class Rem {
    private static final String SEPARATOR =
            "____________________________________________________________";

    public static void main(String[] args) {
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
        System.out.println(SEPARATOR);

        // Stores tasks in a dynamically sized list so tasks can be added and removed easily.
        ArrayList<Task> added = new ArrayList<>();

        //Scanner picks up and responds to user input
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Me: ");
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);
            String trimmedCommand = command.trim();

            try {
                if (trimmedCommand.equalsIgnoreCase("bye")) {

                    System.out.println("Rem: [Yawn] Need more sleep. Time for bed...");
                    System.out.println(SEPARATOR);
                    break;
                } else if (trimmedCommand.equalsIgnoreCase("list")) {
                    System.out.println("Rem: Hmm... what to do now?");
                    for (int i = 0; i < added.size(); i++) {
                        System.out.println("Rem: " + (i + 1) + "." + added.get(i));
                    }
                } else if (isCommand(trimmedCommand, "mark")) {
                    int taskNumber = parseTaskNumber(trimmedCommand, "mark", added.size());
                    Task task = added.get(taskNumber - 1);
                    task.markAsDone();
                    System.out.println("Rem: We did it! I've marked this task as done:");
                    System.out.println("Rem: " + task);
                } else if (isCommand(trimmedCommand, "unmark")) {
                    int taskNumber = parseTaskNumber(trimmedCommand, "unmark", added.size());
                    Task task = added.get(taskNumber - 1);
                    task.markAsNotDone();
                    System.out.println("Rem: Aww ok... I've marked this task as not done yet:");
                    System.out.println("Rem: " + task);
                } else if (isCommand(trimmedCommand, "delete")) {
                    int taskNumber = parseTaskNumber(trimmedCommand, "delete", added.size());
                    Task removedTask = added.remove(taskNumber - 1);
                    System.out.println("Rem: Noted. I've removed this task:");
                    System.out.println("Rem:   " + removedTask);
                    System.out.println("Rem: Now you have " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
                } else if (isTaskCreationCommand(trimmedCommand)) {
                    Task task = createTask(trimmedCommand);
                    added.add(task);
                    System.out.println("Rem: Ok! I've added this task:");
                    System.out.println("Rem: " + task);
                    System.out.println("Rem: Now you have " + added.size()
                            + (added.size() == 1 ? " task" : " tasks") + " in the list.");
                } else {
                    throw new UnknownCommandException();
                }
            } catch (RemException e) {
                System.out.println("Rem: " + e.getMessage());
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
    }

    //Checks whether a command creates a task type
    private static boolean isTaskCreationCommand(String command) {
        return isCommand(command, "todo")
                || isCommand(command, "deadline")
                || isCommand(command, "event");
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
            return new Deadline(description, by);
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
        return new Event(description, from, to);
    }
}
