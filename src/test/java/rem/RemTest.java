package rem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class RemTest {
    @TempDir
    Path directory;

    @Test
    public void getResponse_invalidCommands_providesActionableExamples() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        assertTrue(rem.getResponse("todos").text().contains("Try todo read book"));
        assertTrue(rem.getResponse("deadline report").text().contains("/by YYYY-MM-DD [HHmm]"));
        assertTrue(rem.getResponse("event meeting").text().contains("The end must be at or after the start."));
        assertTrue(rem.getResponse("mark 1").text().contains("Use list to see task numbers"));
        assertFalse(rem.getResponse("list").text().contains("Example:"));
    }

    @Test
    public void getResponse_addMarkReload_collectsLinesAndPersists() {
        String path = directory.resolve("rem.txt").toString();
        Rem rem = new Rem(path);
        assertEquals("Ok! I've added this:\n[T][ ] read book\nYay! Our first task!",
                rem.getResponse("todo read book").text());
        rem.getResponse("mark 1");
        assertEquals("Hmm... what to do now?\n1.[T][X] read book",
                new Rem(path).getResponse("list").text());
        rem.getResponse("unmark 1");
        assertTrue(rem.getResponse("find book").text().contains("[T][ ] read book"));
        rem.getResponse("delete 1");
        assertEquals("Hmm... what to do now?", new Rem(path).getResponse("list").text());
    }

    @Test
    public void getResponse_invalidThenValid_doesNotLeakMessages() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        Response invalid = rem.getResponse("invalid");
        assertFalse(invalid.isExit());
        assertTrue(invalid.isError());
        assertTrue(rem.getResponse("todo").isError());
        assertTrue(rem.getResponse("mark 1").isError());
        assertFalse(rem.getResponse("list").isError());
        assertEquals("You didn't say what you wanna do...", rem.getResponse("todo").text());
        assertEquals("Hmm... what to do now?", rem.getResponse("list").text());
        Response farewell = rem.getResponse("BYE");
        assertTrue(farewell.isExit());
        assertFalse(farewell.isError());
        assertEquals("[Yawn] Need more sleep. Time for bed...", farewell.text());
    }

    @Test
    public void getResponse_noteReplaceFindDeleteNote_behavesAndPersists() {
        String path = directory.resolve("rem.txt").toString();
        Rem rem = new Rem(path);
        rem.getResponse("todo read book /note Borrow it from Alice");

        assertEquals("Hmm... Rem will try his best to remember:\n"
                + "[T][ ] read book\n  Note: Return it tomorrow",
                rem.getResponse("note 1 Return it tomorrow").text());
        assertTrue(rem.getResponse("find TOMORROW").text().contains("Note: Return it tomorrow"));
        assertEquals("Phew, Rem kinda forgot what the note was:\n[T][ ] read book",
                rem.getResponse("deletenote 1").text());
        assertEquals("You didn't give Rem anything to remember though...",
                rem.getResponse("deletenote 1").text());
        assertFalse(new Rem(path).getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_invalidNoteText_exactMessagesReturned() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        rem.getResponse("todo read book");

        assertEquals("You didn't say what Rem should remember...",
                rem.getResponse("note 1").text());
        assertEquals("Rem can't remember more than 200 characters...",
                rem.getResponse("note 1 " + "a".repeat(201)).text());
        assertFalse(rem.getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_storageFailure_reportsError() throws IOException {
        Path blocked = Files.createDirectory(directory.resolve("blocked"));
        Rem rem = new Rem(blocked.toString());
        assertTrue(rem.getWelcome().contains("Rem found nothing"));
        Response failure = rem.getResponse("todo read book");
        assertEquals("Rem couldn't save the tasks... Could you check the data folder?", failure.text());
        assertTrue(failure.isError());
        assertFalse(failure.isExit());
    }
}
