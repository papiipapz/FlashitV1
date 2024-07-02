package org.group.flashitv1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProfileManagementController {
    private Scene previousScene;

    @FXML
    private Button backButton;

    @FXML
    private TextField newPassInput;

    @FXML
    private Text newPassText;

    @FXML
    private TextField passInput;

    @FXML
    private Text passText;

    @FXML
    private Button saveButton;

    @FXML
    private TextField userInput;

    @FXML
    private Text userText;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    void menuButton(ActionEvent event) {
        if (previousScene != null) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(previousScene);
            System.out.println("Switched back to previous scene. Previous scene: " + previousScene);
        } else {
            System.out.println("Cannot switch back to previous scene. Previous scene is null.");
        }
    }

    @FXML
    void saveChanges(ActionEvent event) {

    }

}
