package com.example.cinemauq.service;

import com.example.cinemauq.model.TarjetaVirtual;

// patron adapter, aqui adaptamos los metodos de tarjetavirtual a mediopago
public class AdaptadorTarjetaVirtual implements MedioPago {
    private final TarjetaVirtual tarjeta;

    public AdaptadorTarjetaVirtual(TarjetaVirtual tarjeta) {
        if (tarjeta == null) throw new IllegalArgumentException("Tarjeta requerida");
        this.tarjeta = tarjeta;
    }

    @Override public void verificar(int monto) { tarjeta.verificarPago(monto); }
    @Override public void cobrar(int monto, String concepto) { tarjeta.descontar(monto, concepto); }
    @Override public void devolver(int monto, String concepto) { tarjeta.reembolsar(monto, concepto); }
}
