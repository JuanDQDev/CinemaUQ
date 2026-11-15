package com.example.cinemauq.model;

final class DetalleConfiteria implements ElementoProducto {
    private final String nombre;
    private final double precio;

    DetalleConfiteria(ElementoProducto articulo) {
        nombre = articulo.getNombre();
        precio = articulo.calcularPrecio();
    }

    @Override
    public double calcularPrecio() {
        return precio;
    }

    @Override
    public String getNombre() {
        return nombre;
    }
}
