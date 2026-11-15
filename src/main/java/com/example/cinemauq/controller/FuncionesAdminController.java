package com.example.cinemauq.controller;

import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.Pelicula;
import com.example.cinemauq.model.Sala;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class FuncionesAdminController extends FormularioAdmin {
    @FXML private ComboBox<Funcion> funcion;
    @FXML private ComboBox<Pelicula> pelicula;
    @FXML private ComboBox<Sala> sala;
    @FXML private DatePicker fecha;
    @FXML private TextField hora;
    @FXML private TextField idioma;

    @FXML
    private void initialize() {
        funcion.valueProperty().addListener((propiedad, anterior, actual) -> seleccionar());
    }

    @Override
    protected void cargarCampos() {
        funcion.setValue(null);
        funcion.getItems().setAll(sistema.getFuncionesAdministracion());
        pelicula.setValue(null);
        pelicula.getItems().setAll(sistema.getPeliculas());
        sala.setValue(null);
        sala.getItems().setAll(sistema.getSalas());
        fecha.setValue(LocalDate.now().plusDays(2));
        hora.setText("18:00");
        idioma.setText("Doblada");
    }

    private void seleccionar() {
        Funcion elegida = funcion.getValue();
        if (elegida == null) return;

        pelicula.setValue(elegida.getPeliculaFuncion());
        fecha.setValue(elegida.getFechaHoraFuncion().toLocalDate());
        hora.setText(elegida.getFechaHoraFuncion().toLocalTime().toString());
        idioma.setText(elegida.getIdioma());
        sala.setValue(null);
        for (Sala opcion : sala.getItems()) {
            if (opcion.getNumero() == elegida.getSalaFuncion().getNumero()) {
                sala.setValue(opcion);
                break;
            }
        }
    }

    @FXML
    private void guardarFuncion() {
        guardar(() -> sistema.guardarFuncion(funcion.getValue(), pelicula.getValue(),
                sala.getValue(), leerFechaHora(), idioma.getText()));
    }

    private LocalDateTime leerFechaHora() {
        if (fecha.getValue() == null) throw new IllegalArgumentException("Selecciona una fecha");
        try {
            return LocalDateTime.of(fecha.getValue(), LocalTime.parse(hora.getText().trim()));
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Revisa la hora: por ejemplo 18:30");
        }
    }

    @FXML
    private void eliminar() {
        ejecutar(() -> {
            if (Alertas.confirmar("Eliminar función", "Solo se elimina si nunca tuvo compras.")) {
                sistema.eliminarFuncion(funcion.getValue());
                cargarCampos();
            }
        });
    }

    @FXML
    private void cancelarFuncion() {
        ejecutar(() -> {
            Funcion elegida = funcion.getValue();
            if (elegida == null) throw new IllegalArgumentException("Selecciona una función");
            String mensaje = elegida + ". Se devolverá el 100 % a cada comprador pendiente de reembolso. ¿Continuar?";
            if (Alertas.confirmar("Cancelar función", mensaje)) {
                int cantidad = sistema.cancelarFuncion(elegida);
                cargarCampos();
                Alertas.informacion("Función cancelada", cantidad + " compras reembolsadas.");
            }
        });
    }
}
