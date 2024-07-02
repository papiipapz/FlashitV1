package org.group.flashitv1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class CardController {

    private double x = 0;
    private double y = 0;

    private Scene previousScene;

    @FXML
    private StackPane bigCard;

    @FXML
    private VBox bigCard_Back;

    @FXML
    private Button bigCard_BackButton;

    @FXML
    private VBox bigCard_Front;

    @FXML
    private Button bigCard_FrontButton;

    @FXML
    private VBox cardBox;

    @FXML
    private Text cardTitle;

    @FXML
    private Text categoryText;

    @FXML
    private Button deleteButton;

    @FXML
    private ImageView leftButton;

    @FXML
    private Button menuButton;

    @FXML
    private Text pageNo;

    @FXML
    private Button previousButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button rightButton;

    public void manageProfile (ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previousScene = currentStage.getScene();

            // Load the FXML for the popup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ProfileManagement.fxml"));
            Parent root = loader.load();


            ProfileManagementController profileManagementController = loader.getController();
            profileManagementController.setPreviousScene(previousScene);

            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            root.setOnMousePressed((MouseEvent mouseEvent) -> {

                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();

            });

            root.setOnMouseDragged((MouseEvent mouseEvent) ->{

                currentStage.setX(mouseEvent.getScreenX() - x);
                currentStage.setY(mouseEvent.getScreenY() - y);

            });

            currentStage.setScene(scene);
            System.out.println("Switched to new scene. Previous scene: " + previousScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void showAnswer(ActionEvent event) {
        if (event.getSource() == bigCard_FrontButton) {
            bigCard_Front.setVisible(false);
            bigCard_FrontButton.setVisible(false);
            bigCard_Back.setVisible(true);
            bigCard_BackButton.setVisible(true);
        } else if (event.getSource() == bigCard_BackButton) {
            bigCard_Back.setVisible(false);
            bigCard_BackButton.setVisible(false);
            bigCard_Front.setVisible(true);
            bigCard_FrontButton.setVisible(true);
        }
    }

}

