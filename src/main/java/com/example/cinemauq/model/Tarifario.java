package com.example.cinemauq.model;

public class Tarifario {
    private int entradaBase = 18000;
    private int recargo3D = 4000;
    private int recargoIMAX = 7000;

    public void actualizar(int base, int tresD, int imax) {
        if (base <= 0 || tresD < 0 || imax < 0
                || base > 1000000 || tresD > 1000000 || imax > 1000000) {
            throw new IllegalArgumentException("La tarifa base debe ser positiva y los recargos no negativos; máximo $1.000.000");
        }
        entradaBase = base;
        recargo3D = tresD;
        recargoIMAX = imax;
    }

    public int getEntradaBase() { return entradaBase; }
    public int getRecargo3D() { return recargo3D; }
    public int getRecargoIMAX() { return recargoIMAX; }
}
