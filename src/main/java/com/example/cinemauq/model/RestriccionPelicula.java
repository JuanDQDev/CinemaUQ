package com.example.cinemauq.model;

public enum RestriccionPelicula {
    SIETE(7),
    DOCE(12),
    QUINCE(15),
    DIEZ_Y_OCHO(18)

    private final int edadMinima;
    RestriccionPelicula(int edadMinima) { this.edadMinima = edadMinima; }
    public int getEdadMinima() { return edadMinima; }
}
