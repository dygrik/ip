# Rem

This is a greenfield Java project for the chatbot Rem. Given below are instructions on how to use it.

## Running RemBot

Use Java 25 and run `.\gradlew.bat run` to open the JavaFX chat window.
Send a command with Enter or the Send button. Tasks are saved in `data/rem.txt`
relative to the directory where you launch Rem. Existing console commands also work
in the GUI, including `todo`, `deadline`, `event`, `list`, `mark`, `unmark`,
`delete`, `find`, `on`, and `bye`. The farewell stays visible briefly before closing.

Build the runnable JAR with `.\gradlew.bat shadowJar`, then run
`java -jar build/libs/rem.jar`. The bundled JavaFX natives target Windows, macOS,
and Linux x64, matching the tutorial setup. Other architectures need matching JavaFX natives.
For the console interface, use `.\gradlew.bat runConsole` or run `rem.Rem` in the IDE.
For the GUI in IntelliJ, run `rem.Launcher` after refreshing Gradle.

FXML layouts are in `src/main/resources/view` and styling is in
`src/main/resources/css/main.css`. The supplied Rem picture is bundled as a resource.

## Checking code style

Use JDK 25 and run these commands from the project directory:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
.\gradlew.bat check
```

The first command checks production and test Java code. The second runs both Checkstyle
and the JUnit tests. On macOS/Linux, use `./gradlew` instead of `.\gradlew.bat`.
Errors and warnings fail the checks. HTML reports are written to
`build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`.

Checkstyle 11.0.0 uses the SE-EDU rules in `config/checkstyle/checkstyle.xml`, copied from
[AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle)
as directed by the [SE-EDU tutorial](https://se-education.org/guides/tutorials/checkstyle.html).
`suppressions.xml` retains the tutorial's test-code Javadoc exceptions; production code
is not exempted. Checkstyle complements the coding-standard skill and manual review;
it does not verify every judgment-based rule or application correctness.

For optional IntelliJ integration, install CheckStyle-IDEA, select version 11.0.0,
import `config/checkstyle/checkstyle.xml` as a local configuration, and enable scanning
of Java sources including tests. Gradle remains the shared, reproducible check.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/rem/Rem.java` file, right-click it, and choose `Run Rem.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____
   |  _ \    ___    _ __ ___
   | |_) |  / _ \  | '_ ` _ \
   |  _ <  |  __/  | | | | | |
   |_| \_\  \___|  |_| |_| |_|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
