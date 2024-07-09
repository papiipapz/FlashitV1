package org.group.flashitv1;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class CardMenuController {

    private Firestore db;
    private double x = 0;
    private double y = 0;
    private Scene previousScene;
    private String username;
    private String userId;
    private boolean isDeleteMode = false;



    @FXML
    private void initialize() {
        // Add a listener to the ComboBox to filter decks based on category selection
        categoriesComboBox.setOnAction(event -> filterDecksByCategory());
    }

    public CardMenuController(){
        loadDecks();
    }

    public void setFirestore(Firestore firestore) {
        this.db = firestore;
        loadDecks();
    }

    public void setUserDetails(String username, String userId) {
        this.username = username;
        this.userId = userId;
        System.out.println("Username: " + username + ", UsernameId: " + userId);

        loadDecks();
    }

    @FXML
    private Pane Card;

    @FXML
    public FlowPane cardsContainer;

    @FXML
    private ComboBox<String> categoriesComboBox;

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
    public void openNewCard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomCard.fxml"));
            Parent root = loader.load();

            CustomCardController customCardController = loader.getController();
            customCardController.setFirestore(db);
            customCardController.setUsername(username);
            customCardController.setUserId(userId);
            customCardController.setCardMenuController(this);  // Pass the current instance
            customCardController.setPreviousScene(((Node) event.getSource()).getScene());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    void logoutApp (ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void refreshDecks() {
        // Clear the FlowPane while keeping the createNewCard button
        cardsContainer.getChildren().clear();
        cardsContainer.getChildren().add(createNewCard);  // Keep the createNewCard button

        // Reload decks from Firestore
        loadDecks();
    }

    public void loadDecks() {
        if (db == null || username == null || username.isEmpty()) {
            System.out.println("Firestore instance or username is null. Cannot load decks.");
            return;
        }

        CollectionReference decksRef = db.collection("users").document(username).collection("decks");
        ApiFuture<QuerySnapshot> querySnapshot = decksRef.get();

        querySnapshot.addListener(() -> {
            try {
                List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

                Platform.runLater(() -> {
                    categoriesComboBox.getItems().clear();
                    categoriesComboBox.getItems().add("All Categories");
                    cardsContainer.getChildren().clear(); // Ensure the container is cleared
                });

                Set<String> categories = new HashSet<>();
                for (QueryDocumentSnapshot document : documents) {
                    String deckName = document.getId();
                    String category = document.getString("category");
                    categories.add(category);

                    Button deckButton = new Button(deckName);
                    deckButton.setPrefHeight(135.0);
                    deckButton.setPrefWidth(175.0);
                    deckButton.getStyleClass().add("deck_button");
                    deckButton.setWrapText(true);
                    deckButton.setFont(new Font(19.0));

                    deckButton.setOnAction(event -> openDeck(deckName));
                    Platform.runLater(() -> {
                        cardsContainer.getChildren().add(deckButton);
                    });
                }

                Platform.runLater(() -> {
                    categoriesComboBox.getItems().addAll(categories);
                    categoriesComboBox.getSelectionModel().selectFirst();
                    if (!cardsContainer.getChildren().contains(createNewCard)) {
                        cardsContainer.getChildren().add(createNewCard);
                    }
                });

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }


    private void openDeck(String deckTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Card.fxml"));
            Parent root = loader.load();

            CardController cardController = loader.getController();
            cardController.initialize(db, username, userId, deckTitle);

            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterDecksByCategory() {
        String selectedCategory = categoriesComboBox.getValue();

        if (selectedCategory == null) {
            System.out.println("No category selected. All decks are selected.");
            return;
        }

        CollectionReference decksRef = db.collection("users").document(username).collection("decks");
        ApiFuture<QuerySnapshot> querySnapshot = decksRef.get();

        querySnapshot.addListener(() -> {
            try {
                List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();

                    for (QueryDocumentSnapshot document : documents) {
                        String deckName = document.getId();
                        String category = document.getString("category");

                        if (selectedCategory.equals("All Categories") || selectedCategory.equals(category)) {
                            Button deckButton = new Button(deckName);
                            deckButton.setPrefHeight(135.0);
                            deckButton.setPrefWidth(175.0);
                            deckButton.getStyleClass().add("deck_button");
                            deckButton.setWrapText(true);
                            deckButton.setFont(new Font(19.0));

                            deckButton.setOnAction(event -> openDeck(deckName));
                            cardsContainer.getChildren().add(deckButton);
                        }
                    }
                    if (!cardsContainer.getChildren().contains(createNewCard)) {
                        cardsContainer.getChildren().add(createNewCard);
                    }
                });

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }


    private void loadDecksByCategory(String category) {
        if (db == null) {
            System.out.println("Firestore instance is null. Cannot load decks.");
            return;
        }
        if (username == null || username.isEmpty()) {
            System.out.println("Username is null or empty. Cannot load decks.");
            return;
        }

        CollectionReference decksRef = db.collection("users").document(username).collection("decks");
        Query query = decksRef.whereEqualTo("category", category);

        // Fetch the decks from Firestore
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        querySnapshot.addListener(() -> {
            try {
                List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

                // Clear the FlowPane while keeping the "Create Card" button
                Platform.runLater(() -> {
                    cardsContainer.getChildren().clear();
                    cardsContainer.getChildren().add(createNewCard);
                });

                for (QueryDocumentSnapshot document : documents) {
                    String deckName = document.getId();
                    System.out.println("Deck found in category: " + category + " - " + deckName);

                    // Create a new button for each deck
                    Button deckButton = new Button(deckName);
                    deckButton.setPrefHeight(135.0);
                    deckButton.setPrefWidth(175.0);
                    deckButton.getStyleClass().add("deck_button");
                    deckButton.setWrapText(true);
                    deckButton.setFont(new Font(19.0));

                    // Add event handler to open the deck
                    deckButton.setOnAction(event -> openDeck(deckName));

                    // Add the deck button to the FlowPane
                    Platform.runLater(() -> {
                        cardsContainer.getChildren().add(deckButton);
                    });
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }

    @FXML
    private void toggleDeleteMode() {
        isDeleteMode = !isDeleteMode; // Toggle the delete mode flag

        // Clear the current decks and reload them based on delete mode
        cardsContainer.getChildren().clear();
        if (isDeleteMode) {
            loadDecksWithDeleteButtons();
        } else {
            loadDecks(); // Load decks without delete buttons
        }
    }


    private void loadDecksWithDeleteButtons() {
        if (db == null) {
            System.out.println("Firestore instance is null. Cannot load decks.");
            return;
        }
        if (username == null || username.isEmpty()) {
            System.out.println("Username is null or empty. Cannot load decks.");
            return;
        }

        CollectionReference decksRef = db.collection("users").document(username).collection("decks");

        // Fetch the decks from Firestore
        ApiFuture<QuerySnapshot> querySnapshot = decksRef.get();

        querySnapshot.addListener(() -> {
            try {
                List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

                for (QueryDocumentSnapshot document : documents) {
                    String deckName = document.getId();
                    System.out.println("Deck found: " + deckName);

                    // Create a container for the deck button and delete button
                    HBox deckContainer = new HBox();
                    deckContainer.setSpacing(10);

                    // Create a new button for each deck
                    Button deckButton = new Button(deckName);
                    deckButton.setPrefHeight(135.0);
                    deckButton.setPrefWidth(175.0);
                    deckButton.getStyleClass().add("deck_button");
                    deckButton.setWrapText(true);
                    deckButton.setFont(new Font(19.0));

                    // Add event handler to open the deck
                    deckButton.setOnAction(event -> openDeck(deckName));

                    // Create a delete button for each deck in delete mode
                    Button deleteButton = new Button("Delete");
                    deleteButton.setPrefHeight(135.0);
                    deleteButton.setPrefWidth(75.0);
                    deleteButton.getStyleClass().add("delete_button");

                    // Add event handler to delete the deck
                    deleteButton.setOnAction(event -> deleteDeck(deckName));

                    // Add the deck button and delete button to the container
                    deckContainer.getChildren().addAll(deckButton, deleteButton);

                    // Add the container to the FlowPane
                    Platform.runLater(() -> {
                        cardsContainer.getChildren().add(deckContainer);
                    });
                }

                // Add the "Create Card" button at the end
                Platform.runLater(() -> {
                    cardsContainer.getChildren().add(createNewCard);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }

    private void deleteDeck(String deckName) {
        // Confirmation alert before deleting the deck
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Deck");
        alert.setContentText("Are you sure you want to delete the deck '" + deckName + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed deletion, proceed with deleting the deck and its cards
            deleteCardsUnderDeck(deckName);

            // Reference to the deck document in Firestore
            DocumentReference deckRef = db.collection("users").document(username)
                    .collection("decks").document(deckName);

            // Delete the deck document from Firestore using ApiFuture
            ApiFuture<WriteResult> future = deckRef.delete();

            // Add a callback listener for completion
            future.addListener(() -> {
                Platform.runLater(() -> {
                    // Refresh the decks to reflect the deletion and exit delete mode
                    toggleDeleteMode(); // This will reload decks without delete buttons
                    System.out.println("Deck deleted successfully from Firestore: " + deckName);
                });
            }, Runnable::run);

            // Handle exceptions
            future.addListener(() -> {
                try {
                    future.get(); // Ensure any exceptions are handled
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        System.err.println("Error deleting deck from Firestore: " + e.getMessage());
                    });
                }
            }, Runnable::run);
        }
    }

    private void deleteCardsUnderDeck(String deckName) {
        // Reference to the cards collection under the deck
        CollectionReference cardsRef = db.collection("users").document(username)
                .collection("decks").document(deckName).collection("cards");

        // Delete all cards under the deck
        ApiFuture<QuerySnapshot> querySnapshot = cardsRef.get();
        querySnapshot.addListener(() -> {
            try {
                List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    String cardId = document.getId();
                    // Delete each card document
                    cardsRef.document(cardId).delete();
                    System.out.println("Deleted card: " + cardId + " under deck: " + deckName);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }
}