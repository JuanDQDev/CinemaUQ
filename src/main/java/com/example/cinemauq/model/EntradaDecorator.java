package com.example.cinemauq.model;

public abstract class EntradaDecorator implements Entrada{
    protected Entrada entrada;

    public EntradaDecorator(Entrada entrada) {
        if (entrada == null){
            throw new IllegalArgumentException("Se requiere una entrada");
        }
        this.entrada = entrada;
    }

    @Override
    public double calcularPrecio(){
        return entrada.calcularPrecioEntrada();
    }

    @Override
    public String getDetalle    (){
        return entrada.getDetalle();
    }



}
