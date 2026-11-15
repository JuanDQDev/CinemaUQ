package com.example.cinemauq.controller;

import com.example.cinemauq.model.Sala;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SalasAdminController extends FormularioAdmin {
    @FXML private ComboBox<Sala> sala;
    @FXML private ComboBox<String> formato;
    @FXML private TextField numero;
    @FXML private TextField filas;
    @FXML private TextField columnas;

    @FXML
    private void initialize() {
        sala.valueProperty().addListener((propiedad, anterior, actual) -> seleccionar());
    }

    @Override
    protected void cargarCampos() {
        sala.setValue(null);
        sala.getItems().setAll(sistema.getSalas());
        formato.getItems().setAll("2D", "3D", "IMAX", "IMAX 3D");
        formato.setValue("2D");
        numero.clear();
        numero.setDisable(false);
        filas.setText("6");
        columnas.setText("10");
    }

    private void seleccionar() {
        Sala seleccion = sala.getValue();
        if (seleccion == null) return;

        numero.setText(String.valueOf(seleccion.getNumero()));
        numero.setDisable(true);
        formato.setValue(seleccion.getFormato());
        filas.setText(String.valueOf(seleccion.getFilas()));
        columnas.setText(String.valueOf(seleccion.getColumnas()));
    }

    @FXML
    private void guardarSala() {
        guardar(() -> sistema.guardarSala(sala.getValue(), entero(numero), formato.getValue(),
                entero(filas), entero(columnas)));
    }

    @FXML
    private void eliminar() {
        ejecutar(() -> {
            if (Alertas.confirmar("Eliminar sala", "No debe tener funciones futuras activas.")) {
                sistema.eliminarSala(sala.getValue());
                cargarCampos();
            }
        });
    }
}
