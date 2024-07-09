package org.group.flashitv1;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class CardController {

    @FXML
    private StackPane bigCard;

    @FXML
    private VBox bigCard_Back;

    @FXML
    private VBox bigCard_Front;

    @FXML
    private Text termText;

    @FXML
    private Text answerText;

    @FXML
    private Button bigCard_FrontButton;

    @FXML
    private Button bigCard_BackButton;

    @FXML
    private Text pageNo;

    private Firestore db;
    private String username;
    private String userId;
    private String deckTitle;
    private List<QueryDocumentSnapshot> cards;
    private int currentIndex = 0;
    private double x = 0;
    private double y = 0;
    private Scene previousScene;

    @FXML
    public void initialize() {
        bigCard.getStyleClass().add("big_Card_Layout");
        bigCard_Front.getStyleClass().add("big_Card_Layout");
        bigCard_Back.getStyleClass().add("big_Card_Layout");
        bigCard_FrontButton.getStyleClass().add("big_ButtonCard_Layout");
        bigCard_BackButton.getStyleClass().add("big_ButtonCard_Layout");

        System.out.println("Initializing CardController");
        System.out.println("bigCard_FrontButton: " + bigCard_FrontButton);
        System.out.println("bigCard_BackButton: " + bigCard_BackButton);

//        this.db = db;
//        this.username = username;
//        this.userId = userId;
//        this.deckTitle = deckTitle;
//
//        loadCards();
    }

    public void initialize(Firestore db, String username, String userId, String deckTitle) {
        this.db = db;
        this.username = username;
        this.userId = userId;
        this.deckTitle = deckTitle;

        loadCards();
    }

    private void loadCards() {
        CollectionReference cardsRef = db.collection("users").document(username)
                .collection("decks").document(deckTitle).collection("cards");

        // Fetch the cards from Firestore
        ApiFuture<QuerySnapshot> querySnapshot = cardsRef.get();

        querySnapshot.addListener(() -> {
            try {
                // Copy the Firestore list to a modifiable list
                cards = new ArrayList<>(querySnapshot.get().getDocuments());

                Platform.runLater(() -> {
                    if (!cards.isEmpty()) {
                        // Reset to the first card if the current index is out of range
                        if (currentIndex >= cards.size()) {
                            currentIndex = 0;
                        }
                        showCard(currentIndex); // Show the card at the current index
                    } else {
                        // If no cards, update UI to reflect no cards
                        bigCard_FrontButton.setText("");
                        bigCard_BackButton.setText("");
                        bigCard_Front.setVisible(true);
                        bigCard_Back.setVisible(false);
                        pageNo.setText("0/0");
                    }
                    updatePageNumber(); // Update page number after loading cards
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }


    private void showCard(int index) {
        if (index < 0 || index >= cards.size()) {
            return; // Index out of bounds
        }

        DocumentSnapshot card = cards.get(index);
        String term = card.getString("term");
        String answer = card.getString("answer");

        // Debugging: Print the term and answer
        System.out.println("Term: " + term);
        System.out.println("Answer: " + answer);

        Platform.runLater(() -> {
            // Check if term and answer are not null
            if (term != null && answer != null) {
                bigCard_FrontButton.setText(term);
                bigCard_BackButton.setText(answer);

                // Ensure the front side is visible by default
                bigCard_Front.setVisible(true);
                bigCard_Back.setVisible(false);
            } else {
                System.out.println("Term or Answer is null. Skipping update.");
            }
        });

        currentIndex = index;
    }

    @FXML
    private void flipCard(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        RotateTransition rt = new RotateTransition(Duration.millis(500), bigCard);
        rt.setFromAngle(0);
        rt.setToAngle(180);
        rt.setAxis(Rotate.Y_AXIS); // Rotate around the Y-axis

        rt.setOnFinished(e -> {
            // Toggle visibility based on which button triggered the event
            if (sourceButton == bigCard_FrontButton) {
                bigCard_Front.setVisible(false);
                bigCard_FrontButton.setVisible(false);
                bigCard_Back.setVisible(true);
                bigCard_BackButton.setVisible(true);
                bigCard.setRotate(0); // Reset rotation after flip
            } else if (sourceButton == bigCard_BackButton) {
                bigCard_Back.setVisible(false);
                bigCard_BackButton.setVisible(false);
                bigCard_Front.setVisible(true);
                bigCard_FrontButton.setVisible(true);
                bigCard.setRotate(0); // Reset rotation after flip
            }
        });

        rt.play();
    }

    @FXML
    private void showNextCard() {
        if (currentIndex + 1 < cards.size()) {
            currentIndex++;
            updateCardView();
        } else {
            System.out.println("No more cards to show (Next).");
        }
    }

    @FXML
    private void showPreviousCard() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            updateCardView();
        } else {
            System.out.println("No previous cards to show (Previous).");
        }
    }

    private void updateCardView() {
        Platform.runLater(() -> {
            DocumentSnapshot card = cards.get(currentIndex);
            String term = card.getString("term");
            String answer = card.getString("answer");

            if (term != null && answer != null) {
                if (bigCard_Back.isVisible()) {
                    bigCard_FrontButton.setText(term);
                    bigCard_BackButton.setText(answer);
                    bigCard_Front.setVisible(false);
                    bigCard_Back.setVisible(true);
                } else {
                    bigCard_FrontButton.setText(term);
                    bigCard_BackButton.setText(answer);
                    bigCard_Front.setVisible(true);
                    bigCard_Back.setVisible(false);
                }
            } else {
                System.out.println("Term or Answer is null. Skipping update.");
            }

            // Update page number text
            updatePageNumber();
        });
    }

    private void updatePageNumber() {
        // Ensure the currentIndex is within the valid range
        if (cards.isEmpty()) {
            pageNo.setText("0/0");
        } else {
            pageNo.setText((currentIndex + 1) + "/" + cards.size());
        }
    }

    public void manageProfile(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previousScene = currentStage.getScene();

            // Load the FXML for the profile management
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ProfileManagement.fxml"));
            Parent root = loader.load();

            // Get the controller for ProfileManagement.fxml
            ProfileManagementController profileManagementController = loader.getController();
            profileManagementController.setPreviousScene(previousScene);
            profileManagementController.setFirestore(db); // Pass Firestore instance

            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            root.setOnMousePressed((MouseEvent mouseEvent) -> {
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent mouseEvent) -> {
                currentStage.setX(mouseEvent.getScreenX() - x);
                currentStage.setY(mouseEvent.getScreenY() - y);
            });

            currentStage.setScene(scene);
            System.out.println("Switched to Profile Management scene. Previous scene: " + previousScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    private void returnToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardMenu.fxml"));
            Parent root = loader.load();

            CardMenuController cardMenuController = loader.getController();
            cardMenuController.setFirestore(db); // Set Firestore instance
            cardMenuController.setUserDetails(username, userId); // Set user details

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load CardMenu screen.");
        }
    }

    @FXML
    private void refreshCards() {
        cards.clear();

        // Fetch the cards from Firestore again
        loadCards();
    }

    @FXML
    private void deleteCurrentCard() {
        if (currentIndex < 0 || currentIndex >= cards.size()) {
            return; // Index out of bounds
        }

        // Confirmation alert before deleting the card
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Card");
        alert.setHeaderText("Are you sure you want to delete this card?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DocumentSnapshot cardToDelete = cards.get(currentIndex);
            String cardId = cardToDelete.getId();

            // Reference to the card document in Firestore
            DocumentReference cardRef = db.collection("users").document(username)
                    .collection("decks").document(deckTitle).collection("cards").document(cardId);

            // Delete the card document from Firestore
            cardRef.delete().addListener(() -> {
                // Remove the card from the local list
                cards.remove(currentIndex);

                // Refresh the UI to reflect the changes
                Platform.runLater(() -> {
                    if (cards.isEmpty()) {
                        // If no cards are left, clear the UI
                        bigCard_FrontButton.setText("");
                        bigCard_BackButton.setText("");
                        bigCard_Front.setVisible(true);
                        bigCard_Back.setVisible(false);
                        pageNo.setText("0/0");
                    } else {
                        // Adjust the index to ensure it's within the new bounds of the cards list
                        if (currentIndex >= cards.size()) {
                            currentIndex = cards.size() - 1;
                        }
                        // Show the card at the new current index
                        showCard(currentIndex);
                    }

                    // Refresh cards to reload the deck from Firestore
                    refreshCards();
                });
            }, Runnable::run);
        }
    }
}
