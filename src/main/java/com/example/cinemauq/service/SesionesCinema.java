package com.example.cinemauq.service;

import com.example.cinemauq.model.Administrador;
import com.example.cinemauq.model.Cliente;
import com.example.cinemauq.model.CuentaCliente;
import java.util.ArrayList;
import java.util.List;

/** Registro de clientes, credenciales y control de la sesión actual. */
final class SesionesCinema {
    private final List<CuentaCliente> cuentas = new ArrayList<>();
    private final Administrador administrador =
            new Administrador("A01", "Administración CinemaUQ", "admin@cinemauq.demo", "Admin123");
    private Cliente clienteActual;
    private boolean adminActivo;
    private long versionSesion;

    Cliente registrar(String id, String nombre, int edad, String clave) {
        if (id == null || !id.trim().matches("[A-Za-z0-9_-]{3,30}")
                || nombre == null || nombre.trim().length() < 2 || edad < 1 || edad > 120) {
            throw new IllegalArgumentException("Revisa identificación (3 a 30 letras o números), nombre y edad (1 a 120)");
        }
        id = id.trim();
        if (id.equalsIgnoreCase("A01") || id.equalsIgnoreCase("admin")) {
            throw new IllegalArgumentException("Esa identificación está reservada");
        }
        for (CuentaCliente cuenta : cuentas) {
            if (cuenta.getCliente().getId().equalsIgnoreCase(id)) {
                throw new IllegalArgumentException("Ya existe una cuenta con esa identificación");
            }
        }
        Cliente cliente = new Cliente(id, nombre.trim(), edad);
        CuentaCliente cuenta = new CuentaCliente(cliente, clave);
        cliente.getTarjeta().otorgarBonoBienvenida();
        cuentas.add(cuenta);
        return cliente;
    }

    void iniciarSesion(String id, String clave) {
        cerrarSesion();
        if (id == null || clave == null) throw new IllegalArgumentException("Escribe identificación y contraseña");
        id = id.trim();
        if (id.equalsIgnoreCase("admin")) id = "A01";
        if (administrador.validarCredenciales(id.toUpperCase(), clave)) {
            adminActivo = true;
            return;
        }
        for (CuentaCliente cuenta : cuentas) {
            if (cuenta.coincide(id, clave)) {
                clienteActual = cuenta.getCliente();
                return;
            }
        }
        throw new IllegalArgumentException("Identificación o contraseña incorrectas");
    }

    void cerrarSesion() {
        clienteActual = null;
        adminActivo = false;
        versionSesion++;
    }

    boolean esAdministrador() { return adminActivo; }
    boolean hayCliente() { return clienteActual != null; }
    long getVersionSesion() { return versionSesion; }

    void exigirAdministrador() {
        if (!adminActivo) throw new SecurityException("Esta operación requiere iniciar sesión como administrador");
    }

    Cliente exigirCliente(long version) {
        if (clienteActual == null || adminActivo || version != versionSesion) {
            throw new SecurityException("La sesión terminó. Vuelve a iniciar sesión");
        }
        return clienteActual;
    }

    void exigirSesion() {
        if (!adminActivo && clienteActual == null) throw new SecurityException("Debes iniciar sesión");
    }

    Cliente getClienteActual() {
        return exigirCliente(versionSesion);
    }

    List<Cliente> getClientes() {
        exigirAdministrador();
        List<Cliente> lista = new ArrayList<>();
        for (CuentaCliente cuenta : cuentas) lista.add(cuenta.getCliente());
        return lista;
    }

    Administrador administradorActual() {
        exigirAdministrador();
        return administrador;
    }
}
