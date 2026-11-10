package com.example.cinemauq;

import com.example.cinemauq.controller.ContextoAplicacion;
import com.example.cinemauq.controller.PrincipalController;
import com.example.cinemauq.service.CinemaFacade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada JavaFX.
 *
 * Arma las tres capas de MVC:
 *  - Modelo: CinemaFacade.crearDemostracion() (cliente, cartelera, funciones y confitería).
 *  - Vista: principal-view.fxml, que contiene el área donde se cargan las demás vistas.
 *  - Controlador: PrincipalController, que recibe el contexto con la fachada.
 */
public class CinemaUQApplication extends Application {

    @Override
    public void start(Stage ventana) throws IOException {
        ContextoAplicacion contexto = new ContextoAplicacion(CinemaFacade.crearDemostracion());

        FXMLLoader cargador = new FXMLLoader(CinemaUQApplication.class.getResource("view/principal-view.fxml"));
        Parent raiz = cargador.load();
        PrincipalController principal = cargador.getController();
        principal.inicializar(contexto);

        Scene escena = new Scene(raiz, 1180, 760);
        escena.getStylesheets().add(
                CinemaUQApplication.class.getResource("css/cinemauq.css").toExternalForm());

        ventana.setTitle("CinemaUQ");
        ventana.setMinWidth(1040);
        ventana.setMinHeight(700);
        ventana.setScene(escena);
        ventana.show();
    }
}
