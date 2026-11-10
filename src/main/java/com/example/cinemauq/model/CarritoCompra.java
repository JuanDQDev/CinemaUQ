package com.example.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compra en curso mientras el cliente avanza por la interfaz:
 * película, función, asientos elegidos y productos de confitería.
 *
 * Es parte del Modelo en MVC: los controladores la leen y la modifican,
 * pero no guardan este estado por su cuenta. Así, al volver de una pantalla
 * a otra, la selección se conserva.
 */
public class CarritoCompra {

    private Pelicula pelicula;
    private Funcion funcion;
    private final List<String> asientos = new ArrayList<>();
    private final List<ElementoProducto> productos = new ArrayList<>();

    /**
     * Fija la película y la función. Si cambia la función, los asientos
     * elegidos dejan de tener sentido y se descartan.
     */
    public void seleccionarFuncion(Pelicula pelicula, Funcion funcion) {
        if (funcion == null || pelicula == null) {
            throw new IllegalArgumentException("Se requiere película y función");
        }
        if (this.funcion != funcion) {
            asientos.clear();
        }
        this.pelicula = pelicula;
        this.funcion = funcion;
    }

    /**
     * Agrega el asiento si no estaba elegido o lo quita si ya lo estaba.
     *
     * @return true si el asiento quedó seleccionado
     */
    public boolean alternarAsiento(String codigo) {
        if (asientos.remove(codigo)) {
            return false;
        }
        asientos.add(codigo);
        return true;
    }

    public boolean contieneAsiento(String codigo) {
        return asientos.contains(codigo);
    }

    public void agregarProducto(ElementoProducto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto requerido");
        }
        productos.add(producto);
    }

    /** Quita una unidad del producto, si hay alguna. */
    public void quitarProducto(ElementoProducto producto) {
        productos.remove(producto);
    }

    /** Unidades de un producto en el carrito (se compara por identidad). */
    public int cantidadDe(ElementoProducto producto) {
        int cantidad = 0;
        for (ElementoProducto p : productos) {
            if (p == producto) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /** Deja el carrito vacío, por ejemplo después de una compra confirmada. */
    public void vaciar() {
        pelicula = null;
        funcion = null;
        asientos.clear();
        productos.clear();
    }

    public Pelicula getPelicula() { return pelicula; }
    public Funcion getFuncion() { return funcion; }
    public List<String> getAsientos() { return new ArrayList<>(asientos); }
    public List<ElementoProducto> getProductos() { return new ArrayList<>(productos); }
}
