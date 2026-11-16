package com.example.cinemauq.model;

import java.time.LocalDate;

public class Promocion {
    private final String codigo;
    private String nombre;
    private int porcentaje;
    private LocalDate desde;
    private LocalDate hasta;
    private boolean activa;

    public Promocion(String codigo, String nombre, int porcentaje,
                     LocalDate desde, LocalDate hasta, boolean activa) {
        this.codigo = codigo;
        actualizar(nombre, porcentaje, desde, hasta, activa);
    }

    public void actualizar(String nombre, int porcentaje, LocalDate desde, LocalDate hasta, boolean activa) {
        if(nombre == null || nombre.isBlank() || porcentaje < 1 || porcentaje > 100
                || desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException("Revisa el nombre, el porcentaje (1 a 100) y las fechas de la promoción");
        }
        this.nombre = nombre.trim();
        this.porcentaje = porcentaje;
        this.desde = desde;
        this.hasta = hasta;
        this.activa = activa;
    }

    public boolean vigente(LocalDate fecha) { return activa && !fecha.isBefore(desde) && !fecha.isAfter(hasta); }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getPorcentaje() { return porcentaje; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public boolean isActiva() { return activa; }

    @Override
    public String toString() {
        return nombre + " - " + porcentaje + " % - " + (activa ? "Activa" : "Inactiva");
    }
}
