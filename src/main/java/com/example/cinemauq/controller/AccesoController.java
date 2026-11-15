package com.example.cinemauq.controller;

import com.example.cinemauq.service.SistemaCinema;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AccesoController {
    @FXML private StackPane raiz;
    @FXML private VBox notificaciones;
    @FXML private ImageView logoImagen;
    @FXML private TextField txtUsuario, txtNuevoUsuario, txtNombre, txtEdad;
    @FXML private PasswordField txtClave, txtNuevaClave, txtRepetirClave;
    @FXML private Button btnTema;
    private SistemaCinema sistema;
    private NavegadorSesion navegador;
    private boolean claro;

    public void inicializar(SistemaCinema sistema, NavegadorSesion navegador) {
        this.sistema = sistema;
        this.navegador = navegador;
        logoImagen.setImage(Visuales.logo());
        Alertas.configurar(notificaciones, raiz);
        Alertas.setTemaClaro(false);
        btnTema.setGraphic(Visuales.iconoTema(false));
    }

    @FXML private void ingresar() {
        try {
            sistema.iniciarSesion(txtUsuario.getText(), txtClave.getText());
            txtClave.clear();
            navegador.mostrarCuenta();
        } catch (RuntimeException e) { Alertas.error(e); }
    }

    @FXML private void registrar() {
        try {
            if (!txtNuevaClave.getText().equals(txtRepetirClave.getText())) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            int edad;
            try { edad = Integer.parseInt(txtEdad.getText().trim()); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Escribe tu edad en números"); }
            sistema.registrar(txtNuevoUsuario.getText(), txtNombre.getText(), edad, txtNuevaClave.getText());
            sistema.iniciarSesion(txtNuevoUsuario.getText(), txtNuevaClave.getText());
            txtNuevaClave.clear(); txtRepetirClave.clear();
            navegador.mostrarCuenta();
            Alertas.informacion("Bienvenido a CinemaUQ", "Tu tarjeta recibió $50.000 de bienvenida.");
        } catch (RuntimeException e) { Alertas.error(e); }
    }

    @FXML private void cambiarTema() {
        claro = !claro;
        raiz.getStyleClass().remove("tema-claro");
        if (claro) raiz.getStyleClass().add("tema-claro");
        Alertas.setTemaClaro(claro);
        btnTema.setGraphic(Visuales.iconoTema(claro));
    }
}
