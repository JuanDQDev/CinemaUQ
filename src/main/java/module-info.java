module com.example.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.cinemauq to javafx.fxml;
    opens com.example.cinemauq.controller to javafx.fxml;

    opens com.example.cinemauq.model;
    opens com.example.cinemauq.service;

    exports com.example.cinemauq;
    exports com.example.cinemauq.controller;
    exports com.example.cinemauq.model;
    exports com.example.cinemauq.service;
    exports com.example.cinemauq.exception;
}

