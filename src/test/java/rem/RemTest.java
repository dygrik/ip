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
        assertEquals("Got it! I put it on the list:\n[T][ ] read book\nYay! Our first task!",
                rem.getResponse("todo read book").text());
        rem.getResponse("mark 1");
        assertEquals("Hmm... what to do now?\n1.[T][X] read book",
                new Rem(path).getResponse("list").text());
        rem.getResponse("unmark 1");
        assertTrue(rem.getResponse("find book").text().contains("[T][ ] read book"));
        rem.getResponse("delete 1");
        assertEquals("Hmm... what to do now?\nNo tasks yet. Got something for us to do?",
                new Rem(path).getResponse("list").text());
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
        assertEquals("Hmm... what to do now?\nNo tasks yet. Got something for us to do?",
                rem.getResponse("list").text());
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

        assertEquals("I'll keep this note with your task:\n"
                + "[T][ ] read book\n  Note: Return it tomorrow",
                rem.getResponse("note 1 Return it tomorrow").text());
        assertTrue(rem.getResponse("find TOMORROW").text().contains("Note: Return it tomorrow"));
        assertEquals("Okay. Took the note off this task:\n[T][ ] read book",
                rem.getResponse("deletenote 1").text());
        assertEquals("This task doesn't have a note to remove.",
                rem.getResponse("deletenote 1").text());
        assertFalse(new Rem(path).getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_invalidNoteText_exactMessagesReturned() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        rem.getResponse("todo read book");

        assertEquals("What should I keep in this note?",
                rem.getResponse("note 1").text());
        assertEquals("Rem can't remember more than 200 characters...",
                rem.getResponse("note 1 " + "a".repeat(201)).text());
        assertFalse(rem.getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_emptySearchAndSchedule_reportsAbsenceWithoutInventingAvailability() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        assertFalse(rem.hasTasks());
        assertEquals("Didn't find any tasks matching 'pillow'. Try another word?",
                rem.getResponse("find pillow").text());
        rem.getResponse("todo wash pillow");
        assertTrue(rem.hasTasks());
        assertEquals("Found these!\n1.[T][ ] wash pillow", rem.getResponse("find pillow").text());
        assertEquals("Nothing scheduled on Oct 01 2026. Maybe nap time?",
                rem.getResponse("on 2026-10-01").text());
        rem.getResponse("delete 1");
        assertFalse(rem.hasTasks());
    }

    @Test
    public void getResponse_storageFailure_reportsError() throws IOException {
        Path blocked = Files.createDirectory(directory.resolve("blocked"));
        Rem rem = new Rem(blocked.toString());
        assertTrue(rem.getWelcome().contains("I couldn't load your saved tasks"));
        Response failure = rem.getResponse("todo read book");
        assertEquals("Oh. Your changes weren't saved. Could you check the data folder?", failure.text());
        assertTrue(failure.isError());
        assertFalse(failure.isExit());
    }
}
