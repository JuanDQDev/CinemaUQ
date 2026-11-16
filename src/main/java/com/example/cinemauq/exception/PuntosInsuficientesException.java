package com.example.cinemauq.exception;

/** El cliente intenta redimir más puntos vigentes de los que tiene. */
public class PuntosInsuficientesException extends RuntimeException {
    public PuntosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
