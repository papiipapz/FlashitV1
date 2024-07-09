package org.group.flashitv1;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ProfileManagementController {
    private Scene previousScene;
    private Firestore db; // Firestore instance for database operations

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

    public void setFirestore(Firestore firestore) {
        this.db = firestore;
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Changes");
        alert.setHeaderText("Are you sure you want to save changes?");
        alert.setContentText("Click OK to confirm.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Proceed with saving changes
            String username = userInput.getText().trim();
            String currentPassword = passInput.getText().trim();
            String newPassword = newPassInput.getText().trim();

            // Validate inputs
            if (username.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
                Alert emptyFieldsAlert = new Alert(Alert.AlertType.WARNING);
                emptyFieldsAlert.setTitle("Warning");
                emptyFieldsAlert.setHeaderText(null);
                emptyFieldsAlert.setContentText("Please fill in all fields.");
                emptyFieldsAlert.showAndWait();
                return;
            }

            // Retrieve user document from Firestore
            if (db != null) {
                DocumentReference userRef = db.collection("users").document(username);

                // Use ApiFuture for asynchronous updates
                ApiFuture<DocumentSnapshot> future = userRef.get();
                ApiFutures.addCallback(future, new ApiFutureCallback<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String storedPassword = documentSnapshot.getString("password");
                            if (storedPassword != null && storedPassword.equals(currentPassword)) {
                                // Update password
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("password", newPassword);

                                // Perform the update and handle the result
                                ApiFuture<WriteResult> updateFuture = userRef.set(updates, SetOptions.merge());
                                ApiFutures.addCallback(updateFuture, new ApiFutureCallback<WriteResult>() {
                                    @Override
                                    public void onSuccess(WriteResult writeResult) {
                                        // Clear fields after successful update
                                        Platform.runLater(() -> {
                                            userInput.clear();
                                            passInput.clear();
                                            newPassInput.clear();
                                        });

                                        // Inform the user about successful save
                                        Platform.runLater(() -> {
                                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                            successAlert.setTitle("Changes Saved");
                                            successAlert.setHeaderText(null);
                                            successAlert.setContentText("Your password has been updated.");
                                            successAlert.showAndWait();
                                        });
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        handleFirestoreError("Failed to update password", throwable);
                                    }
                                }, Executors.newCachedThreadPool()); // You can adjust the executor if needed
                            } else {
                                handleAuthenticationError("Incorrect username or password");
                            }
                        } else {
                            handleSpecificError("User not found");
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        handleFirestoreError("Failed to fetch user data", throwable);
                    }
                }, Executors.newCachedThreadPool()); // You can adjust the executor if needed
            } else {
                // Handle case where Firestore instance is null
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Firestore instance is null. Cannot save changes.");
                    errorAlert.showAndWait();
                });
            }
        }
    }

    // Helper method for handling Firestore errors
    private void handleFirestoreError(String message, Throwable throwable) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Firestore Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(message + ": " + throwable.getMessage());
            errorAlert.showAndWait();
        });
    }

    // Helper method for handling authentication errors
    private void handleAuthenticationError(String message) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Authentication Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(message);
            errorAlert.showAndWait();
        });
    }

    // Helper method for handling specific error scenarios
    private void handleSpecificError(String message) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(message);
            errorAlert.showAndWait();
        });
    }

}