package com.example.cinemauq.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * Estadísticas de un periodo, ya calculadas por {@link EstadisticasCine}.
 * Solo contiene datos: las vistas reciben cada Map y lo dibujan sin calcular nada.
 */
public class ResumenEstadistico {
    private final LocalDate desde;
    private final LocalDate hasta;
    private final int compras;
    private final int cancelaciones;
    private final int entradasVendidas;
    private final long ingresosBrutos;
    private final long reembolsos;
    private final long recargas;
    private final double ocupacionPromedio;
    private final Map<String, Integer> entradasPorPelicula;
    private final Map<String, Long> ingresosPorPelicula;
    private final Map<String, Double> ocupacionPorFuncion;
    private final Map<LocalDate, Long> ventasPorDia;

    ResumenEstadistico(LocalDate desde, LocalDate hasta, int compras, int cancelaciones, int entradasVendidas,
                       long ingresosBrutos, long reembolsos, long recargas, double ocupacionPromedio,
                       Map<String, Integer> entradasPorPelicula, Map<String, Long> ingresosPorPelicula,
                       Map<String, Double> ocupacionPorFuncion, Map<LocalDate, Long> ventasPorDia) {
        this.desde = desde;
        this.hasta = hasta;
        this.compras = compras;
        this.cancelaciones = cancelaciones;
        this.entradasVendidas = entradasVendidas;
        this.ingresosBrutos = ingresosBrutos;
        this.reembolsos = reembolsos;
        this.recargas = recargas;
        this.ocupacionPromedio = ocupacionPromedio;
        this.entradasPorPelicula = entradasPorPelicula;
        this.ingresosPorPelicula = ingresosPorPelicula;
        this.ocupacionPorFuncion = ocupacionPorFuncion;
        this.ventasPorDia = ventasPorDia;
    }

    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    /** Compras hechas en el periodo (incluye las que luego se cancelaron). */
    public int getCompras() { return compras; }
    /** Cancelaciones realizadas en el periodo. */
    public int getCancelaciones() { return cancelaciones; }
    /** Asientos de las compras del periodo que siguen confirmadas. */
    public int getEntradasVendidas() { return entradasVendidas; }
    /** Suma de Compra.getTotal() de las compras del periodo, incluida la confitería. */
    public long getIngresosBrutos() { return ingresosBrutos; }
    public long getReembolsos() { return reembolsos; }
    public long getIngresosNetos() { return ingresosBrutos - reembolsos; }
    public long getRecargas() { return recargas; }
    /** Porcentaje de asientos ocupados en las funciones del periodo. */
    public double getOcupacionPromedio() { return ocupacionPromedio; }
    /** Título de la película → entradas vendidas (gráfico de barras). */
    public Map<String, Integer> getEntradasPorPelicula() { return entradasPorPelicula; }
    /** Título de la película → ingresos brutos (gráfico de pastel). */
    public Map<String, Long> getIngresosPorPelicula() { return ingresosPorPelicula; }
    /** Función → porcentaje de ocupación. */
    public Map<String, Double> getOcupacionPorFuncion() { return ocupacionPorFuncion; }
    /** Día → ventas brutas; incluye los días sin ventas (gráfico de líneas). */
    public Map<LocalDate, Long> getVentasPorDia() { return ventasPorDia; }
}
