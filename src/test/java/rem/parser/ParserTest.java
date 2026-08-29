package rem.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import rem.command.CommandType;
import rem.exception.EmptyDescriptionException;
import rem.exception.InvalidDateException;
import rem.exception.InvalidDeadlineFormatException;
import rem.exception.InvalidEventFormatException;
import rem.exception.InvalidTaskNumberException;
import rem.exception.RemException;
import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.Todo;

/**
 * Tests command argument validation and task construction by {@link Parser}.
 */
public class ParserTest {
    @Test
    public void getCommandType_mixedCaseKnownCommand_commandTypeReturned() {
        assertEquals(CommandType.DEADLINE, Parser.getCommandType("DeAdLiNe submit report"));
    }

    @Test
    public void getCommandType_blankOrUnknownCommand_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("   "));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("deadlines submit report"));
    }

    @Test
    public void parseTaskNumber_positiveInteger_numberReturned() throws InvalidTaskNumberException {
        assertEquals(12, Parser.parseTaskNumber("mark   12", "mark"));
    }

    @Test
    public void parseTaskNumber_missingNonNumericOrNonPositiveNumber_exceptionThrown() {
        assertThrows(InvalidTaskNumberException.class, () -> Parser.parseTaskNumber("mark", "mark"));
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark two", "mark"));
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 0", "mark"));
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark -1", "mark"));
    }

    @Test
    public void parseDate_validDate_dateReturned() throws InvalidDateException {
        assertEquals(LocalDate.of(2026, 8, 29), Parser.parseDate("on 2026-08-29"));
    }

    @Test
    public void parseDate_missingDateDateTimeOrInvalidDate_exceptionThrown() {
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on"));
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on 2026-08-29 1200"));
        assertThrows(InvalidDateException.class, () -> Parser.parseDate("on tomorrow"));
    }

    @Test
    public void commandParsing_turkishDefaultLocale_commandsRemainCaseInsensitive()
            throws RemException {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertEquals(CommandType.FIND, Parser.getCommandType("find book"));
            assertInstanceOf(Deadline.class,
                    Parser.createTask("DEADLINE submit report /BY 2026-08-29"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void parseKeyword_validKeyword_keywordReturned() throws EmptyDescriptionException {
        assertEquals("read book", Parser.parseKeyword("find   read book"));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseKeyword("find"));
    }

    @Test
    public void createTask_validTodo_todoReturned() throws RemException {
        Task task = Parser.createTask("ToDo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void createTask_validDeadline_deadlineReturned() throws RemException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                Parser.createTask("deadline submit report /by 2026-08-29 1800"));

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 29, 18, 0), deadline.getBy());
    }

    @Test
    public void createTask_validEvent_eventReturned() throws RemException {
        Event event = assertInstanceOf(Event.class,
                Parser.createTask("event workshop /from 2026-08-29 0900 /to 2026-08-30 1700"));

        assertEquals("workshop", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 29, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 30, 17, 0), event.getTo());
    }

    @Test
    public void createTask_emptyDescriptions_exceptionThrown() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.createTask("todo"));
        assertThrows(EmptyDescriptionException.class,
                () -> Parser.createTask("deadline /by 2026-08-29"));
        assertThrows(EmptyDescriptionException.class,
                () -> Parser.createTask("event /from 2026-08-29 /to 2026-08-30"));
    }

    @Test
    public void createTask_invalidDeadlineDetails_exceptionThrown() {
        assertThrows(InvalidDeadlineFormatException.class,
                () -> Parser.createTask("deadline submit report"));
        assertThrows(InvalidDeadlineFormatException.class,
                () -> Parser.createTask("deadline submit report /by tomorrow"));
    }

    @Test
    public void createTask_invalidEventDetails_exceptionThrown() {
        assertThrows(InvalidEventFormatException.class,
                () -> Parser.createTask("event workshop /from 2026-08-29"));
        assertThrows(InvalidEventFormatException.class,
                () -> Parser.createTask("event workshop /from tomorrow /to 2026-08-30"));
        assertThrows(InvalidEventFormatException.class,
                () -> Parser.createTask("event workshop /from 2026-08-30 /to 2026-08-29"));
    }
}
