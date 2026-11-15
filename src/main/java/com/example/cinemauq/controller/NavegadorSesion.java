package com.example.cinemauq.controller;

import com.example.cinemauq.CinemaUQApplication;
import com.example.cinemauq.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;

/** Cambia entre acceso, cliente y administrador en la misma ventana. */
public class NavegadorSesion {
    private final Stage ventana;
    private final SistemaCinema sistema;

    public NavegadorSesion(Stage ventana, SistemaCinema sistema) {
        this.ventana = ventana;
        this.sistema = sistema;
    }

    public void iniciar() {
        ventana.setTitle("CinemaUQ · Tu próxima historia");
        ventana.getIcons().setAll(Visuales.logo());
        ventana.setMinWidth(1024);
        ventana.setMinHeight(680);
        mostrarAcceso();
        javafx.geometry.Rectangle2D pantalla = javafx.stage.Screen.getPrimary().getVisualBounds();
        ventana.setWidth(Math.min(1360, pantalla.getWidth() - 40));
        ventana.setHeight(Math.min(850, pantalla.getHeight() - 40));
        ventana.show();
    }

    private void cambiarRaiz(Parent raiz) {
        if (ventana.getScene() == null) {
            Scene escena = new Scene(raiz, 1360, 850);
            escena.getStylesheets().add(CinemaUQApplication.class.getResource("css/cinemauq.css").toExternalForm());
            ventana.setScene(escena);
        } else ventana.getScene().setRoot(raiz);
        Visuales.animarBotones(raiz);
    }

    public void mostrarAcceso() {
        sistema.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(CinemaUQApplication.class.getResource("view/acceso-view.fxml"));
            Parent raiz = loader.load();
            AccesoController controller = loader.getController();
            controller.inicializar(sistema, this);
            cambiarRaiz(raiz);
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    public void mostrarCuenta() {
        try {
            if (sistema.esAdministrador()) {
                FXMLLoader loader = new FXMLLoader(
                        CinemaUQApplication.class.getResource("view/administracion-view.fxml"));
                Parent raiz = loader.load();
                AdministracionController controller = loader.getController();
                controller.inicializar(sistema, this);
                cambiarRaiz(raiz);
                return;
            }

            FXMLLoader loader = new FXMLLoader(CinemaUQApplication.class.getResource("view/principal-view.fxml"));
            Parent raiz = loader.load();
            ContextoAplicacion contexto = new ContextoAplicacion(new CinemaFacade(sistema));
            contexto.setAlCerrarSesion(this::mostrarAcceso);
            PrincipalController controller = loader.getController();
            controller.inicializar(contexto);
            cambiarRaiz(raiz);
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }
}
