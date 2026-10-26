package com.example.cinemauq.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MovimientoTarjeta {
    private final String fechaHora;
    private final TipoMovimiento tipo;
    private final int monto;
    private final int saldoFinal;
    private final String descripcion;

    public MovimientoTarjeta(TipoMovimiento tipo, int monto, int saldoFinal, String descripcion) {
        this.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        this.tipo = tipo;
        this.monto = monto;
        this.saldoFinal = saldoFinal;
        this.descripcion = descripcion;
    }
    public String getFechaHora() { return fechaHora; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getMonto() { return monto; }
    public int getSaldoFinal() { return saldoFinal; }
    public String getDescripcion() { return descripcion; }
}