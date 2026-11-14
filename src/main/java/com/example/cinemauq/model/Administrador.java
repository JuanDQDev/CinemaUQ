package com.example.cinemauq.model;

public class Administrador {
    private String nombre;
    private String identificacion;
    private String correo;
    private String clave;

    public Administrador(String identificacion, String nombre, String correo, String clave) {
        if (identificacion == null || identificacion.isBlank() || nombre == null || nombre.isBlank() || clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("El administrador requiere identificación y nombre");
        }
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
    }

    public boolean validarCredenciales(String identificacion, String clave) {
        return this.identificacion.equals(identificacion)
                && this.clave.equals(clave);
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }

}
