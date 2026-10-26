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

    public String getCodigo() { return codigo; }
    public String getNombrePelicula() { return nombrePelicula; }
    public String getDescripcion() { return descripcion; }
    public RestriccionPelicula getRestriccionPelicula() { return restriccionPelicula; }

    @Override
    public String toString() {
        return nombrePelicula;
    }

}
