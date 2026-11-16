package com.example.cinemauq.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class MovimientoTarjeta {
    private final LocalDateTime fechaReal;
    private final String fechaHora;
    private final TipoMovimiento tipo;
    private final int monto;
    private final int saldoFinal;
    private final String descripcion;

    public MovimientoTarjeta(TipoMovimiento tipo, int monto, int saldoFinal, String descripcion) {
        this(tipo, monto, saldoFinal, descripcion, LocalDateTime.now());
    }

    public MovimientoTarjeta(TipoMovimiento tipo, int monto, int saldoFinal, String descripcion, LocalDateTime fecha) {
        if (fecha == null) throw new IllegalArgumentException("El movimiento requiere su fecha");
        this.fechaReal = fecha;
        this.fechaHora = fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.tipo = tipo;
        this.monto = monto;
        this.saldoFinal = saldoFinal;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaReal() { return fechaReal; }
    public String getFechaHora() { return fechaHora; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getMonto() { return monto; }
    public int getSaldoFinal() { return saldoFinal; }
    public String getDescripcion() { return descripcion; }
}