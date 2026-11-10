package com.example.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

public class MontoPuntos {
    private List<LotePuntos> lotes;

    public MontoPuntos() {
        this.lotes = new ArrayList<>();
    }

    public void acumularPuntos(double montoCompra) {
        double puntosGanados = montoCompra * 0.10; // Calcula el 10%

        if (puntosGanados > 0) {
            LotePuntos nuevoLote = new LotePuntos(puntosGanados);
            lotes.add(nuevoLote);
        }
    }

    public double getTotalPuntosVigentes() {
        // Limpiamos o ignoramos los que ya pasaron de 1 año
        return lotes.stream()
                .filter(lote -> !lote.estaVencido())
                .mapToDouble(LotePuntos::getCantidad)
                .sum();
    }

    public List<LotePuntos> getLotes() {
        return lotes;
    }
}
