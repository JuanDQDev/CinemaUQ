package com.example.cinemauq.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de una redención: cuántos puntos se usaron, en qué compra y cuándo.
 * Recuerda de qué lotes salieron, para devolverlos si la compra se cancela.
 */
public class RedencionPuntos {
    private final String factura;
    private final int puntos;
    private final LocalDateTime fecha;
    private boolean devuelta;
    private final List<LotePuntos> lotes = new ArrayList<>();
    private final List<Double> cantidades = new ArrayList<>();

    RedencionPuntos(String factura, int puntos, LocalDateTime fecha) {
        this.factura = factura;
        this.puntos = puntos;
        this.fecha = fecha;
    }

    void registrarConsumo(LotePuntos lote, double cantidad) {
        lotes.add(lote);
        cantidades.add(cantidad);
    }

    void devolver() {
        if (devuelta) return;
        for (int i = 0; i < lotes.size(); i++) {
            lotes.get(i).devolver(cantidades.get(i));
        }
        devuelta = true;
    }

    public String getFactura() { return factura; }
    public int getPuntos() { return puntos; }
    public LocalDateTime getFecha() { return fecha; }
    /** true si la compra se canceló y los puntos volvieron a la cuenta. */
    public boolean isDevuelta() { return devuelta; }
}
