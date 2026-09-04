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
        assertFalse(rem.getResponse("invalid").isExit());
        assertEquals("You didn't say what you wanna do...", rem.getResponse("todo").text());
        assertEquals("Hmm... what to do now?", rem.getResponse("list").text());
        Response farewell = rem.getResponse("BYE");
        assertTrue(farewell.isExit());
        assertEquals("[Yawn] Need more sleep. Time for bed...", farewell.text());
    }

    @Test
    public void getResponse_storageFailure_reportsError() throws IOException {
        Path blocked = Files.createDirectory(directory.resolve("blocked"));
        Rem rem = new Rem(blocked.toString());
        assertTrue(rem.getWelcome().contains("Rem found nothing"));
        assertEquals("Rem couldn't save the tasks... Could you check the data folder?",
                rem.getResponse("todo read book").text());
    }
}
