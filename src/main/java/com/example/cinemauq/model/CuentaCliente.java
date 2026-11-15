package com.example.cinemauq.model;

public class CuentaCliente {
    private final Cliente cliente;
    private final String clave;

    public CuentaCliente(Cliente cliente, String clave) {
        if (cliente == null || clave == null || clave.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        this.cliente = cliente;
        this.clave = clave;
    }

    public boolean coincide(String identificacion, String clave) {
        return cliente.getId().equalsIgnoreCase(identificacion)
                && this.clave.equals(clave);
    }

    public Cliente getCliente() { return cliente; }
}
