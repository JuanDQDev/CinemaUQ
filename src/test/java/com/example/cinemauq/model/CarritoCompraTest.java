package com.example.cinemauq.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del carrito que usa la interfaz durante la compra. */
class CarritoCompraTest {

    private final Pelicula pelicula =
            new Pelicula("P01", "Película de prueba", "Descripción", RestriccionPelicula.SIETE);

    private Funcion funcion(String id) {
        return new Funcion.Builder().conIdFuncion(id)
                .conFechaHora(LocalDateTime.now().plusDays(1)).conIdioma("Español")
                .conSala(new Sala(1, "2D", 5, 8)).conPelicula(pelicula).build();
    }

    @Test
    void alternarAsientoLoAgregaYLoQuita() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.seleccionarFuncion(pelicula, funcion("F-1"));

        assertTrue(carrito.alternarAsiento("D4"));
        assertTrue(carrito.contieneAsiento("D4"));
        assertFalse(carrito.alternarAsiento("D4"));
        assertTrue(carrito.getAsientos().isEmpty());
    }

    @Test
    void cambiarDeFuncionDescartaLosAsientos() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.seleccionarFuncion(pelicula, funcion("F-1"));
        carrito.alternarAsiento("A1");

        carrito.seleccionarFuncion(pelicula, funcion("F-2"));

        assertTrue(carrito.getAsientos().isEmpty());
    }

    @Test
    void cuentaUnidadesDeCadaProducto() {
        CarritoCompra carrito = new CarritoCompra();
        Producto gaseosa = new Producto("Gaseosa", 7000, 1);
        carrito.agregarProducto(gaseosa);
        carrito.agregarProducto(gaseosa);
        carrito.quitarProducto(gaseosa);

        assertEquals(1, carrito.cantidadDe(gaseosa));
        assertEquals(1, carrito.getProductos().size());
    }

    @Test
    void vaciarDejaElCarritoSinDatos() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.seleccionarFuncion(pelicula, funcion("F-1"));
        carrito.alternarAsiento("A1");
        carrito.agregarProducto(new Producto("Nachos", 11000, 1));

        carrito.vaciar();

        assertNull(carrito.getFuncion());
        assertTrue(carrito.getAsientos().isEmpty());
        assertTrue(carrito.getProductos().isEmpty());
    }
}
