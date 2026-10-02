package com.example.cinemauq.model;

public class IdentificadorFactura {

    private static IdentificadorFactura instancia;
    private int id;

    private IdentificadorFactura() {
        id = 1;
    }

    public static IdentificadorFactura getInstance() {
        if (instancia == null) {
            instancia = new IdentificadorFactura();
        }

        return instancia;
    }

    public String generarIdentificador() {
        String consecutivo = String.format("FAC-%06d", id);
        id++;
        return consecutivo;
    }
}