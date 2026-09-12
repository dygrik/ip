package rem.exception;

/**
 * Indicates that a task has no note to delete.
 */
public class MissingNoteException extends RemException {
    private static final String MESSAGE = "You didn't give Rem anything to remember though...";

    /**
     * Creates an exception for deleting a nonexistent note.
     */
    public MissingNoteException() {
        super(MESSAGE);
    }
}
