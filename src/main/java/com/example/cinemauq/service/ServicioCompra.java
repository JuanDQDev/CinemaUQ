package com.example.cinemauq.service;

import com.example.cinemauq.exception.AsientoOcupadoException;
import com.example.cinemauq.exception.CompraInvalidaException;
import com.example.cinemauq.exception.EdadNoPermitidaException;
import com.example.cinemauq.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioCompra {

    // se implemento Decorator para agregar recargos según el formato de la sala.
    public Entrada crearEntrada(Funcion funcion) {
        Entrada entrada = new EntradaBase(18000);
        String formato = funcion.getSalaFuncion().getFormato().toUpperCase();
        if (formato.contains("3D")) {
            entrada = new LenteTresDDecorator(entrada, 4000);
        }
        if (formato.contains("IMAX")) {
            entrada = new PantallaIMAXDecorator(entrada, 7000);
        }
        return entrada;
    }

    public int calcularTotal(Funcion funcion, List<String> asientos, List<ElementoProducto> productos) {
        if (funcion == null) {
            throw new CompraInvalidaException("Selecciona una función");
        }
        if (asientos == null || asientos.isEmpty()) {
            throw new CompraInvalidaException("Selecciona al menos un asiento");
        }
        if (productos == null) {
            throw new CompraInvalidaException("Carrito de productos inválido");
        }

        // Revisión de repetidos con una lista, sin HashSet ni HashMap.
        List<String> revisados = new ArrayList<>();
        for (String codigo : asientos) {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new CompraInvalidaException("Asiento inválido");
            }
            String asiento = codigo.trim().toUpperCase();
            if (revisados.contains(asiento)) {
                throw new CompraInvalidaException("Hay asientos repetidos");
            }
            revisados.add(asiento);
        }

        int precioEntradas = (int) Math.round(crearEntrada(funcion).calcularPrecioEntrada()) * asientos.size();
        int total = precioEntradas;
        for (ElementoProducto producto : productos) {
            if (producto == null) {
                throw new CompraInvalidaException("Producto inválido");
            }
            total = total + (int) Math.round(producto.calcularPrecio());
        }
        return total;
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<String> asientos,
                          List<ElementoProducto> productos, MedioPago medioPago) {
        if (cliente == null || funcion == null || medioPago == null) {
            throw new CompraInvalidaException("Cliente, función y pago son obligatorios");
        }

        // Regla 1: verificar la edad mínima de la película.
        RestriccionPelicula restriccion = funcion.getPeliculaFuncion().getRestriccionPelicula();
        if (restriccion != null && cliente.getEdad() < restriccion.getEdadMinima()) {
            throw new EdadNoPermitidaException("Edad mínima: " + restriccion.getEdadMinima() + " años");
        }

        // Regla 2: no vender entradas para funciones pasadas.
        if (!funcion.getFechaHoraFuncion().isAfter(LocalDateTime.now())) {
            throw new CompraInvalidaException("Esta función ya comenzó");
        }

        List<String> seleccionados = new ArrayList<>();
        if (asientos != null) {
            for (String codigo : asientos) {
                if (codigo == null) {
                    seleccionados.add(null);
                } else {
                    seleccionados.add(codigo.trim().toUpperCase());
                }
            }
        }
        List<ElementoProducto> extras = new ArrayList<>();
        if (productos != null) {
            extras.addAll(productos);
        }
        int total = calcularTotal(funcion, seleccionados, extras);

        // Regla 3: verificar todos los asientos antes de cobrar.
        Sala sala = funcion.getSalaFuncion();
        for (String codigo : seleccionados) {
            Asiento asiento;
            try {
                asiento = sala.buscarAsiento(codigo);
            } catch (IllegalArgumentException error) {
                throw new CompraInvalidaException(error.getMessage());
            }
            if (asiento.isOcupado()) {
                throw new AsientoOcupadoException("Asiento ocupado: " + codigo);
            }
        }

        // Regla 4: verificar saldo, cobrar y registrar el pago.
        medioPago.verificar(total);
        String factura = ConsecutivoFactura.getInstance().generarConsecutivo();
        medioPago.cobrar(total, factura);

        // Regla 5: marcar asientos ocupados solamente cuando el pago fue aprobado.
        List<String> ocupados = new ArrayList<>();
        try {
            for (String codigo : seleccionados) {
                sala.ocuparAsiento(codigo);
                ocupados.add(codigo);
            }
        } catch (RuntimeException error) {
            // Si algo falla después del cobro, liberar y devolver el dinero.
            for (String codigo : ocupados) {
                sala.buscarAsiento(codigo).liberar();
            }
            medioPago.devolver(total, factura);
            throw error;
        }
        return new Compra(factura, cliente, funcion, seleccionados, extras, total);
    }
}
