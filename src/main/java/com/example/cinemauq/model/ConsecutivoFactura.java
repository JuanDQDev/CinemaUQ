package com.example.cinemauq.model;

public class ConsecutivoFactura {

    private static final ConsecutivoFactura instancia = new ConsecutivoFactura();
    private int id;

    private ConsecutivoFactura() {
        id = 1;
    }

    public static ConsecutivoFactura getInstance() {
        return instancia;
    }

    public String generarConsecutivo() {
        String consecutivo = String.format("FAC-%06d", id);
        id++;
        return consecutivo;
    }
}