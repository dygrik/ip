package rem.exception;

/**
 * Indicates that an event command does not contain a valid start and end time.
 */
public class InvalidEventFormatException extends RemException {
    private static final String MESSAGE =
            "I need to know when it starts and when it ends...";

    /**
     * Creates an exception for missing, empty, or malformed event time information.
     */
    public InvalidEventFormatException() {
        super(MESSAGE);
    }
}
