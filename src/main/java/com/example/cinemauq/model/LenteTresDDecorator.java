package com.example.cinemauq.model;

public class LenteTresDDecorator extends EntradaDecorator{
    private final double costoAdicional;


    public LenteTresDDecorator(Entrada entrada, double costoAdicional) {
        super(entrada);
        if(!Double.isFinite(costoAdicional) || costoAdicional < 0){
            throw new IllegalArgumentException("El costo adicional debe de ser valido.");
        }
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
