package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for the application, loads GUI from FXML and launches the app.
 */
public class Main extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("chess_gui.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("chess_ico.png")));
        final Scene scene = new Scene(root, 800, 830);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(final String[] args) {
        launch(args);
    }
}
