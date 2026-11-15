package com.example.cinemauq.controller;

import com.example.cinemauq.CinemaUQApplication;
import com.example.cinemauq.service.SistemaCinema;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Carga la pestaña elegida y controla el tema y la salida del administrador. */
public class AdministracionController {
    @FXML private StackPane raiz;
    @FXML private TabPane pestanasAdmin;
    @FXML private VBox avisos;
    @FXML private Button btnTemaAdmin;

    private SistemaCinema sistema;
    private NavegadorSesion navegador;
    private boolean claro;

    public void inicializar(SistemaCinema sistema, NavegadorSesion navegador) {
        sistema.exigirAdministrador();
        this.sistema = sistema;
        this.navegador = navegador;
        Alertas.configurar(avisos, raiz);
        Alertas.setTemaClaro(false);
        btnTemaAdmin.setGraphic(Visuales.iconoTema(false));

        pestanasAdmin.getSelectionModel().selectedItemProperty().addListener(
                (propiedad, anterior, elegida) -> {
                    if (anterior != null) anterior.setContent(null);
                    mostrarPestana(elegida);
                });
        mostrarPestana(pestanasAdmin.getSelectionModel().getSelectedItem());
    }

    private void mostrarPestana(Tab pestana) {
        if (pestana == null) return;

        // El id de la pestaña coincide con el nombre de su archivo FXML.
        String archivo = "view/admin/" + pestana.getId() + "-view.fxml";
        FXMLLoader cargador = new FXMLLoader(CinemaUQApplication.class.getResource(archivo));
        try {
            ScrollPane vista = cargador.load();
            FormularioAdmin formulario = cargador.getController();
            formulario.inicializar(sistema);
            pestana.setContent(vista);
            Visuales.animarBotones((Parent) vista.getContent());
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo abrir " + pestana.getText(), error);
        }
    }

    @FXML
    private void cambiarTema() {
        claro = !claro;
        raiz.getStyleClass().remove("tema-claro");
        if (claro) raiz.getStyleClass().add("tema-claro");
        Alertas.setTemaClaro(claro);
        btnTemaAdmin.setGraphic(Visuales.iconoTema(claro));
    }

    @FXML
    private void cerrarSesion() {
        navegador.mostrarAcceso();
    }
}
