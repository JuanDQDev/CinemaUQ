package com.example.cinemauq.model;

import java.time.LocalDateTime;

public class LotePuntos {
    private double cantidad;
    private LocalDateTime fechaObtencion = LocalDateTime.now();
    private final String factura;
    private boolean anulado;

    public LotePuntos(double cantidad) {
        this(cantidad, "");
    }

    public LotePuntos(double cantidad, String factura) {
        this.cantidad = cantidad;
        this.factura = factura;
    }

    public double getCantidad() {
        return cantidad;
    }
    public LocalDateTime getFechaObtencion() {
        return fechaObtencion;
    }
    public String getFactura() { return factura; }
    public boolean isAnulado() { return anulado; }
    public void anular() { anulado = true; }
    public boolean estaVencido() {
        return !LocalDateTime.now().isBefore(fechaObtencion.plusYears(1));
    }
}
