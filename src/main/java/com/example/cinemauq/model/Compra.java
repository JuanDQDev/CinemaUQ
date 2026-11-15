package com.example.cinemauq.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Compra { // La usaremos como Context de state para guardar los estados actuales y delegar las operaciones

    private final String factura;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<String> asientos;
    private final List<ElementoProducto> confiteria;
    private final int total;
    private final String fecha;
    private EstadoCompra estado;
    private final LocalDateTime fechaCompra = LocalDateTime.now();
    private LocalDateTime fechaCancelacion;
    private int reembolso;
    private String motivoCancelacion = "";
    private final String nombrePelicula;
    private final String detalleFuncion;

    public Compra(String factura, Cliente cliente, Funcion funcion, List<String> asientos,
                  List<ElementoProducto> confiteria, int total) {
        this.factura = factura;
        this.cliente = cliente;
        this.funcion = funcion;
        this.asientos = new ArrayList<>(asientos);
        this.confiteria = new ArrayList<>();
        for (ElementoProducto elemento : confiteria) {this.confiteria.add(new DetalleConfiteria(elemento));}
        this.nombrePelicula = funcion.getPeliculaFuncion().getNombrePelicula();
        this.detalleFuncion = funcion.toString();
        this.total = total;
        this.fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        this.estado = new CompraConfirmada();
    }

    public void validarCancelacion(LocalDateTime horaActual) {
        estado.validarCancelacion(this, horaActual);
    }

    public void marcarCancelada(LocalDateTime horaActual) {
        estado = estado.cancelar(this, horaActual);
    }

    public void registrarCancelacion(int monto, LocalDateTime hora, boolean porCine) {
        if (monto < 0 || monto > total) {
            throw new IllegalArgumentException("Reembolso invalido");
        }
        if (porCine) {
            estado = estado.cancelar(this, hora);
        } else {
            estado = estado.cancelar(this, hora);
        }
        reembolso = monto;
        fechaCancelacion = hora;
        motivoCancelacion = porCine ? "Funcion cancelada por el cine" : "Cancelación solicitada por el cliente";
    }

    public boolean estaCancelada() { return estado.estaCancelada(); }
    public int getReembolso() { return reembolso; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public String getNombrePelicula() { return nombrePelicula; }
    public String getDetalleFuncion() { return detalleFuncion; }

    public String getEstado() { return estado.getNombre(); }
    public String getFactura() { return factura; }
    public Cliente getCliente() { return cliente; }
    public Funcion getFuncion() { return funcion; }
    public List<String> getAsientos() { return new ArrayList<>(asientos); }
    public List<ElementoProducto> getConfiteria() { return new ArrayList<>(confiteria); }
    public int getTotal() { return total; }
    public String getFecha() { return fecha; }

}