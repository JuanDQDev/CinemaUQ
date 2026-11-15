package com.example.cinemauq.model;

import com.example.cinemauq.exception.CompraInvalidaException;
import java.time.LocalDateTime;

public class CompraCancelada implements EstadoCompra {

    @Override
    public void validarCancelacion(Compra compra, LocalDateTime horaActual) {
        throw new CompraInvalidaException(
                "La compra ya esta cancelada");
    }

    @Override
    public EstadoCompra cancelar(Compra compra, LocalDateTime horaActual) {
        throw new CompraInvalidaException(
                "La compra ya esta cancelada");
    }

    @Override
    public EstadoCompra cancelarPorCine() {
        throw new CompraInvalidaException(
                "La compra ya esta cancelada"
        );
    }

    @Override
    public boolean estaCancelada() {
        return true;
    }

    @Override
    public String getNombre() {
        return "Cancelada";
    }
}