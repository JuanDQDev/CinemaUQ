package com.example.cinemauq.model;

public class EntradaBase implements Entrada{
    private double precioBase;

    public EntradaBase(double preecioBase) {
        this.precioBase = preecioBase;
    }

    @Override
    public double calcularPrecioEntrada() {
        return precioBase;
    }

    @Override
    public String getDetalle() {
        return "Entrada Estandar: ";
    }
}
