package com.example.cinemauq.model;

public class SolicitudRecarga {
    private final Cliente cliente;
    private final int monto;
    private String estado = "Pendiente";

    public SolicitudRecarga(Cliente cliente, int monto) {
        if (cliente == null || monto <= 0) {
            throw new IllegalArgumentException("Solicitud invalida");
        }
        this.cliente = cliente;
        this.monto = monto;
    }

    public void resolver(boolean aprobada) {
        if(!isPendiente()) {
            throw new IllegalArgumentException("Solicitud ya atendida");
        }
        estado = aprobada ? "Aprobada" : "Rechazada";
    }

    public Cliente getCliente() { return cliente; }
    public int getMonto() { return monto; }
    public String getEstado() { return estado; }
    public boolean isPendiente() { return "Pendiente".equals(estado); }

    @Override
    public String toString() {
        return cliente.getId() + " - " + cliente.getNombre() + " - $" + monto + " - " + estado;
    }
}
