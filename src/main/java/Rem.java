import java.util.Scanner;

public class Rem {
    private static final String SEPARATOR =
            "____________________________________________________________";

    public static void main(String[] args) {
        String banner = " ____                      \n"
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
        String[] added = new String[100];
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
                for (int i = 0; i < addCount; i++) {
                    System.out.println("Rem: " + (i + 1) + ". " + added[i]);
                }
            } else {
                added[addCount] = command;
                addCount++;
                System.out.println("Rem: added: " + command);
            }

            System.out.println(SEPARATOR);
        }

        scanner.close();
    }
}
