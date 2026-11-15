package com.example.cinemauq.controller;

import com.example.cinemauq.service.SistemaCinema;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/** Operaciones comunes de los formularios. Cada pestaña carga sus propios campos. */
public abstract class FormularioAdmin {
    protected SistemaCinema sistema;

    public final void inicializar(SistemaCinema sistema) {
        sistema.exigirAdministrador();
        this.sistema = sistema;
        cargarCampos();
    }

    protected abstract void cargarCampos();

    @FXML
    protected final void limpiar() {
        ejecutar(this::cargarCampos);
    }

    protected int entero(TextField campo) {
        String texto = campo.getText().trim().replace(".", "").replace("$", "");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Revisa los campos numéricos: usa números enteros");
        }
    }

    protected void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException error) {
            Alertas.error(error);
        }
    }

    protected void guardar(Runnable cambio) {
        ejecutar(() -> {
            cambio.run();
            cargarCampos();
            Alertas.informacion("Cambios guardados", "La información del cine se actualizó.");
        });
    }
}
