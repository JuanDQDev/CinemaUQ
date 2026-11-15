package com.example.cinemauq.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TarifasAdminController extends FormularioAdmin {
    @FXML private TextField tarifaBase;
    @FXML private TextField recargo3D;
    @FXML private TextField recargoIMAX;

    @Override
    protected void cargarCampos() {
        int[] tarifas = sistema.getTarifas();
        tarifaBase.setText(String.valueOf(tarifas[0]));
        recargo3D.setText(String.valueOf(tarifas[1]));
        recargoIMAX.setText(String.valueOf(tarifas[2]));
    }

    @FXML
    private void guardarTarifas() {
        guardar(() -> sistema.cambiarTarifas(entero(tarifaBase),
                entero(recargo3D), entero(recargoIMAX)));
    }
}
