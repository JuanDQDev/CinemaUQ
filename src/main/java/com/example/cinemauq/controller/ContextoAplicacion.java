package com.example.cinemauq.controller;

import com.example.cinemauq.CinemaUQApplication;
import com.example.cinemauq.model.CarritoCompra;
import com.example.cinemauq.model.ObservadorSaldo;
import com.example.cinemauq.model.TarjetaVirtual;
import com.example.cinemauq.service.CinemaFacade;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class ContextoAplicacion implements ObservadorSaldo {
    private final CinemaFacade fachada;
    private final TarjetaVirtual tarjeta;
    private final CarritoCompra carrito = new CarritoCompra();
    private final IntegerProperty saldo = new SimpleIntegerProperty();
    private final ObjectProperty<Vista> vistaActual = new SimpleObjectProperty<>();
    private StackPane contenedor;
    private Parent raizAplicacion;
    private boolean temaClaro;
    private ParallelTransition transicion;
    private Runnable alCerrarSesion;

    public void setAlCerrarSesion(Runnable accion) { alCerrarSesion = accion; }
    public void cerrarSesion() {
        if (transicion != null) transicion.stop();
        tarjeta.quitarObservador(this);
        carrito.vaciar();
        if (alCerrarSesion != null) alCerrarSesion.run();
    }

    public ContextoAplicacion(CinemaFacade fachada) {
        if (fachada == null) throw new IllegalArgumentException("Se requiere la fachada");
        this.fachada = fachada;
        tarjeta = fachada.getTarjeta();
        saldo.set(tarjeta.getSaldo());
        tarjeta.agregarObservador(this);
    }

    void setContenedor(StackPane contenedor) { this.contenedor = contenedor; }

    void setRaizAplicacion(Parent raiz) {
        raizAplicacion = raiz;
        aplicarTema();
    }

    public void alternarTema() {
        temaClaro = !temaClaro;
        aplicarTema();
    }

    private void aplicarTema() {
        if (raizAplicacion == null) return;
        raizAplicacion.getStyleClass().remove("tema-claro");
        if (temaClaro) raizAplicacion.getStyleClass().add("tema-claro");
        Alertas.setTemaClaro(temaClaro);
    }

    public boolean isTemaClaro() { return temaClaro; }

    public void mostrar(Vista vista) {
        // Impedir abrir pasos que aún no tienen los datos necesarios.
        if ((vista == Vista.ASIENTOS || vista == Vista.CONFITERIA || vista == Vista.PAGO)
                && carrito.getFuncion() == null) vista = Vista.CARTELERA;
        if ((vista == Vista.CONFITERIA || vista == Vista.PAGO)
                && carrito.getAsientos().isEmpty()) vista = Vista.ASIENTOS;

        FXMLLoader cargador = new FXMLLoader(
                CinemaUQApplication.class.getResource("view/" + vista.getArchivo()));
        Parent pagina;
        try {
            pagina = cargador.load();
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo cargar " + vista.getArchivo(), error);
        }
        Object controlador = cargador.getController();
        if (controlador instanceof Controlador) {
            ((Controlador) controlador).inicializar(this);
        }
        if (pagina instanceof Region) {
            Region region = (Region) pagina;
            region.setMinWidth(0);
            region.setMinHeight(Region.USE_PREF_SIZE);
            region.setMaxWidth(1440);
        }

        // Cada página tiene su propio desplazamiento y comienza arriba.
        StackPane marco = new StackPane(pagina);
        marco.getStyleClass().add("marco-pagina");
        StackPane.setAlignment(pagina, Pos.TOP_CENTER);
        ScrollPane desplazamiento = new ScrollPane(marco);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setFitToHeight(true);
        desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        desplazamiento.getStyleClass().add("scroll-limpio");
        desplazamiento.setVvalue(0);

        if (transicion != null) transicion.stop();
        contenedor.getChildren().setAll(desplazamiento);
        vistaActual.set(vista);
        Visuales.animarBotones(pagina);
        transicion = Visuales.entrada(pagina);
    }

    @Override
    public void saldoCambiado(int nuevoSaldo) {
        saldo.set(nuevoSaldo);
    }

    public CinemaFacade getFachada() { return fachada; }
    public CarritoCompra getCarrito() { return carrito; }
    public ReadOnlyIntegerProperty saldoProperty() { return saldo; }
    public ReadOnlyObjectProperty<Vista> vistaActualProperty() { return vistaActual; }
}
