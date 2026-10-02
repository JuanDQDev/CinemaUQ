package com.example.cinemauq.model;

public class ConsecutivoFactura {

    private static ConsecutivoFactura instancia;
    private int id;

    private ConsecutivoFactura() {
        id = 1;
    }

    public static ConsecutivoFactura getInstance() {
        if (instancia == null) {
            instancia = new ConsecutivoFactura();
        }

        return instancia;
    }

    public String generarConsecutivo() {
        String consecutivo = String.format("FAC-%06d", id);
        id++;
        return consecutivo;
    }
}