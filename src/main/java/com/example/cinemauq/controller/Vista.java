package com.example.cinemauq.controller;

/**
 * Pantallas de la aplicación. El número de paso indica la posición dentro
 * del flujo de compra (0 = pantalla fuera del flujo).
 */
public enum Vista {
    CARTELERA("cartelera-view.fxml", 1),
    ASIENTOS("asientos-view.fxml", 2),
    CONFITERIA("confiteria-view.fxml", 3),
    PAGO("pago-view.fxml", 4),
    TARJETA("tarjeta-view.fxml", 0),
    COMPRAS("compras-view.fxml", 0);

    private final String archivo;
    private final int paso;

    Vista(String archivo, int paso) {
        this.archivo = archivo;
        this.paso = paso;
    }

    public String getArchivo() { return archivo; }
    public int getPaso() { return paso; }
}
