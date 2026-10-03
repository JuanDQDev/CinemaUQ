package com.example.cinemauq.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del patron Singleton (RN-014: no puede haber facturas con el mismo consecutivo).
 */
class ConsecutivoFacturaTest {

    @Test
    void siempreSeObtieneLaMismaInstancia() {
        ConsecutivoFactura primera = ConsecutivoFactura.getInstance();
        ConsecutivoFactura segunda = ConsecutivoFactura.getInstance();

        assertSame(primera, segunda);
    }

    @Test
    void cp11LosConsecutivosNoSeRepiten() {
        String primero = ConsecutivoFactura.getInstance().generarConsecutivo();
        String segundo = ConsecutivoFactura.getInstance().generarConsecutivo();

        assertNotEquals(primero, segundo);
        assertTrue(primero.startsWith("FAC-"));
        assertEquals(Integer.parseInt(primero.substring(4)) + 1, Integer.parseInt(segundo.substring(4)));
    }
}
