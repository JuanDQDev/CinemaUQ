package com.example.cinemauq.model;

public class Producto implements ElementoProducto{
    private String nombre;
    private double precioUnitario;
    private int cantidad;

    public Producto(String nombre, double precioUnitario, int cantidad) {
        if (nombre == null || nombre.isBlank() || !Double.isFinite(precioUnitario)
                || precioUnitario < 0 || cantidad <= 0) {
            throw new IllegalArgumentException("Producto con nombre, precio o cantidad inválidos");
        }
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }



    @Override
    public double calcularPrecio() {
        return precioUnitario * cantidad;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void actualizar(String nombre, int precio){
        if(nombre == null || nombre.isBlank() || precio <= 0 || precio > 1000000) {
            throw new IllegalArgumentException("Debes escribir un nombre y un precio entre $1 y $1.000.000");
        }
        this.nombre = nombre.trim();
        this.precioUnitario = precio;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() { return cantidad; }

    @Override
    public String toString() {
        return nombre + " - $" + Math.round(calcularPrecio());
    }
}
