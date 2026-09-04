package rem.ui;

import java.util.Scanner;
import java.util.function.Consumer;

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
    private final Consumer<String> output;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = message -> System.out.println("Rem: " + message);
    }

    /**
     * Creates a message-only UI for a graphical conversation.
     *
     * @param output Consumer receiving each response line.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
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
            System.out.println("Rem: Rem found nothing... Guess I'll start a new one!");
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Prompts for and reads the next command.
     *
     * @return The command entered by the user, or {@code null} if the input stream is closed.
     */
    public String readCommand() {
        System.out.print("Me: ");
        System.out.flush();
        if (!scanner.hasNextLine()) {
            return null;
        }
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
        output.accept(message);
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
        if (scanner != null) {
            scanner.close();
        }
    }
}
