package com.example.cinemauq.service;

import com.example.cinemauq.exception.AsientoOcupadoException;
import com.example.cinemauq.exception.SaldoInsuficienteException;
import com.example.cinemauq.exception.TarjetaInactivaException;
import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.ElementoProducto;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.MovimientoTarjeta;
import com.example.cinemauq.model.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del pago con tarjeta virtual a través de la fachada:
 * verificación de saldo, descuento, registro del movimiento y excepciones del dominio.
 * Son las mismas operaciones que ejecuta la vista de pago.
 */
class CompraTarjetaVirtualTest {

    private final CinemaFacade fachada = CinemaFacade.crearDemostracion();

    private Funcion primeraFuncion() {
        return fachada.getFunciones(fachada.getCartelera().get(0)).get(0);
    }

    @Test
    void compraDescuentaSaldoYRegistraMovimiento() {
        int saldoInicial = fachada.getTarjeta().getSaldo();
        Funcion funcion = primeraFuncion();
        List<String> asientos = Arrays.asList("D4", "D5");
        List<ElementoProducto> productos = new ArrayList<>();
        productos.add(fachada.getConfiteria().get(0));

        int total = fachada.calcularTotal(funcion, asientos, productos);
        Compra compra = fachada.confirmarCompra(funcion, asientos, productos);

        assertEquals(total, compra.getTotal());
        assertEquals(saldoInicial - total, fachada.getTarjeta().getSaldo());
        List<MovimientoTarjeta> movimientos = fachada.getTarjeta().getMovimientos();
        MovimientoTarjeta ultimo = movimientos.get(movimientos.size() - 1);
        assertEquals(TipoMovimiento.COMPRA, ultimo.getTipo());
        assertEquals(-total, ultimo.getMonto());
        assertEquals(compra.getFactura(), ultimo.getDescripcion());
        assertTrue(funcion.getSalaFuncion().buscarAsiento("D4").isOcupado());
    }

    @Test
    void saldoInsuficienteRechazaLaCompraSinCambiarElSaldo() {
        Funcion funcion = primeraFuncion();
        List<String> asientos = new ArrayList<>();
        for (char fila = 'A'; fila <= 'C'; fila++) {
            for (int numero = 1; numero <= 8; numero++) {
                asientos.add(String.valueOf(fila) + numero);
            }
        }
        int saldoInicial = fachada.getTarjeta().getSaldo();

        assertThrows(SaldoInsuficienteException.class,
                () -> fachada.confirmarCompra(funcion, asientos, new ArrayList<>()));
        assertEquals(saldoInicial, fachada.getTarjeta().getSaldo());
        assertFalse(funcion.getSalaFuncion().buscarAsiento("A1").isOcupado());
    }

    @Test
    void tarjetaInactivaNoPermitePagar() {
        fachada.getTarjeta().setActiva(false);

        assertThrows(TarjetaInactivaException.class,
                () -> fachada.confirmarCompra(primeraFuncion(), Arrays.asList("A1"), new ArrayList<>()));
    }

    @Test
    void asientoYaVendidoNoSePuedeComprarOtraVez() {
        Funcion funcion = primeraFuncion();
        fachada.confirmarCompra(funcion, Arrays.asList("E4"), new ArrayList<>());

        assertThrows(AsientoOcupadoException.class,
                () -> fachada.confirmarCompra(funcion, Arrays.asList("E4"), new ArrayList<>()));
    }

    @Test
    void recargaAutorizadaSumaSaldoYQuedaRegistrada() {
        int saldoInicial = fachada.getTarjeta().getSaldo();

        fachada.recargarTarjeta(50000);

        assertEquals(saldoInicial + 50000, fachada.getTarjeta().getSaldo());
        List<MovimientoTarjeta> movimientos = fachada.getTarjeta().getMovimientos();
        assertEquals(TipoMovimiento.RECARGA, movimientos.get(movimientos.size() - 1).getTipo());
    }
}
