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

        //Initializes storage and counter for user input
        Task[] added = new Task[100];
        int addCount = 0;

        //Scanner picks up and responds to user input
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Me: ");
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equalsIgnoreCase("bye")) {

                System.out.println("Rem: [Yawn] Need more sleep. Time for bed...");
                System.out.println(SEPARATOR);
                break;
            } else if (command.equalsIgnoreCase("list")) {
                System.out.println("Rem: Hmm... what to do now?");
                for (int i = 0; i < addCount; i++) {
                    System.out.println("Rem: " + (i + 1) + "." + added[i]);
                }
            } else if (command.toLowerCase().startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                Task task = added[taskNumber - 1];
                task.markAsDone();
                System.out.println("Rem: We did it! I've marked this task as done:");
                System.out.println("Rem: " + task);
            } else if (command.toLowerCase().startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7).trim());
                Task task = added[taskNumber - 1];
                task.markAsNotDone();
                System.out.println("Rem: Aww ok... I've marked this task as not done yet:");
                System.out.println("Rem: " + task);
            } else if (isTaskCreationCommand(command)) {
                Task task = createTask(command);
                added[addCount] = task;
                addCount++;
                System.out.println("Rem: Ok! I've added this task:");
                System.out.println("Rem: " + task);
                System.out.println("Rem: Now you have " + addCount
                        + (addCount == 1 ? " task" : " tasks") + " in the list.");
            } else {
                System.out.println("Rem: Hmm... I don't know what to do with that...");
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
    }

    //Checks whether a command creates a task types
    private static boolean isTaskCreationCommand(String command) {
        String c = command.toLowerCase();
        return c.startsWith("todo ")
                || c.startsWith("deadline ")
                || c.startsWith("event ");
    }

    //Creates the appropriate task subtype
    private static Task createTask(String command) {
        String c = command.toLowerCase();
        if (c.startsWith("todo ")) {
            return new Todo(command.substring(5).trim());
        }

        if (c.startsWith("deadline ")) {
            int byIndex = c.indexOf(" /by ");
            String description = command.substring(9, byIndex).trim();
            String by = command.substring(byIndex + 5).trim();
            return new Deadline(description, by);
        }

        int fromIndex = c.indexOf(" /from ");
        int toIndex = c.indexOf(" /to ", fromIndex + 7);
        String description = command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        return new Event(description, from, to);
    }
}
