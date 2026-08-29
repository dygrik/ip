package rem.exception;

/**
 * Indicates that the user entered a command that Rem does not recognize.
 */
public class UnknownCommandException extends RemException {
    private static final String MESSAGE = "Hmm... I don't know what to do with that...";

    /**
     * Creates an exception for an unknown or blank command.
     */
    public UnknownCommandException() {
        super(MESSAGE);
    }
}
