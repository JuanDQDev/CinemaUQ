package com.example.cinemauq.model;

public class Cliente {
    private String id;
    private String nombre;
    private int edad;

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
