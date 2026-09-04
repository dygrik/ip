# UI Test Plan

This file is the source of truth for scripted end-to-end tests of Rem's console UI. Run it with the project-specific `test-ui` skill.

## Launch information

- Required JDK: Java 25
- Compile: `javac -d out` followed by every `.java` file under `src/main/java` except the JavaFX-only classes
  `Launcher.java`, `Main.java`, `MainWindow.java`, and `DialogBox.java`
- Run: `java -cp out rem.Rem`
- Comparison: exact standard-output comparison, with CRLF and LF treated as equivalent
- Default timeout: 10 seconds per test case
- Test isolation: remove the `data` folder before each case unless the case specifies saved data
- Persistence check: after a case that changes the task list, inspect `data/rem.txt`
  and verify that it contains the final task list in the documented storage format

## TC-01: Create and list all task types

**Aim:** Verify that Rem creates each task subtype, parses date/time values, reports the task count,
and displays dates in a readable format.

**Inputs:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] borrow book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] borrow book
Rem: 2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
Rem: 3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-03: Handle invalid user input without exiting

**Aim:** Verify that Rem reports unknown commands, empty task descriptions, invalid deadline and event formats, and invalid task numbers, then continues accepting commands.

**Inputs:**

```text

blah
todo
deadline return book
deadline return book /by
event meeting
event meeting /from 2pm
event /from 2pm /to 4pm
mark
mark abc
mark 0
todo read book
mark 2
unmark -1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: When is this due by again?
____________________________________________________________
Me: ____________________________________________________________
Rem: When is this due by again?
____________________________________________________________
Me: ____________________________________________________________
Rem: I need to know when it starts and when it ends...
____________________________________________________________
Me: ____________________________________________________________
Rem: I need to know when it starts and when it ends...
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-02: Preserve the task type when marking and unmarking

**Aim:** Verify that operations inherited from `Task` update completion status without losing the to-do type marker.

**Inputs:**

```text
todo read book
mark 1
unmark 1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! I've marked this task as done:
Rem: [T][X] read book
____________________________________________________________
Me: ____________________________________________________________
Rem: Aww ok... I've marked this task as not done yet:
Rem: [T][ ] read book
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-05: Delete boundary tasks and handle an empty list

**Aim:** Verify that Rem rejects deletion from an empty list, deletes the first and last remaining tasks, renumbers tasks, and reports a zero task count correctly.

**Inputs:**

```text
delete 1
todo first task
todo second task
delete 1
list
delete 1
list
delete 1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] first task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] second task
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: One less thing to do! Removed:
Rem: [T][ ] first task
Rem: Now we are only left with 1 task in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] second task
____________________________________________________________
Me: ____________________________________________________________
Rem: One less thing to do! Removed:
Rem: [T][ ] second task
Rem: Now we are only left with 0 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-06: Recognize mixed-case enum commands

**Aim:** Verify that command recognition is case-insensitive and that unsupported first words still produce the unknown-command response.

**Inputs:**

```text
ToDo mixed case task
MaRk 1
UnMaRk 1
LiSt
nonsense
BYE
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] mixed case task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! I've marked this task as done:
Rem: [T][X] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Aww ok... I've marked this task as not done yet:
Rem: [T][ ] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-04: Delete a task from the list

**Aim:** Verify that deleting a task removes the selected task, reduces the task count, and renumbers the remaining tasks.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: One less thing to do! Removed:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now we are only left with 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] read book
Rem: 2.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-07: Save the final task list to disk

**Aim:** Verify that adding, marking, and deleting tasks leaves the final task list saved in
`data/rem.txt`. After the process exits, the file must contain `T | 1 | saved task` followed by a
newline.

**Inputs:**

```text
todo saved task
deadline removed task /by 2019-06-06
mark 1
delete 2
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] saved task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [D][ ] removed task (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! I've marked this task as done:
Rem: [T][X] saved task
____________________________________________________________
Me: ____________________________________________________________
Rem: One less thing to do! Removed:
Rem: [D][ ] removed task (by: Jun 06 2019)
Rem: Now we are only left with 1 task in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-08: Load saved tasks when starting

**Aim:** Verify that Rem loads every task subtype and its completion status from `data/rem.txt`.
Before starting Rem, create the file with these contents:

```text
T | 1 | read book
D | 0 | return book | 2019-06-06 0000
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][X] read book
Rem: 2.[D][ ] return book (by: Jun 06 2019)
Rem: 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-09: Recover from malformed saved data

**Aim:** Verify that malformed saved data does not crash Rem. Before starting Rem, create
`data/rem.txt` containing `D | maybe | broken task`, which has an invalid status and missing due
value.

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
Rem: Rem found nothing... Guess I'll start a new one!
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-10: Continue after a save failure

**Aim:** Verify that an unwritable data-file path does not crash Rem. Before starting Rem, create
`data/rem.txt` as a directory so it cannot be read from or overwritten as a regular file.

**Inputs:**

```text
todo unsaved task
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
Rem: Rem found nothing... Guess I'll start a new one!
____________________________________________________________
Me: ____________________________________________________________
Rem: Rem couldn't save the tasks... Could you check the data folder?
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-11: Find scheduled tasks by date

**Aim:** Verify that the `on` command lists deadlines due and events overlapping the requested
date, excludes todos, reports dates with no scheduled tasks, and rejects invalid dates.

**Inputs:**

```text
todo buy snacks
deadline submit report /by 2019-12-04 1800
event conference /from 2019-12-03 0900 /to 2019-12-05 1700
on 2019-12-04
on 2019-12-06
on tomorrow
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] buy snacks
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [D][ ] submit report (by: Dec 04 2019, 6:00 PM)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [E][ ] conference (from: Dec 03 2019, 9:00 AM to: Dec 05 2019, 5:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Here's what's scheduled on Dec 04 2019:
Rem: 1.[D][ ] submit report (by: Dec 04 2019, 6:00 PM)
Rem: 2.[E][ ] conference (from: Dec 03 2019, 9:00 AM to: Dec 05 2019, 5:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: You're free on Dec 06 2019.
____________________________________________________________
Me: ____________________________________________________________
Rem: Please use a date like 2019-10-15.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-12: Find tasks by description keyword

**Aim:** Verify that `find` searches task descriptions case-insensitively, preserves matching task
order, excludes non-matches, and rejects a missing keyword.

**Inputs:**

```text
todo Read Book
deadline return book /by 2019-06-06
event library visit /from 2019-06-07 1400 /to 2019-06-07 1600
find BOOK
find
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hello! I'm Rem!
Rem: No more sleeping. Need help?
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [T][ ] Read Book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Ok! I've added this:
Rem: [E][ ] library visit (from: Jun 07 2019, 2:00 PM to: Jun 07 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Here are the matching tasks in your list:
Rem: 1.[T][ ] Read Book
Rem: 2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## Graphical interface regression checks

Run `gradlew test` on a desktop with Java 25. `MainWindowTest` loads the actual FXML and CSS
on the JavaFX thread, submits commands through Send and Enter, and checks bubble order,
avatars, wrapping, resizing, scrolling, and exit controls. The console cases above remain
unchanged because both interfaces execute the same commands.

Launch the GUI with `gradlew run` or `java -jar build/libs/rem.jar` after `gradlew shadowJar`.
The background is black, Rem uses `images/rem.jpeg`, and the user's avatar is a white circle.
`bye` displays the farewell, disables input, and closes the application after 1.5 seconds.
