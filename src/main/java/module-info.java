module com.example.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cinemauq to javafx.fxml;
    exports com.example.cinemauq;
    exports com.example.cinemauq.view;
    opens com.example.cinemauq.view to javafx.fxml;
    exports com.example.cinemauq.model;
    opens com.example.cinemauq.model to javafx.fxml;
}