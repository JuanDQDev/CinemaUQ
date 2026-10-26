package com.example.cinemauq.service;

public interface MedioPago {
    void verificar(int monto);
    void cobrar(int monto, String concepto);
    void devolver(int monto, String concepto);
}
