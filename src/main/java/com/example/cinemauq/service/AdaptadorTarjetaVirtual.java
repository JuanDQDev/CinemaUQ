package com.example.cinemauq.service;

import com.example.cinemauq.model.TarjetaVirtual;

import java.time.LocalDateTime;

// patron adapter, aqui adaptamos los metodos de tarjetavirtual a mediopago
public class AdaptadorTarjetaVirtual implements MedioPago {
    private final TarjetaVirtual tarjeta;
    /** null = usar la hora actual. Con fecha fija se registran los datos de demostración. */
    private final LocalDateTime fecha;

    public AdaptadorTarjetaVirtual(TarjetaVirtual tarjeta) {
        this(tarjeta, null);
    }

    public AdaptadorTarjetaVirtual(TarjetaVirtual tarjeta, LocalDateTime fecha) {
        if (tarjeta == null) throw new IllegalArgumentException("Tarjeta requerida");
        this.tarjeta = tarjeta;
        this.fecha = fecha;
    }

    private LocalDateTime ahora() { return fecha == null ? LocalDateTime.now() : fecha; }

    @Override public void verificar(int monto) { tarjeta.verificarPago(monto); }
    @Override public void cobrar(int monto, String concepto) { tarjeta.descontar(monto, concepto, ahora()); }
    @Override public void devolver(int monto, String concepto) { tarjeta.reembolsar(monto, concepto, ahora()); }
}
