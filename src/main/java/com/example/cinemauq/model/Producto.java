package com.example.cinemauq.model;

public class Producto implements ElementoProducto{
    private String nombre;
    private double precioUnitario;
    private int cantidad;

    public Producto(String nombre, double precioUnitario, int cantidad) {
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }



    @Override
    public double calcularPrecio() {
        return precioUnitario*cantidad;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
