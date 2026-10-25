package com.example.cinemauq.model;

import javax.swing.text.AbstractDocument;
import java.util.List;

public class Combo implements ElementoProducto{
    private String nombreCombo;
    private double descuento;
    private List<ElementoProducto> elementos;

    public Combo(String nombreCombo, double descuento, List<ElementoProducto> elementos) {
        this.nombreCombo = nombreCombo;
        this.descuento = descuento;
        this.elementos = elementos;
    }


    public void AgregarProducto(ElementoProducto producto) {

        if (producto == null) {
            throw new IllegalArgumentException("El producto o combo a agregar no puede ser nulo.");
        }
        if (producto == this) {
            throw new IllegalArgumentException("No se puede incluir el mismo combo dentro de sí mismo.");
        }

        elementos.add(producto);
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        if (!elementos.isEmpty()) {
            for (ElementoProducto elemento : elementos) {
                total += elemento.calcularPrecio();
            }
            total *= descuento;
        }
        return total;
    }

    @Override
    public String getNombre() {
        return nombreCombo;
    }

    public List<ElementoProducto> getElementos() {
        return elementos;
    }
}
