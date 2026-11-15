package com.example.cinemauq.controller;

import com.example.cinemauq.model.Pelicula;
import com.example.cinemauq.model.RestriccionPelicula;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PeliculasAdminController extends FormularioAdmin {
    @FXML private ComboBox<Pelicula> pelicula;
    @FXML private TextField titulo;
    @FXML private TextArea sinopsis;
    @FXML private ComboBox<RestriccionPelicula> edad;

    @FXML
    private void initialize() {
        pelicula.valueProperty().addListener((propiedad, anterior, actual) -> seleccionar());
    }

    @Override
    protected void cargarCampos() {
        pelicula.setValue(null);
        pelicula.getItems().setAll(sistema.getPeliculas());
        titulo.clear();
        sinopsis.clear();
        edad.getItems().setAll(RestriccionPelicula.values());
        edad.setValue(RestriccionPelicula.SIETE);
    }

    private void seleccionar() {
        Pelicula seleccion = pelicula.getValue();
        if (seleccion == null) return;

        titulo.setText(seleccion.getNombrePelicula());
        sinopsis.setText(seleccion.getDescripcion());
        edad.setValue(seleccion.getRestriccionPelicula());
    }

    @FXML
    private void guardarPelicula() {
        guardar(() -> sistema.guardarPelicula(pelicula.getValue(), titulo.getText(),
                sinopsis.getText(), edad.getValue()));
    }

    @FXML
    private void eliminar() {
        ejecutar(() -> {
            if (Alertas.confirmar("Eliminar película", "Solo se elimina si no tiene funciones futuras activas.")) {
                sistema.eliminarPelicula(pelicula.getValue());
                cargarCampos();
            }
        });
    }
}
