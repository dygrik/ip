package rem.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rem.Rem;

public class MainWindowTest {
    @TempDir
    Path directory;

    @BeforeAll
    public static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(ready::countDown);
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void chat_submitResizeAndExit_loadsResourcesAndUsesRealCommands() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            new Scene(root);
            loader.<MainWindow>getController().setRem(new Rem(directory.resolve("rem.txt").toString()));
            TextField input = (TextField) loader.getNamespace().get("userInput");
            Button send = (Button) loader.getNamespace().get("sendButton");
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            ScrollPane scroll = (ScrollPane) loader.getNamespace().get("scrollPane");
            input.setText("todo read book");
            send.fire();
            input.setText("note 1 Borrow it from Alice");
            send.fire();
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertEquals(7, dialogs.getChildren().size());
            assertEquals("", input.getText());
            HBox reply = (HBox) dialogs.getChildren().get(6);
            assertTrue(((Label) reply.lookup("#dialog")).getText().contains("1.[T][ ] read book"));
            assertTrue(((Label) reply.lookup("#dialog")).getText()
                    .contains("Note: Borrow it from Alice"));
            ImageView image = (ImageView) root.lookup("ImageView");
            assertFalse(image.getImage().isError());
            assertEquals(40, image.getFitWidth());
            input.setText("   ");
            assertTrue(send.isDisabled());
            input.fireEvent(new ActionEvent());
            assertEquals(7, dialogs.getChildren().size());
            input.setText("todos");
            send.fire();
            assertEquals("todos", input.getText());
            VBox error = (VBox) dialogs.getChildren().get(8).lookup("#message");
            assertTrue(error.getStyleClass().contains("error-message"));
            assertTrue(error.lookup("#errorHeading").isVisible());
            assertTrue(error.getAccessibleText().startsWith("Error:"));
            input.setText("list");
            send.fire();
            assertEquals("", input.getText());
            assertFalse(dialogs.getChildren().get(10).lookup("#errorHeading").isManaged());
            input.setText("todo " + "A long description that needs wrapping. ".repeat(8)
                    + " /note " + "A detailed note. ".repeat(10));
            send.fire();
            Label longReply = (Label) dialogs.getChildren().get(12).lookup("#dialog");
            for (int width : new int[]{380, 800}) {
                root.resize(width, width == 380 ? 400 : 600);
                root.applyCss();
                root.layout();
                assertEquals(Color.BLACK, root.getBackground().getFills().get(0).getFill());
                assertTrue(input.getWidth() > 100);
                assertTrue(scroll.getWidth() <= width);
                assertTrue(reply.getBoundsInParent().getMaxX() <= dialogs.getWidth());
                Label replyText = (Label) reply.lookup("#dialog");
                assertTrue(replyText.getWidth() > 100);
                assertTrue(replyText.getWidth() < width);
                assertEquals(Color.web("#38212a"), error.getBackground().getFills().get(0).getFill());
                assertTrue(longReply.getHeight() >= longReply.prefHeight(longReply.getWidth()) - 1);
                assertTrue(longReply.getHeight() > 60);
            }
            for (int i = 0; i < 15; i++) {
                input.setText("list");
                send.fire();
            }
            root.layout();
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(((Label) reply.lookup("#dialog")).isWrapText());
            scroll.setVvalue(0.25);
            root.resize(400, 500);
            root.layout();
            // JavaFX adjusts the offset as text reflows, but must not jump to the latest message.
            assertTrue(scroll.getVvalue() > 0);
            assertTrue(scroll.getVvalue() < 0.9);
            input.setText("bye");
            send.fire();
            assertTrue(input.isDisabled());
            assertTrue(send.isDisabled());
            return null;
        });
        Platform.runLater(check);
        check.get(20, TimeUnit.SECONDS);
    }
}
