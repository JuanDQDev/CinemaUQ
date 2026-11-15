package com.example.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

public class MontoPuntos {
    private List<LotePuntos> lotes = new ArrayList<>();

    public void acumularPuntos(double monto) {
        acumularPuntos(monto, "");
    }

    public void acumularPuntos(double monto, String factura) {
        if(monto > 0) {
            lotes.add(new LotePuntos(monto * 0.10, factura));
        }
    }

    public void anularPorCompra(String factura){
        for (LotePuntos lote : lotes) {
            if(lote.getFactura().equals(factura)){
                lote.anular();
            }
        }
    }

    public double getTotalPuntosVigentes() {
        double total = 0;
        for (LotePuntos lote : lotes) {
            if (!lote.estaVencido() && !lote.isAnulado()) total += lote.getCantidad();
        }
        return total;
    }

    public List<LotePuntos> getLotes() {
        return new ArrayList<>(lotes);
    }
}
