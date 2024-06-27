package org.group.flashitv1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class menuFlash extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(menuFlash.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1114, 800);
        stage.setTitle("FlashIT");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}