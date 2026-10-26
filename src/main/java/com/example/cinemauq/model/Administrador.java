package com.example.cinemauq.model;

public class Administrador {
    private String nombre;
    private String identificacion;
    private String correo;

    public Administrador(String identificacion, String nombre, String correo) {
        if (identificacion == null || identificacion.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El administrador requiere identificación y nombre");
        }
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }

}
