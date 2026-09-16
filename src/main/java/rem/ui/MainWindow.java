package rem.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rem.Rem;
import rem.Response;

/**
 * Handles chat submissions without running the blocking console input loop.
 */
public class MainWindow {
    private static final double SCROLL_SPEED_MULTIPLIER = 1.5;
    private final CommandHistory history = new CommandHistory();
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    private Rem rem;

    @FXML
    private void initialize() {
        // Resizing should not interrupt reading older messages.
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                sendButton.setDisable(newValue.isBlank()));
        sendButton.setDisable(true);
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
    }

    /**
     * Recalls commands with unmodified arrow keys without submitting them.
     */
    private void handleHistoryKey(KeyEvent event) {
        if (userInput.isDisabled() || event.isAltDown() || event.isControlDown()
                || event.isMetaDown() || event.isShiftDown()) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            userInput.setText(history.previous(userInput.getText()));
        } else if (event.getCode() == KeyCode.DOWN) {
            userInput.setText(history.next(userInput.getText()));
        } else {
            return;
        }
        userInput.positionCaret(userInput.getLength());
        event.consume();
    }

    /**
     * Speeds up vertical wheel scrolling while preserving direct touch and modified gestures.
     */
    private void handleScroll(ScrollEvent event) {
        double overflow = dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight();
        if (overflow <= 0 || event.isDirect() || event.isControlDown() || event.isAltDown()
                || event.isMetaDown() || event.isShiftDown()
                || Math.abs(event.getDeltaX()) >= Math.abs(event.getDeltaY())) {
            return;
        }
        double range = scrollPane.getVmax() - scrollPane.getVmin();
        double position = scrollPane.getVvalue() - event.getDeltaY() * SCROLL_SPEED_MULTIPLIER / overflow * range;
        scrollPane.setVvalue(Math.clamp(position, scrollPane.getVmin(), scrollPane.getVmax()));
        event.consume();
    }

    /**
     * Connects the loaded task manager and displays its greeting.
     *
     * @param rem Task manager for this window.
     */
    public void setRem(Rem rem) {
        this.rem = rem;
        dialogContainer.getChildren().add(new DialogBox(rem.getWelcome(), false));
    }

    @FXML
    private void handleUserInput() {
        if (userInput.isDisabled()) {
            return;
        }
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        history.add(input);
        dialogContainer.getChildren().add(new DialogBox(input, true));
        Response response = rem.getResponse(input);
        dialogContainer.getChildren().add(new DialogBox(response.text(), false, response.isError()));
        if (!response.isError()) {
            userInput.clear();
        }
        // Apply the new message heights before moving to the latest response.
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
        userInput.requestFocus();
        if (response.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Let the user read the farewell before closing the window.
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
