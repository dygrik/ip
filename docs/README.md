# Rem User Guide

Rem is a task-tracking chatbot. Commands work in both the console and JavaFX chat window.

## Adding a task with a note

Add the optional `/note` field at the end of any task-creation command:

```text
todo read book /note Borrow it from Alice
deadline submit report /by 2026-10-01 1800 /note Include the appendix
event consultation /from 2026-10-01 1400 /to 2026-10-01 1500 /note Bring the draft
```

Rem displays the note on the line below its task:

```text
[T][ ] read book
  Note: Borrow it from Alice
```

`/note` is recognized case-insensitively when it is a separate field token. It is reserved in task
descriptions; quoted descriptions and escaping are not supported. Everything after `/note` is note
text, so the note itself may contain `/note`, `/by`, `/from`, or `/to`.

## Adding or replacing a note

First run `list` to obtain the task's number, then enter:

```text
note TASK_NUMBER NOTE
```

For example:

```text
note 2 Include the references
```

If the task already has a note, Rem replaces it automatically:

```text
Hmm... Rem will try his best to remember:
[D][ ] submit report (by: Oct 01 2026, 6:00 PM)
  Note: Include the references
```

The numbers displayed by `find` and `on` are positions within those result lists. Always use the
number displayed by the full `list` command with `note` and `deletenote`.

## Deleting a note

Enter `deletenote` followed by the task number from `list`:

```text
deletenote 2
```

Rem responds:

```text
Phew, Rem kinda forgot what the note was:
[D][ ] submit report (by: Oct 01 2026, 6:00 PM)
```

If the task has no note, Rem responds:

```text
You didn't give Rem anything to remember though...
```

## Note rules

- A task can have one note.
- A note must contain between 1 and 200 Unicode code points after surrounding whitespace is removed.
- Notes are single-line text. Links, filenames, punctuation, and emoji are stored as text.
- A missing note produces `You didn't say what Rem should remember...`.
- A note longer than 200 characters produces `Rem can't remember more than 200 characters...`.
- Notes appear whenever a task is displayed, including in `list`, `find`, `on`, `mark`, `unmark`,
  and `delete` responses.
- `find` matches text in task descriptions and notes without regard to letter case.

## Saved-data compatibility

Tasks remain stored in `data/rem.txt`. Existing task records without notes continue to load and keep
their existing format. A task with a note has an additional final `N:` field containing the note as
Base64-encoded UTF-8 text. This encoding prevents note text such as ` | ` from being mistaken for a
storage separator.

Files containing notes require this version of Rem or a newer one. Older Rem versions cannot load
the additional note field.
