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
    private static final int DIALOG_WIDTH_OFFSET = 90;
    private static final int AVATAR_SIZE = 48;
    private static final double AVATAR_RADIUS = AVATAR_SIZE / 2.0;
    private static final Image REM_IMAGE =
            new Image(DialogBox.class.getResource("/images/rem.jpeg").toExternalForm());
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
        loadView();
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(DIALOG_WIDTH_OFFSET));

        Image profile = isUser ? USER_IMAGE : REM_IMAGE;
        avatar.getChildren().add(createProfilePicture(profile));
        configureSpeaker(isUser);
    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load chat bubble", e);
        }
    }

    private static ImageView createProfilePicture(Image profile) {
        ImageView picture = new ImageView(profile);
        // Crop the center to a square so landscape profile pictures are not stretched.
        double side = Math.min(profile.getWidth(), profile.getHeight());
        double cropX = (profile.getWidth() - side) / 2;
        double cropY = (profile.getHeight() - side) / 2;
        picture.setViewport(new Rectangle2D(cropX, cropY, side, side));
        picture.setFitWidth(AVATAR_SIZE);
        picture.setFitHeight(AVATAR_SIZE);
        picture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        return picture;
    }

    private void configureSpeaker(boolean isUser) {
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
