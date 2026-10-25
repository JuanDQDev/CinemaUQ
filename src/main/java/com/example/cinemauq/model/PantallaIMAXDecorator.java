package com.example.cinemauq.model;

public class PantallaIMAXDecorator extends EntradaDecorator{
    private double costoAdicional;


    public PantallaIMAXDecorator(Entrada entrada, double costoAdicional) {
        super(entrada);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioEntrada() {
        return super.calcularPrecio() + costoAdicional;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + pantalla IMAX ";
    }
}
