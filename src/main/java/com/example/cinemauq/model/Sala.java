package com.example.cinemauq.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sala del cine con su distribucion de asientos.
 *
 * Patron Prototype: la sala registrada por el administrador funciona como
 * plantilla. Cada funcion debe recibir sala.clonar(), una copia profunda con
 * los mismos asientos pero con su propia disponibilidad. Asi, vender el D4 en
 * la funcion de las 7:00 p. m. no lo ocupa en la funcion de las 9:00 p. m.
 */
public class Sala implements Clonable<Sala> {

    private final int numero;
    private final String formato;
    private final int filas;
    private final int columnas;
    private final List<Asiento> asientos;

    /**
     * Crea la sala y genera sus asientos: filas A, B, C... y columnas 1..n.
     */
    public Sala(int numero, String formato, int filas, int columnas) {
        if (filas <= 0 || filas > 26 || columnas <= 0) {
            throw new IllegalArgumentException("La sala debe tener entre 1 y 26 filas y al menos una columna");
        }
        this.numero = numero;
        this.formato = formato;
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            for (int c = 1; c <= columnas; c++) {
                asientos.add(new Asiento((char) ('A' + f), c));
            }
        }
    }

    /**
     * Constructor privado usado solo por clonar(): recibe la lista de asientos ya copiada.
     */
    private Sala(int numero, String formato, int filas, int columnas, List<Asiento> asientos) {
        this.numero = numero;
        this.formato = formato;
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = asientos;
    }

    /**
     * Copia profunda: crea una sala nueva y clona cada asiento.
     * Si solo se copiara la lista (copia superficial), la sala original y la copia
     * compartirian los mismos objetos Asiento y ocupar uno afectaria a ambas.
     */
    @Override
    public Sala clonar() {
        List<Asiento> copiaAsientos = new ArrayList<>();
        for (Asiento asiento : asientos) {
            copiaAsientos.add(asiento.clonar());
        }
        return new Sala(numero, formato, filas, columnas, copiaAsientos);
    }

    public Asiento buscarAsiento(String codigo) {
        for (Asiento asiento : asientos) {
            if (asiento.getCodigo().equalsIgnoreCase(codigo)) {
                return asiento;
            }
        }
        throw new IllegalArgumentException("El asiento " + codigo + " no existe en la sala " + numero);
    }

    public void ocuparAsiento(String codigo) {
        buscarAsiento(codigo).ocupar();
    }

    public int contarDisponibles() {
        int disponibles = 0;
        for (Asiento asiento : asientos) {
            if (!asiento.isOcupado()) {
                disponibles++;
            }
        }
        return disponibles;
    }

    public int getCapacidad() {
        return asientos.size();
    }

    public int getNumero() {
        return numero;
    }

    public String getFormato() {
        return formato;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    /** Lista de solo lectura: los asientos se ocupan con ocuparAsiento(), no modificando la lista. */
    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }
}
