package com.example.cinemauq.model;

import javax.swing.text.AbstractDocument;
import java.util.List;

public class Combo implements ElementoProducto {
    private String nombreCombo;
    private double descuento;
    private List<ElementoProducto> elementos;

    public Combo(String nombreCombo, double descuento, List<ElementoProducto> elementos) {
        if (nombreCombo == null || nombreCombo.isBlank() || !Double.isFinite(descuento)
                || descuento < 0 || descuento > 1) {
            throw new IllegalArgumentException("Nombre o porcentaje de descuento inválido");
        }
        this.nombreCombo = nombreCombo;
        this.descuento = descuento;
        this.elementos = elementos;
    }


    public void AgregarProducto(ElementoProducto producto) {
        if (producto == null || producto == this || contieneCombo(producto, this)) {
            throw new IllegalArgumentException("Un combo no puede contenerse a si mismo ni formar ciclos");
        }
        elementos.add(producto);
    }


    private boolean contieneCombo(ElementoProducto producto, Conmbo buscado){
        if (producto == buscado) return true;
        if (producto instanceof Combo){
            Combo combo = (Combo) producto;
            for( ElementoProducto elemento : combo.elementos){
                if(contieneCombo(elemento, buscado)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        for (ElementoProducto elemento : elementos) total += elemento.calcularPrecio();
        return Math.round(total * (1 - descuento) * 100.0) / 100.0;
    }

    @Override
    public String getNombre() {
        return nombreCombo;
    }
    public double getDescuento() { return descuento; }
    public List<ElementoProducto> getElementos() {
        return elementos;
    }
    @Override
    public String toString() { return nombreCombo + " - $" + Math.round(calcularPrecio()); }
}
