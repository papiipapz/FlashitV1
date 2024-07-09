package org.group.flashitv1;

import com.google.cloud.firestore.Firestore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class menuFlash extends Application {

    private Firestore db;

    private String username;

    // This method will be called from the LoginScreen to pass the user's details
    public void setUserDetails(String username, String userId) {
        this.username = username;
    }

    // No-argument constructor required by FXML loader
    public menuFlash() {
        // Default constructor logic if any
    }

    // Method to set Firestore after controller initialization
    public void setFirestore(Firestore firestore) {
        this.db = firestore;
    }

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
