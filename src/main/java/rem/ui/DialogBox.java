package rem.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Displays a wrapping message beside the speaker's circular profile picture.
 */
public class DialogBox extends HBox {
    private static final Image REM_IMAGE = new Image(DialogBox.class.getResource("/images/rem.jpeg").toExternalForm());
    private static final Image USER_IMAGE =
            new Image(DialogBox.class.getResource("/images/hidden_king.jpg").toExternalForm());
    @FXML
    private Label dialog;
    @FXML
    private StackPane avatar;

    /**
     * Creates one message, aligned according to its speaker.
     *
     * @param text Message text.
     * @param isUser Whether this message belongs to the user.
     */
    public DialogBox(String text, boolean isUser) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load chat bubble", e);
        }
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(90));
        Image profile = isUser ? USER_IMAGE : REM_IMAGE;
        ImageView picture = new ImageView(profile);
        // Crop the center to a square so landscape profile pictures are not stretched.
        double side = Math.min(profile.getWidth(), profile.getHeight());
        double cropX = (profile.getWidth() - side) / 2;
        double cropY = (profile.getHeight() - side) / 2;
        picture.setViewport(new Rectangle2D(cropX, cropY, side, side));
        picture.setFitWidth(48);
        picture.setFitHeight(48);
        picture.setClip(new Circle(24, 24, 24));
        avatar.getChildren().add(picture);
        if (isUser) {
            avatar.setAccessibleText("You");
            getChildren().setAll(dialog, avatar);
            setAlignment(Pos.TOP_RIGHT);
            dialog.getStyleClass().add("user-message");
        } else {
            avatar.setAccessibleText("RemBot");
        }
    }
}
