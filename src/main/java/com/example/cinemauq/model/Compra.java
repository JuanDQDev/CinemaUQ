package com.example.cinemauq.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    public Compra(String factura, Cliente cliente, Funcion funcion, List<String> asientos,
                  List<ElementoProducto> confiteria, int total) {
        this.factura = factura;
        this.cliente = cliente;
        this.funcion = funcion;
        this.asientos = new ArrayList<>(asientos);
        this.confiteria = new ArrayList<>(confiteria);
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

    public String getEstado() { return estado.getNombre(); }
    public String getFactura() { return factura; }
    public Cliente getCliente() { return cliente; }
    public Funcion getFuncion() { return funcion; }
    public List<String> getAsientos() { return asientos; }
    public List<ElementoProducto> getConfiteria() { return confiteria; }
    public int getTotal() { return total; }
    public String getFecha() { return fecha; }

}