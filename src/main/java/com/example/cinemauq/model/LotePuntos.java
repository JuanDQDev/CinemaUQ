package com.example.cinemauq.model;

import java.time.LocalDateTime;

/**
 * Puntos ganados en una compra. Cada lote guarda su fecha de obtención y
 * vence un año después. Al redimir se descuenta de {@code disponible};
 * {@code cantidad} conserva lo que se ganó, para el historial y los reportes.
 */
public class LotePuntos {
    private double cantidad;
    private double disponible;
    private final LocalDateTime fechaObtencion;
    private final String factura;
    private boolean anulado;

    public LotePuntos(double cantidad) {
        this(cantidad, "");
    }

    public LotePuntos(double cantidad, String factura) {
        this(cantidad, factura, LocalDateTime.now());
    }

    public LotePuntos(double cantidad, String factura, LocalDateTime fechaObtencion) {
        if (fechaObtencion == null) throw new IllegalArgumentException("El lote requiere su fecha");
        this.cantidad = cantidad;
        this.disponible = cantidad;
        this.factura = factura;
        this.fechaObtencion = fechaObtencion;
    }

    public double getCantidad() {
        return cantidad;
    }

    /** Puntos del lote que todavía no se han redimido. */
    public double getDisponible() {
        return disponible;
    }

    public LocalDateTime getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaObtencion.plusYears(1);
    }

    public String getFactura() { return factura; }
    public boolean isAnulado() { return anulado; }
    public void anular() { anulado = true; }

    public boolean estaVencido() {
        return estaVencido(LocalDateTime.now());
    }

    public boolean estaVencido(LocalDateTime fecha) {
        return !fecha.isBefore(getFechaVencimiento());
    }

    /** Puntos que este lote puede aportar a una redención en la fecha dada. */
    double utilizables(LocalDateTime fecha) {
        return anulado || estaVencido(fecha) ? 0 : disponible;
    }

    void consumir(double puntos) {
        if (puntos < 0 || puntos > disponible + 0.0001) {
            throw new IllegalArgumentException("El lote no tiene esos puntos");
        }
        disponible -= puntos;
    }

    void devolver(double puntos) {
        disponible = Math.min(cantidad, disponible + puntos);
    }
}
