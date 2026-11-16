package com.example.cinemauq.model;

import com.example.cinemauq.exception.PuntosInsuficientesException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuenta de puntos de un cliente.
 *
 * Reglas (enunciado, sección 4):
 *  - cada compra acumula el 10 % de lo pagado con la tarjeta;
 *  - cada acumulación guarda su fecha y vence al año;
 *  - 1 punto equivale a $1 al redimir;
 *  - se redimen primero los puntos más próximos a vencer;
 *  - no se pueden usar puntos vencidos ni más de los disponibles.
 */
public class MontoPuntos {
    /** Porcentaje de la compra que se convierte en puntos. */
    public static final double PORCENTAJE_ACUMULACION = 0.10;

    private List<LotePuntos> lotes = new ArrayList<>();
    private final List<RedencionPuntos> redenciones = new ArrayList<>();

    public void acumularPuntos(double monto) {
        acumularPuntos(monto, "");
    }

    public void acumularPuntos(double monto, String factura) {
        acumularPuntos(monto, factura, LocalDateTime.now());
    }

    public void acumularPuntos(double monto, String factura, LocalDateTime fecha) {
        if(monto > 0) {
            lotes.add(new LotePuntos(monto * PORCENTAJE_ACUMULACION, factura, fecha));
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
        return getTotalPuntosVigentes(LocalDateTime.now());
    }

    public double getTotalPuntosVigentes(LocalDateTime fecha) {
        double total = 0;
        for (LotePuntos lote : lotes) {
            total += lote.utilizables(fecha);
        }
        return total;
    }

    /** Puntos enteros que el cliente puede redimir hoy. */
    public int getPuntosRedimibles(LocalDateTime fecha) {
        return (int) Math.floor(getTotalPuntosVigentes(fecha) + 0.0001);
    }

    /**
     * Descuenta los puntos de una compra, empezando por los lotes que vencen antes.
     * Si no alcanzan los puntos vigentes, no cambia nada y lanza la excepción.
     */
    public void redimir(int puntos, String factura, LocalDateTime fecha) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a redimir deben ser mayores que cero");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Se requiere la fecha de la redención");
        }
        if (puntos > getPuntosRedimibles(fecha)) {
            throw new PuntosInsuficientesException("Puntos insuficientes. Disponibles: "
                    + getPuntosRedimibles(fecha) + ", necesarios: " + puntos);
        }
        // Ordenar por vencimiento sin modificar la lista original.
        List<LotePuntos> porVencimiento = new ArrayList<>(lotes);
        porVencimiento.sort((a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));

        RedencionPuntos redencion = new RedencionPuntos(factura, puntos, fecha);
        double pendiente = puntos;
        for (LotePuntos lote : porVencimiento) {
            if (pendiente <= 0.0001) break;
            double tomar = Math.min(pendiente, lote.utilizables(fecha));
            if (tomar > 0) {
                lote.consumir(tomar);
                redencion.registrarConsumo(lote, tomar);
                pendiente -= tomar;
            }
        }
        redenciones.add(redencion);
    }

    /** Al cancelar una compra, los puntos que se usaron en ella vuelven a sus lotes. */
    public void devolverPorCompra(String factura) {
        for (RedencionPuntos redencion : redenciones) {
            if (redencion.getFactura().equals(factura)) redencion.devolver();
        }
    }

    public List<LotePuntos> getLotes() {
        return new ArrayList<>(lotes);
    }

    public List<RedencionPuntos> getRedenciones() {
        return new ArrayList<>(redenciones);
    }
}
