package org.group.flashitv1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CustomCardController {
    private Scene previousScene;
    private int questionCounter = 1;

    @FXML
    private Button addQuestions;

    @FXML
    private TextArea answerInput;

    @FXML
    private Text answerText;

    @FXML
    private VBox cardForm;

    @FXML
    private TextField categoryInput;

    @FXML
    private Text categoryText;

    @FXML
    private Button createNewCard;

    @FXML
    private TextField descriptionInput;

    @FXML
    private Text descriptionText;

    @FXML
    private Button menuButton;

    @FXML
    private TextArea questionInput;

    @FXML
    private Text questionText;

    @FXML
    private VBox questionsBox;

    @FXML
    private TextField titleInput;

    @FXML
    private Text titleText;

    @FXML
    void addQuestionContainer(ActionEvent event) {
        // Create a new VBox
        VBox newQuestionsBox = new VBox();
        newQuestionsBox.setSpacing(5);

        // Create the children nodes for the new VBox
        Text newQuestionText = new Text("Term/Question " + questionCounter);
        newQuestionText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        TextArea newQuestionInput = new TextArea();
        newQuestionInput.setPromptText("Term/Question " + questionCounter);
        newQuestionInput.setPrefHeight(100);
        newQuestionInput.setPrefWidth(200);
        newQuestionInput.setWrapText(true);

        Text newAnswerText = new Text("Definition/Answer " + questionCounter);
        newAnswerText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        TextArea newAnswerInput = new TextArea();
        newAnswerInput.setPromptText("Definition/Answer " + questionCounter);
        newAnswerInput.setPrefHeight(100);
        newAnswerInput.setPrefWidth(200);
        newAnswerInput.setWrapText(true);

        // Add the children to the new VBox
        newQuestionsBox.getChildren().addAll(newQuestionText, newQuestionInput, newAnswerText, newAnswerInput);

        // Add the new VBox before the addQuestions button
        cardForm.getChildren().add(cardForm.getChildren().indexOf(addQuestions), newQuestionsBox);

        // Increment the counter
        questionCounter++;
    }


    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    public void switchBackToPreviousScene(ActionEvent event) {
        if (previousScene != null) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(previousScene);
            System.out.println("Switched back to previous scene. Previous scene: " + previousScene);
        } else {
            System.out.println("Cannot switch back to previous scene. Previous scene is null.");
        }
    }


    @FXML
    void createNewCard(ActionEvent event) {
        // Check if previousScene is set
        if (previousScene != null) {
            // Create a new card (you can adjust this part based on your card creation logic)
            Pane newCard = new Pane();
            newCard.getStyleClass().add("card_Layout");

            Button cardButton = new Button("New Card");
            newCard.getChildren().add(cardButton);

            // Add the new card to previousScene (assuming cardsContainer is in previousScene)
            // Replace 'cardsContainer' with your actual container in previousScene
            Pane cardsContainer = (Pane) previousScene.getRoot().lookup("#cardsContainer");
            if (cardsContainer != null) {
                cardsContainer.getChildren().add(newCard);
            } else {
                System.out.println("Error: cardsContainer not found in previousScene.");
            }

            // Switch back to the previous scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(previousScene);
            System.out.println("Switched back to previous scene. Previous scene: " + previousScene);
        } else {
            System.out.println("Error: previousScene is null.");
        }
    }



}
