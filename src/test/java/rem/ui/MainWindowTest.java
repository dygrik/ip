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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertEquals(5, dialogs.getChildren().size());
            assertEquals("", input.getText());
            HBox reply = (HBox) dialogs.getChildren().get(4);
            assertTrue(((Label) reply.getChildren().get(1)).getText().contains("1.[T][ ] read book"));
            ImageView image = (ImageView) ((StackPane) reply.getChildren().get(0)).getChildren().get(0);
            assertFalse(image.getImage().isError());
            HBox user = (HBox) dialogs.getChildren().get(1);
            Circle avatar = (Circle) ((StackPane) user.getChildren().get(1)).getChildren().get(0);
            assertEquals(Color.WHITE, avatar.getFill());
            for (int width : new int[]{380, 800}) {
                root.resize(width, 600);
                root.applyCss();
                root.layout();
                assertEquals(Color.BLACK, root.getBackground().getFills().get(0).getFill());
                assertTrue(input.getWidth() > 100);
                assertTrue(scroll.getWidth() <= width);
            }
            for (int i = 0; i < 15; i++) {
                input.setText("list");
                send.fire();
            }
            root.layout();
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(((Label) reply.getChildren().get(1)).isWrapText());
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
