package com.example.cinemauq.model;

/**
 * Asiento de una sala, identificado por fila y numero (por ejemplo "D4").
 * Es parte del Prototype: cada asiento sabe clonarse para que la copia de la
 * sala no comparta asientos con la original.
 */
public class Asiento implements Clonable<Asiento> {

    private final char fila;
    private final int numero;
    private boolean ocupado;

    public Asiento(char fila, int numero) {
        this.fila = fila;
        this.numero = numero;
        this.ocupado = false;
    }

    /**
     * Marca el asiento como ocupado.
     * Regla del PC: un mismo asiento no puede venderse dos veces para una misma funcion.
     *
     * @throws IllegalStateException si el asiento ya estaba ocupado
     */
    public void ocupar() {
        if (ocupado) {
            throw new IllegalStateException("El asiento " + getCodigo() + " ya esta ocupado");
        }
        ocupado = true;
    }

    public void liberar() {
        ocupado = false;
    }

    public String getCodigo() {
        return String.valueOf(fila) + numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    @Override
    public Asiento clonar() {
        Asiento copia = new Asiento(fila, numero);
        copia.ocupado = this.ocupado;
        return copia;
    }
}
