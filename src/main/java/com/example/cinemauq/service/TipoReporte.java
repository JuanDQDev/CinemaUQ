package com.example.cinemauq.service;

/** Reportes que el administrador puede generar (enunciado, sección 7). */
public enum TipoReporte {
    RESUMEN("Resumen estadístico del periodo"),
    VENTAS("Ventas realizadas por periodo"),
    ENTRADAS("Entradas vendidas por película y función"),
    OCUPACION("Ocupación de las salas"),
    CONFITERIA("Productos y combos vendidos"),
    RECARGAS("Recargas realizadas"),
    CANCELACIONES("Cancelaciones y reembolsos"),
    PUNTOS("Puntos acumulados y redimidos");

    private final String titulo;

    TipoReporte(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() { return titulo; }

    @Override
    public String toString() { return titulo; }
}
