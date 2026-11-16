package com.example.cinemauq.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contenido de un reporte ya generado, sin formato de archivo. Tiene lo que pide
 * el enunciado: título, fecha de generación, periodo consultado, registros y totales.
 * Un {@link ExportadorReporte} decide cómo se escribe (hoy, TXT).
 */
public class Reporte {
    private final String titulo;
    private final LocalDateTime fechaGeneracion;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final List<String> encabezados;
    private final List<List<String>> filas = new ArrayList<>();
    private final List<String[]> totales = new ArrayList<>();

    Reporte(String titulo, LocalDate desde, LocalDate hasta, List<String> encabezados) {
        this.titulo = titulo;
        this.fechaGeneracion = LocalDateTime.now();
        this.desde = desde;
        this.hasta = hasta;
        this.encabezados = new ArrayList<>(encabezados);
    }

    void agregarFila(String... valores) {
        if (valores.length != encabezados.size()) {
            throw new IllegalArgumentException("La fila no coincide con los encabezados de " + titulo);
        }
        filas.add(List.of(valores));
    }

    void agregarTotal(String nombre, String valor) {
        totales.add(new String[]{nombre, valor});
    }

    public String getTitulo() { return titulo; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public List<String> getEncabezados() { return new ArrayList<>(encabezados); }
    public List<List<String>> getFilas() { return new ArrayList<>(filas); }
    public List<String[]> getTotales() { return new ArrayList<>(totales); }
    public int getRegistrosEncontrados() { return filas.size(); }
}
