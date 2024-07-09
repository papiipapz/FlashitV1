package org.group.flashitv1;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CustomCardController {

    private Firestore db;
    private Scene previousScene;
    private int questionCounter = 2;
    private String username;
    private String userId;
    private CardMenuController cardMenuController;

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

    public void setFirestore(Firestore firestore) {
        this.db = firestore;
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println("Username set in CustomCardController: " + username);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        System.out.println("UserId set in CustomCardController: " + userId);
    }

    public void setCardMenuController(CardMenuController cardMenuController) {
        this.cardMenuController = cardMenuController;
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    void addQuestionContainer(ActionEvent event) {
        Platform.runLater(() -> {
            VBox newQuestionsBox = new VBox();
            newQuestionsBox.setSpacing(5);

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

            newQuestionsBox.getChildren().addAll(newQuestionText, newQuestionInput, newAnswerText, newAnswerInput);
            cardForm.getChildren().add(cardForm.getChildren().indexOf(addQuestions), newQuestionsBox);

            questionCounter++;
        });
    }

    @FXML
    void createNewCard(ActionEvent event) {
        String deckTitle = titleInput.getText();
        String description = descriptionInput.getText();
        String category = categoryInput.getText();

        if (deckTitle.isEmpty() || description.isEmpty() || category.isEmpty()) {
            System.out.println("Please fill in all deck fields.");
            return;
        }

        List<Map<String, String>> cardsData = new ArrayList<>();

        // Collect data from dynamically added question-answer pairs
        for (Node node : cardForm.getChildren()) {
            if (node instanceof VBox) {
                VBox questionBox = (VBox) node;
                if (questionBox.getChildren().size() == 4) {
                    Node qTextArea = questionBox.getChildren().get(1);
                    Node aTextArea = questionBox.getChildren().get(3);

                    if (qTextArea instanceof TextArea && aTextArea instanceof TextArea) {
                        String dynamicTerm = ((TextArea) qTextArea).getText();
                        String dynamicAnswer = ((TextArea) aTextArea).getText();

                        if (!dynamicTerm.isEmpty() && !dynamicAnswer.isEmpty()) {
                            Map<String, String> dynamicCardData = new HashMap<>();
                            dynamicCardData.put("term", dynamicTerm);
                            dynamicCardData.put("answer", dynamicAnswer);
                            cardsData.add(dynamicCardData);
                        }
                    }
                }
            }
        }

        if (username != null && !username.isEmpty()) {
            DocumentReference userRef = db.collection("users").document(username);
            DocumentReference deckRef = userRef.collection("decks").document(deckTitle);

            Map<String, Object> deckData = new HashMap<>();
            deckData.put("description", description);
            deckData.put("category", category);

            CompletableFuture<Void> deckFuture = CompletableFuture.runAsync(() -> {
                try {
                    deckRef.set(deckData, SetOptions.merge()).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            deckFuture.thenRunAsync(() -> {
                for (int i = 0; i < cardsData.size(); i++) {
                    Map<String, String> cardData = cardsData.get(i);
                    String cardId = UUID.randomUUID().toString();
                    DocumentReference cardRef = deckRef.collection("cards").document(cardId);
                    ApiFuture<WriteResult> cardResult = cardRef.set(cardData);

                    int finalI = i;
                    cardResult.addListener(() -> {
                        try {
                            System.out.println("Card created and saved successfully: " + cardData);
                        } catch (Exception e) {
                            System.err.println("Error saving card: " + e.getMessage());
                        }

                        if (finalI == cardsData.size() - 1) {
                            Platform.runLater(() -> {
                                if (previousScene != null) {
                                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    currentStage.setScene(previousScene);
                                    System.out.println("Switched back to previous scene. Previous scene: " + previousScene);

                                    if (cardMenuController != null) {
                                        cardMenuController.loadDecks();
                                    } else {
                                        System.out.println("CardMenuController is null. Cannot refresh deck list.");
                                    }
                                } else {
                                    System.out.println("Cannot switch back to previous scene. Previous scene is null.");
                                }
                            });
                        }
                    }, Runnable::run);
                }
            });
        } else {
            System.out.println("Username is null or empty. Cannot save card.");
        }
    }

    @FXML
    public void switchBackToPreviousScene(ActionEvent event) {
        if (previousScene != null) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(previousScene);
            System.out.println("Switched back to previous scene. Previous scene: " + previousScene);

            if (cardMenuController != null) {
                cardMenuController.loadDecks();
            } else {
                System.out.println("CardMenuController is null. Cannot refresh deck list.");
            }
        } else {
            System.out.println("Cannot switch back to previous scene. Previous scene is null.");
        }
    }
}