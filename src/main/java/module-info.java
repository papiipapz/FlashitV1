module org.group.flashitv1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires com.google.auth;
    requires firebase.admin;
    requires google.cloud.firestore;
    requires com.google.auth.oauth2;
    requires com.google.api.apicommon;
    requires google.cloud.core;
    requires javafx.swing;

    opens org.group.flashitv1 to javafx.fxml;
    exports org.group.flashitv1;
}