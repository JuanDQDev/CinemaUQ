package com.example.cinemauq.controller;

import com.example.cinemauq.model.Promocion;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class PromocionesAdminController extends FormularioAdmin {
    @FXML private ComboBox<Promocion> promocion;
    @FXML private TextField nombre;
    @FXML private TextField porcentaje;
    @FXML private DatePicker desde;
    @FXML private DatePicker hasta;
    @FXML private CheckBox activa;

    @FXML
    private void initialize() {
        promocion.valueProperty().addListener((propiedad, anterior, actual) -> seleccionar());
    }

    @Override
    protected void cargarCampos() {
        promocion.setValue(null);
        promocion.getItems().setAll(sistema.getPromociones());
        nombre.clear();
        porcentaje.setText("10");
        desde.setValue(LocalDate.now());
        hasta.setValue(LocalDate.now().plusDays(7));
        activa.setSelected(true);
    }

    private void seleccionar() {
        Promocion elegida = promocion.getValue();
        if (elegida == null) return;

        nombre.setText(elegida.getNombre());
        porcentaje.setText(String.valueOf(elegida.getPorcentaje()));
        desde.setValue(elegida.getDesde());
        hasta.setValue(elegida.getHasta());
        activa.setSelected(elegida.isActiva());
    }

    @FXML
    private void guardarPromocion() {
        guardar(() -> sistema.guardarPromocion(promocion.getValue(), nombre.getText(),
                entero(porcentaje), desde.getValue(), hasta.getValue(), activa.isSelected()));
    }

    @FXML
    private void eliminar() {
        ejecutar(() -> {
            if (Alertas.confirmar("Eliminar promoción", "Dejará de aplicarse a nuevas compras.")) {
                sistema.eliminarPromocion(promocion.getValue());
                cargarCampos();
            }
        });
    }
}
