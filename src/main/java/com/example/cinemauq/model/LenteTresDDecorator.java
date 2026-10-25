package com.example.cinemauq.model;

public class LenteTresDDecorator extends EntradaDecorator{
    private double costoAdicional;


    public LenteTresDDecorator(Entrada entrada, double costoAdicional) {
        super(entrada);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioEntrada() {
        return super.calcularPrecio() + costoAdicional;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + Lentes 3D agregados a la entrada";
    }
}
