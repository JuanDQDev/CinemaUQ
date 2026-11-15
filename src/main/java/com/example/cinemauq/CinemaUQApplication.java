package com.example.cinemauq;


import com.example.cinemauq.controller.NavegadorSesion;
import com.example.cinemauq.service.SistemaCinema;
import javafx.application.Application;
import javafx.stage.Stage;

public class CinemaUQApplication extends Application {

    @Override
    public void start(Stage ventana) {
        new NavegadorSesion(ventana, new SistemaCinema()).iniciar();
    }
}
