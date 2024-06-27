module org.group.flashitv1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;

    opens org.group.flashitv1 to javafx.fxml;
    exports org.group.flashitv1;
}