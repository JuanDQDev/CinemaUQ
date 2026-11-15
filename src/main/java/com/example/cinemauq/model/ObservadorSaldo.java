package com.example.cinemauq.model;

/** Recibe el nuevo saldo cuando una operación de tarjeta lo cambia. */
public interface ObservadorSaldo {
    void saldoCambiado(int nuevoSaldo);
}
