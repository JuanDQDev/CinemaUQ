package com.example.cinemauq.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObservadorSaldoTest {
    @Test
    void tarjetaAvisaDespuesDeRegistrarElMovimientoYDejaDeAvisarAlRetirarse() {
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-Prueba");
        List<Integer> saldos = new ArrayList<>();
        List<Integer> movimientosRegistrados = new ArrayList<>();
        ObservadorSaldo pantalla = saldo -> {
            saldos.add(saldo);
            movimientosRegistrados.add(tarjeta.getMovimientos().size());
        };

        tarjeta.agregarObservador(pantalla);
        tarjeta.agregarObservador(pantalla);
        tarjeta.otorgarBonoBienvenida();
        tarjeta.descontar(11000, "F-01");
        tarjeta.reembolsar(8800, "F-01");
        assertThrows(IllegalArgumentException.class, () -> tarjeta.reembolsar(8800, "F-01"));

        tarjeta.quitarObservador(pantalla);
        Administrador admin = new Administrador("A01", "Admin", "admin@cine.test", "Clave123");
        tarjeta.recargar(admin, 10000);

        assertEquals(List.of(50000, 39000, 47800), saldos);
        assertEquals(List.of(1, 2, 3), movimientosRegistrados);
        assertEquals(57800, tarjeta.getSaldo());
    }
}
