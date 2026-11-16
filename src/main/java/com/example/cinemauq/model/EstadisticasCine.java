package com.example.cinemauq.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula las estadísticas de operación del cine (taller de integración, parte C).
 *
 * Está en el modelo y no usa nada de JavaFX: recibe las compras, las funciones
 * programadas y los clientes, y entrega un {@link ResumenEstadistico}. Calcula:
 *  1. entradas vendidas por película, sumando los asientos de cada compra confirmada;
 *  2. ingresos brutos, sumando Compra.getTotal() (incluye la confitería);
 *  3. ocupación por función: asientos ocupados / capacidad de la sala.
 */
public class EstadisticasCine {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final List<Compra> compras;
    private final List<Funcion> funciones;
    private final List<Cliente> clientes;

    public EstadisticasCine(List<Compra> compras, List<Funcion> funciones, List<Cliente> clientes) {
        if (compras == null || funciones == null || clientes == null) {
            throw new IllegalArgumentException("Se requieren las compras, las funciones y los clientes");
        }
        this.compras = new ArrayList<>(compras);
        this.funciones = new ArrayList<>(funciones);
        this.clientes = new ArrayList<>(clientes);
    }

    public ResumenEstadistico calcular(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException("Revisa el intervalo de fechas");
        }
        int cantidad = 0, cancelaciones = 0, entradas = 0;
        long ingresos = 0, reembolsos = 0;
        Map<String, Integer> entradasPorPelicula = new LinkedHashMap<>();
        Map<String, Long> ingresosPorPelicula = new LinkedHashMap<>();
        Map<LocalDate, Long> ventasPorDia = new LinkedHashMap<>();
        for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) ventasPorDia.put(dia, 0L);

        for (Compra compra : compras) {
            if (compra.getFechaCancelacion() != null && enRango(compra.getFechaCancelacion().toLocalDate(), desde, hasta)) {
                cancelaciones++;
                reembolsos += compra.getReembolso();
            }
            LocalDate dia = compra.getFechaCompra().toLocalDate();
            if (!enRango(dia, desde, hasta)) continue;
            cantidad++;
            ingresos += compra.getTotal();
            ventasPorDia.merge(dia, (long) compra.getTotal(), Long::sum);
            ingresosPorPelicula.merge(compra.getNombrePelicula(), (long) compra.getTotal(), Long::sum);
            if (!compra.estaCancelada()) {
                entradas += compra.getAsientos().size();
                entradasPorPelicula.merge(compra.getNombrePelicula(), compra.getAsientos().size(), Integer::sum);
            }
        }

        long recargas = 0;
        for (Cliente cliente : clientes) {
            for (MovimientoTarjeta movimiento : cliente.getTarjeta().getMovimientos()) {
                if (movimiento.getTipo() == TipoMovimiento.RECARGA
                        && enRango(movimiento.getFechaReal().toLocalDate(), desde, hasta)) {
                    recargas += movimiento.getMonto();
                }
            }
        }

        Map<String, Double> ocupacionPorFuncion = new LinkedHashMap<>();
        long capacidad = 0, ocupados = 0;
        List<Funcion> ordenadas = new ArrayList<>(funciones);
        ordenadas.sort((a, b) -> a.getFechaHoraFuncion().compareTo(b.getFechaHoraFuncion()));
        for (Funcion funcion : ordenadas) {
            if (funcion.isCancelada() || !enRango(funcion.getFechaHoraFuncion().toLocalDate(), desde, hasta)) continue;
            Sala sala = funcion.getSalaFuncion();
            int ocupadosFuncion = sala.getCapacidad() - sala.contarDisponibles();
            capacidad += sala.getCapacidad();
            ocupados += ocupadosFuncion;
            ocupacionPorFuncion.put(funcion.getIdFuncion() + " · " + funcion.getPeliculaFuncion().getNombrePelicula()
                    + " · " + funcion.getFechaHoraFuncion().format(HORA), 100.0 * ocupadosFuncion / sala.getCapacidad());
        }
        double ocupacionPromedio = capacidad == 0 ? 0 : 100.0 * ocupados / capacidad;

        return new ResumenEstadistico(desde, hasta, cantidad, cancelaciones, entradas, ingresos, reembolsos,
                recargas, ocupacionPromedio, entradasPorPelicula, ingresosPorPelicula, ocupacionPorFuncion, ventasPorDia);
    }

    private static boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }
}
