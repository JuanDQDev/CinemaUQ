package com.example.cinemauq.model;

public class EntradaBase implements Entrada{
    private double precioBase;

    public EntradaBase(double precioBase) {
        if (!Double.isFinite(precioBase) || precioBase < 0) {
            throw new IllegalArgumentException("Precio de base debe ser positivo");
        }
        this.precioBase = precioBase;
    }

    @Override
    public double calcularPrecioEntrada() {
        return precioBase;
    }

    @Override
    public String getDetalle() {
        return "Entrada estándar";
    }
}
