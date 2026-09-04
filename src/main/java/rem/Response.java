package rem;

/**
 * Holds the text and exit status produced by one command.
 *
 * @param text Complete response for a chat bubble.
 * @param isExit Whether the command ended the conversation.
 */
public record Response(String text, boolean isExit) {
}
