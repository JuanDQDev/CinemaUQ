package com.example.cinemauq.service;

import com.example.cinemauq.exception.AsientoOcupadoException;
import com.example.cinemauq.exception.CompraInvalidaException;
import com.example.cinemauq.exception.EdadNoPermitidaException;
import com.example.cinemauq.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioCompra {
    private final Tarifario tarifas;
    private final List<Promocion> promociones;

    public ServicioCompra() { this(new Tarifario(), new ArrayList<>()); }

    public ServicioCompra(Tarifario tarifas, List<Promocion> promociones) {
        this.tarifas = tarifas;
        this.promociones = promociones;
    }

    public Promocion promocionActual() {
        Promocion mejor = null;
        for (Promocion promocion : promociones) {
            if (promocion.vigente(java.time.LocalDate.now())
                    && (mejor == null || promocion.getPorcentaje() > mejor.getPorcentaje())) {
                mejor = promocion;
            }
        }
        return mejor;
    }


    // se implemento Decorator para agregar recargos según el formato de la sala.
    public Entrada crearEntrada(Funcion funcion) {
        Entrada entrada = new EntradaBase(tarifas.getEntradaBase());
        String formato = funcion.getSalaFuncion().getFormato().toUpperCase();
        if (formato.contains("3D")) {
            entrada = new LenteTresDDecorator(entrada, tarifas.getRecargo3D());
        }
        if (formato.contains("IMAX")) {
            entrada = new PantallaIMAXDecorator(entrada, tarifas.getRecargoIMAX());
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

        long precioEntradas = Math.round(crearEntrada(funcion).calcularPrecioEntrada()) * asientos.size();
        long total = precioEntradas;
        for (ElementoProducto producto : productos) {
            if (producto == null) {
                throw new CompraInvalidaException("Producto inválido");
            }
            total = total + (int) Math.round(producto.calcularPrecio());
        }
        if (total > Integer.MAX_VALUE || total < 0) {
            throw new CompraInvalidaException("El total de la compra es demasiado alto");
        }
        Promocion promocion = promocionActual();
        if (promocion != null) total = Math.round(total * (1 - promocion.getPorcentaje() / 100.0));
        return (int) total;
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<String> asientos,
                          List<ElementoProducto> productos, MedioPago medioPago) {
        if (cliente == null || funcion == null || medioPago == null) {
            throw new CompraInvalidaException("Cliente, función y pago son obligatorios");
        }

        if (funcion.isCancelada()) throw new CompraInvalidaException("La función fue cancelada por el cine");

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
        // Preparar el historial antes del cobro, por si un artículo es inválido.
        Compra compra = new Compra(factura, cliente, funcion, seleccionados, extras, total);
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
        //Acumular puntos
        cliente.getMontoPuntos().acumularPuntos(total, factura);
        return compra;
    }

    public double procesarReembolso(Compra compra, LocalDateTime horaActual) {
        if (compra == null || horaActual == null) {
            throw new CompraInvalidaException(
                    "Se requieren la compra y la fecha actual");
        }

        // State comprueba el estado de la compra y el plazo permitido.
        compra.validarCancelacion(horaActual);

        // Strategy determina el porcentaje del reembolso.
        LocalDateTime fechaFuncion = compra.getFuncion().getFechaHoraFuncion();
        IPoliticaReembolso politica;
        if (fechaFuncion.isAfter(horaActual.plusHours(24))) {
            politica = new ReembolsoCien();
        } else {
            politica = new ReembolsoOchenta();
        }

        return politica.calcularReembolso(compra.getTotal());
    }

    public int cancelarCompra(Compra compra) {
        return cancelarCompra(compra, LocalDateTime.now());
    }

    public int cancelarCompra(Compra compra, LocalDateTime hora) {
        int monto = (int) Math.round(procesarReembolso(compra, hora));
        return ejecutarCancelacion(compra, monto, hora, false);
    }

    public int cancelarPorCine(Compra compra) {
        if (compra == null || compra.estaCancelada()) {
            throw new CompraInvalidaException("La compra no existe o ya está cancelada");
        }
        return ejecutarCancelacion(compra, compra.getTotal(), LocalDateTime.now(), true);
    }

    private int ejecutarCancelacion(Compra compra, int monto, LocalDateTime hora, boolean porCine) {
        List<Asiento> asientos = new ArrayList<>();
        for (String codigo : compra.getAsientos()) {
            asientos.add(compra.getFuncion().getSalaFuncion().buscarAsiento(codigo));
        }
        if (monto > 0) compra.getCliente().getTarjeta().reembolsar(monto, compra.getFactura());
        for (Asiento asiento : asientos) asiento.liberar();
        compra.registrarCancelacion(monto, hora, porCine);
        compra.getCliente().getMontoPuntos().anularPorCompra(compra.getFactura());
        return monto;
    }
}
