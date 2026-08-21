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

        //Scanner to pick up user input and echo accordingly (Level-1)
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
            } else {
                Task t = new Task(command);
                added[addCount] = t;
                addCount++;
                System.out.println("Rem: added: " + command);
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
    }
}
