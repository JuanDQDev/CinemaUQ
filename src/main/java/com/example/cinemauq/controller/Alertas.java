package com.example.cinemauq.controller;

import com.example.cinemauq.exception.AsientoOcupadoException;
import com.example.cinemauq.exception.CompraInvalidaException;
import com.example.cinemauq.exception.EdadNoPermitidaException;
import com.example.cinemauq.exception.SaldoInsuficienteException;
import com.example.cinemauq.exception.TarjetaInactivaException;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * Muestra en ventanas de diálogo los errores del dominio.
 * El mensaje viene de la excepción lanzada por el modelo; aquí solo se elige
 * el título según el tipo de excepción.
 */
public final class Alertas {

    private Alertas() {
    }

    public static void error(RuntimeException error) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo(error));
        alerta.setContentText(error.getMessage());
        alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alerta.getDialogPane().setPrefWidth(460);
        alerta.showAndWait();
    }

    public static void informacion(String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alerta.getDialogPane().setPrefWidth(460);
        alerta.showAndWait();
    }

    static String titulo(RuntimeException error) {
        if (error instanceof SaldoInsuficienteException) return "Saldo insuficiente";
        if (error instanceof TarjetaInactivaException) return "Tarjeta virtual inactiva";
        if (error instanceof AsientoOcupadoException) return "Asiento no disponible";
        if (error instanceof EdadNoPermitidaException) return "Edad no permitida para esta película";
        if (error instanceof CompraInvalidaException) return "Compra no válida";
        return "No se pudo completar la operación";
    }
}
