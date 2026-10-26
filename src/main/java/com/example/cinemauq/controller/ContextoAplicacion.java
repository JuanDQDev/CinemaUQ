package com.example.cinemauq.controller;

import com.example.cinemauq.CinemaUQApplication;
import com.example.cinemauq.model.CarritoCompra;
import com.example.cinemauq.service.CinemaFacade;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Punto de unión entre las vistas y el modelo.
 *
 * - Guarda la fachada (CinemaFacade) y el carrito de la compra en curso.
 * - Carga cada vista FXML en el área central de la ventana y le entrega
 *   este contexto a su controlador.
 * - Expone el saldo como propiedad observable para que la barra superior
 *   se actualice sola después de una compra o una recarga.
 */
public final class ContextoAplicacion {

    private final CinemaFacade fachada;
    private final CarritoCompra carrito = new CarritoCompra();
    private final IntegerProperty saldo = new SimpleIntegerProperty();
    private final ObjectProperty<Vista> vistaActual = new SimpleObjectProperty<>();
    private StackPane contenedor;

    public ContextoAplicacion(CinemaFacade fachada) {
        if (fachada == null) {
            throw new IllegalArgumentException("Se requiere la fachada del cine");
        }
        this.fachada = fachada;
        actualizarSaldo();
    }

    void setContenedor(StackPane contenedor) {
        this.contenedor = contenedor;
    }

    /** Carga la vista indicada y la muestra en el área central. */
    public void mostrar(Vista vista) {
        FXMLLoader cargador = new FXMLLoader(
                CinemaUQApplication.class.getResource("view/" + vista.getArchivo()));
        Parent raiz;
        try {
            raiz = cargador.load();
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo cargar la vista " + vista.getArchivo(), error);
        }
        Object controlador = cargador.getController();
        if (controlador instanceof Controlador) {
            ((Controlador) controlador).inicializar(this);
        }
        contenedor.getChildren().setAll(raiz);
        vistaActual.set(vista);
    }

    /** Vuelve a leer el saldo de la tarjeta del cliente. */
    public void actualizarSaldo() {
        saldo.set(fachada.getTarjeta().getSaldo());
    }

    public CinemaFacade getFachada() { return fachada; }
    public CarritoCompra getCarrito() { return carrito; }
    public ReadOnlyIntegerProperty saldoProperty() { return saldo; }
    public ReadOnlyObjectProperty<Vista> vistaActualProperty() { return vistaActual; }
}
