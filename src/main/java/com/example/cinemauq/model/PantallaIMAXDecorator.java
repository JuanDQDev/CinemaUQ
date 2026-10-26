package com.example.cinemauq.model;

public class PantallaIMAXDecorator extends EntradaDecorator{
    private double costoAdicional;

    public PantallaIMAXDecorator(Entrada entrada, double costoAdicional) {
        super(entrada);
        if (!Double.isFinite(costoAdicional) || costoAdicional < 0) {
            throw new IllegalArgumentException("El costo adicional debe ser valido");
        }
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioEntrada() {
        return super.calcularPrecioEntrada() + costoAdicional;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + pantalla IMAX";
    }
}
