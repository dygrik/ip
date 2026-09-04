package rem.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rem.Rem;
import rem.Response;

/**
 * Handles chat submissions without running the blocking console input loop.
 */
public class MainWindow {
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
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue(1.0));
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
        dialogContainer.getChildren().add(new DialogBox(input, true));
        Response response = rem.getResponse(input);
        dialogContainer.getChildren().add(new DialogBox(response.text(), false));
        userInput.clear();
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
