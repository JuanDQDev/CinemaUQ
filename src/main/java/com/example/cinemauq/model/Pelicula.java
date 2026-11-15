package com.example.cinemauq.model;

public class Pelicula {
    private String codigo;
    private String nombrePelicula;
    private String descripcion;
    private RestriccionPelicula restriccionPelicula;

    public Pelicula(String codigo, String nombrePelicula, String descripcion, RestriccionPelicula restriccionPelicula) {
        if (codigo == null || codigo.isBlank() || nombrePelicula == null || nombrePelicula.isBlank()) {
            throw new IllegalArgumentException("La película necesita un codigo y nombre");
        }
        this.codigo = codigo;
        this.nombrePelicula = nombrePelicula;
        this.descripcion = descripcion;
        this.restriccionPelicula = restriccionPelicula;
    }

    public void actualizar(String nombre, String descripcion, RestriccionPelicula restriccion) {
        if(nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Debes escribir el titulo y la descripcion");
        }
        this.nombrePelicula = nombre.trim();
        this.descripcion = descripcion.trim();
        this.restriccionPelicula = restriccion;
    }

    public String getCodigo() { return codigo; }
    public String getNombrePelicula() { return nombrePelicula; }
    public String getDescripcion() { return descripcion; }
    public RestriccionPelicula getRestriccionPelicula() { return restriccionPelicula; }

    @Override
    public String toString() {
        return nombrePelicula;
    }

}
