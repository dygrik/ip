import java.util.Scanner;

/**
 * Handles console input and output for Rem.
 */
public class Ui implements AutoCloseable {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = " ____                      \n" // Used Codex to generate ASCII
            + "|  _ \\    ___    _ __ ___  \n"
            + "| |_) |  / _ \\  | '_ ` _ \\ \n"
            + "|  _ <  |  __/  | | | | | |\n"
            + "|_| \\_\\  \\___|  |_| |_| |_|\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Rem's welcome message.
     *
     * @param hasLoadError Whether loading the saved tasks failed.
     */
    public void showWelcome(boolean hasLoadError) {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Rem: Hello! I'm Rem!");
        System.out.println("Rem: No more sleeping. Need help?");
        if (hasLoadError) {
            System.out.println("Rem: I couldn't load your saved tasks, so I started with an empty list.");
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return True if another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Prompts for and reads the next command.
     *
     * @return The command entered by the user.
     */
    public String readCommand() {
        System.out.print("Me: ");
        String command = scanner.nextLine();
        System.out.println(SEPARATOR);
        return command;
    }

    /**
     * Displays one line spoken by Rem.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println("Rem: " + message);
    }

    /**
     * Displays the separator between command responses.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Closes the input scanner.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
