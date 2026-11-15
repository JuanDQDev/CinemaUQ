package com.example.cinemauq.service;

import com.example.cinemauq.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reporte sencillo en texto: no necesita librerías adicionales. */
public class ServicioReportes {
    
    public String generar(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        List<Compra> compras = sistema.getTodasCompras();
        long ventas = 0, devoluciones = 0, recargas = 0;
        int cantidad = 0, cancelaciones = 0;
        StringBuilder detalle = new StringBuilder();
        for (Compra c : compras) {
            if (enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
                ventas += c.getTotal(); cantidad++;
                detalle.append(c.getFactura()).append(" | ").append(c.getCliente().getId())
                        .append(" | ").append(c.getNombrePelicula()).append(" | ")
                        .append(c.getEstado()).append(" | pagado $").append(c.getTotal())
                        .append(" | devuelto acumulado $").append(c.getReembolso()).append("\n");
            }
            if (c.getFechaCancelacion() != null && enRango(c.getFechaCancelacion().toLocalDate(), desde, hasta)) {
                devoluciones += c.getReembolso(); cancelaciones++;
            }
        }
        for (Cliente cliente : sistema.getClientes()) {
            for (MovimientoTarjeta m : cliente.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && enRango(m.getFechaReal().toLocalDate(), desde, hasta)) recargas += m.getMonto();
            }
        }
        StringBuilder texto = new StringBuilder("CINEMAUQ - REPORTE\n");
        texto.append(desde).append(" a ").append(hasta).append("\n\n")
                .append("Compras del periodo: ").append(cantidad).append("\n")
                .append("Ventas brutas: $").append(ventas).append("\n")
                .append("Reembolsos realizados en el periodo: $").append(devoluciones).append("\n")
                .append("Ventas menos reembolsos del periodo: $").append(ventas - devoluciones).append("\n")
                .append("Cancelaciones realizadas: ").append(cancelaciones).append("\n")
                .append("Recargas aprobadas (sin bonos): $").append(recargas).append("\n\nCOMPRAS\n")
                .append(detalle).append("\nFUNCIONES Y OCUPACION ACTUAL (fecha de función dentro del periodo)\n");
        for (Funcion f : sistema.getFuncionesAdministracion()) {
            if (enRango(f.getFechaHoraFuncion().toLocalDate(), desde, hasta)) {
                Sala sala = f.getSalaFuncion();
                texto.append(f).append(" | ocupados ").append(sala.getCapacidad() - sala.contarDisponibles())
                        .append("/").append(sala.getCapacidad()).append("\n");
            }
        }
        texto.append("\nPRODUCTOS Y COMBOS (unidades de compras del periodo que siguen confirmadas)\n");
        List<String> nombres = new ArrayList<>();
        for (Compra c : compras) if (!c.estaCancelada() && enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
            for (ElementoProducto p : c.getConfiteria()) if (!nombres.contains(p.getNombre())) nombres.add(p.getNombre());
        }
        for (String nombre : nombres) {
            int unidades = 0;
            for (Compra c : compras) if (!c.estaCancelada() && enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
                for (ElementoProducto p : c.getConfiteria()) if (nombre.equals(p.getNombre())) unidades++;
            }
            texto.append(nombre).append(": ").append(unidades).append("\n");
        }
        texto.append("\nCLIENTES - SALDOS Y PUNTOS VIGENTES ACTUALES\n");
        for (Cliente c : sistema.getClientes()) {
            texto.append(c).append(" | saldo $").append(c.getTarjeta().getSaldo())
                    .append(" | puntos ").append(c.getMontoPuntos().getTotalPuntosVigentes()).append("\n");
        }
        return texto.toString();
    }

    public List<VentaPelicula> ventasPorPelicula(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        List<VentaPelicula> ventas = new ArrayList<>();
        for (Compra compra : sistema.getTodasCompras()) {
            VentaPelicula venta = buscarVenta(ventas, compra.getNombrePelicula());
            if (enRango(compra.getFechaCompra().toLocalDate(), desde, hasta)) {
                venta.total += compra.getTotal();
            }
        }
        return ventas;
    }

    private VentaPelicula buscarVenta(List<VentaPelicula> ventas, String titulo) {
        for (VentaPelicula venta : ventas) {
            if (venta.titulo.equals(titulo)) return venta;
        }
        VentaPelicula nueva = new VentaPelicula(titulo);
        ventas.add(nueva);
        return nueva;
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException("Revisa el intervalo de fechas");
        }
    }

    public static final class VentaPelicula {
        private final String titulo;
        private long total;

        private VentaPelicula(String titulo) {
            this.titulo = titulo;
        }

        public String getTitulo() { return titulo; }
        public long getTotal() { return total; }
    }

    private boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }
}
