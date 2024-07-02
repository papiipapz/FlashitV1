package org.group.flashitv1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CardMenuController {

    private double x = 0;
    private double y = 0;
    private Scene previousScene;

    @FXML
    private Pane Card;

    @FXML
    public FlowPane cardsContainer;

    @FXML
    private ComboBox<?> categoriesComboBox;

    @FXML
    private Button createNewCard;

    @FXML
    private Button deleteCardButton;

    @FXML
    private Button homeButton;

    @FXML
    private HBox navBar;

    @FXML
    private Button profileButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void createNewCard(ActionEvent event) {
        // Create a new card (VBox with some content)
        Pane newCard = new Pane();
        newCard.getStyleClass().add("card_Layout");

        Button cardButton = new Button("New Card");
        newCard.getChildren().add(cardButton);

        // Add the new card to the card container
        cardsContainer.getChildren().add(newCard);

    }

    @FXML
    public void openNewCard(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previousScene = currentStage.getScene();

            // Load the FXML for the popup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CustomCard.fxml"));
            Parent root = loader.load();

            CustomCardController customCardController = loader.getController();
            customCardController.setPreviousScene(previousScene);

            Scene scene = new Scene(root, 500, 500);
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
    void logoutApp (ActionEvent event) {
        Platform.exit();
    }
}

