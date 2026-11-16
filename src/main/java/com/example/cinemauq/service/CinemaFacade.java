package com.example.cinemauq.service;

import com.example.cinemauq.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Acceso del cliente al cine. Cada fachada pertenece a una sesión concreta. */
public class CinemaFacade {
    private final SistemaCinema sistema;
    private final long versionSesion;

    public CinemaFacade(SistemaCinema sistema) {
        this.sistema = sistema;
        this.versionSesion = sistema.getVersionSesion();
        sistema.exigirCliente(versionSesion);
    }

    public static CinemaFacade crearDemostracion() {
        SistemaCinema sistema = new SistemaCinema();
        Cliente cliente = sistema.registrar("1001", "Cliente de demostración", 22, "Demo123");
        sistema.iniciarSesion("A01", "Admin123");
        sistema.recargarCliente(cliente, 100000);
        sistema.iniciarSesion("1001", "Demo123");
        return new CinemaFacade(sistema);
    }

    public Cliente getCliente() { return sistema.exigirCliente(versionSesion); }
    public TarjetaVirtual getTarjeta() { return getCliente().getTarjeta(); }
    public List<Pelicula> getCartelera() { getCliente(); return sistema.getPeliculas(); }
    public List<ElementoProducto> getConfiteria() { getCliente(); return sistema.getConfiteria(); }
    public List<Compra> getCompras() { return sistema.comprasCliente(getCliente()); }
    public List<SolicitudRecarga> getSolicitudes() { return sistema.solicitudesCliente(getCliente()); }

    public List<Funcion> getFunciones(Pelicula pelicula) {
        getCliente();
        return sistema.funcionesDisponibles(pelicula);
    }

    public void solicitarRecarga(int monto) { sistema.solicitarRecarga(getCliente(), monto); }

    public Entrada getTipoEntrada(Funcion funcion) {
        getCliente();
        return sistema.getServicio().crearEntrada(funcion);
    }

    public String getPromocionActual() {
        getCliente();
        Promocion p = sistema.getServicio().promocionActual();
        return p == null ? "Sin promoción adicional" : p.getNombre() + ": " + p.getPorcentaje() + " % de descuento";
    }

    public int calcularTotal(Funcion funcion, List<String> asientos, List<ElementoProducto> productos) {
        getCliente();
        sistema.validarCarrito(funcion, productos);
        return sistema.getServicio().calcularTotal(funcion, asientos, productos);
    }

    public Compra confirmarCompra(Funcion funcion, List<String> asientos, List<ElementoProducto> productos) {
        return confirmarCompra(funcion, asientos, productos, 0, new ArrayList<>());
    }

    /** Compra pagando con puntos algunas entradas o productos del carrito (1 punto = $1). */
    public Compra confirmarCompra(Funcion funcion, List<String> asientos, List<ElementoProducto> productos,
                                  int entradasConPuntos, List<ElementoProducto> productosConPuntos) {
        Cliente cliente = getCliente();
        sistema.validarCarrito(funcion, productos);
        Compra compra = sistema.getServicio().comprar(cliente, funcion, asientos, productos,
                new AdaptadorTarjetaVirtual(cliente.getTarjeta()),
                entradasConPuntos, productosConPuntos, LocalDateTime.now());
        sistema.agregarCompra(compra);
        return compra;
    }

    /** Puntos vigentes que el cliente puede redimir en este momento. */
    public int getPuntosDisponibles() {
        return getCliente().getMontoPuntos().getPuntosRedimibles(LocalDateTime.now());
    }

    /** Puntos que cuesta la selección (entradas y productos completos). */
    public int calcularValorPuntos(Funcion funcion, int entradasConPuntos, List<ElementoProducto> productosConPuntos) {
        getCliente();
        return sistema.getServicio().calcularValorPuntos(funcion, entradasConPuntos, productosConPuntos);
    }

    public int calcularReembolso(Compra compra) {
        sistema.validarPropietario(compra, getCliente());
        return (int) Math.round(sistema.getServicio().procesarReembolso(compra, LocalDateTime.now()));
    }

    public int cancelarCompra(Compra compra, int montoConfirmado) {
        sistema.validarPropietario(compra, getCliente());
        LocalDateTime hora = LocalDateTime.now();
        int actual = (int) Math.round(sistema.getServicio().procesarReembolso(compra, hora));
        if (actual != montoConfirmado) {
            throw new IllegalStateException("El plazo cambió y el reembolso es distinto. Revisa y confirma nuevamente");
        }
        return sistema.getServicio().cancelarCompra(compra, hora);
    }
}
