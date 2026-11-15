package com.example.cinemauq.model;

import com.example.cinemauq.exception.CompraInvalidaException;
import java.time.LocalDateTime;

public class CompraConfirmada implements EstadoCompra {

    @Override
    public void validarCancelacion(Compra compra, LocalDateTime horaActual) {
        if (compra == null || horaActual == null) {
            throw new CompraInvalidaException(
                    "Se requieren la compra y la fecha actual");
        }

        LocalDateTime fechaFuncion =
                compra.getFuncion().getFechaHoraFuncion();

        if (!fechaFuncion.isAfter(horaActual)) {
            throw new CompraInvalidaException(
                    "La función ya comenzo");
        }

        if (fechaFuncion.isBefore(horaActual.plusHours(2))) {
            throw new CompraInvalidaException(
                    "No se puede cancelar cuando faltan menos de dos horas");
        }
    }

    @Override
    public EstadoCompra cancelar(Compra compra, LocalDateTime horaActual) {
        validarCancelacion(compra, horaActual);

        return new CompraCancelada();
    }

    @Override
    public EstadoCompra cancelarPorCine() {
        return new CompraCancelada();
    }

    @Override
    public boolean estaCancelada() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Confirmada";
    }
}