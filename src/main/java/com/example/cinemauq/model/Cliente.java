package com.example.cinemauq.model;

public class Cliente {
    private final String id;
    private final String nombre;
    private final int edad;
    private final TarjetaVirtual tarjeta;

    public Cliente(String id, String nombre, int edad) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank() || edad < 0) {
            throw new IllegalArgumentException("Datos de cliente inválidos");
        }
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.tarjeta = new TarjetaVirtual("TV-" + id);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public TarjetaVirtual getTarjeta() { return tarjeta; }

}
