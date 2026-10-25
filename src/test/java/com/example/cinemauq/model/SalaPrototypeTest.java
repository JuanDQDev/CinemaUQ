package com.example.cinemauq.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patron Prototype aplicado a Sala.
 */
class SalaPrototypeTest {

    @Test
    void elClonEsUnaCopiaProfunda() {
        Sala plantilla = new Sala(1, "2D", 8, 12);
        Sala copia = plantilla.clonar();

        assertNotSame(plantilla, copia);
        assertEquals(plantilla.getCapacidad(), copia.getCapacidad());
        for (int i = 0; i < plantilla.getCapacidad(); i++) {
            Asiento original = plantilla.getAsientos().get(i);
            Asiento clonado = copia.getAsientos().get(i);
            assertNotSame(original, clonado, "La copia no debe compartir objetos Asiento");
            assertEquals(original.getCodigo(), clonado.getCodigo());
        }
    }

    @Test
    void ocuparEnUnaFuncionNoAfectaOtraFuncionDeLaMismaSala() {
        Sala plantilla = new Sala(1, "2D", 8, 12);
        Sala funcion19 = plantilla.clonar();
        Sala funcion21 = plantilla.clonar();

        funcion19.ocuparAsiento("D4");

        assertTrue(funcion19.buscarAsiento("D4").isOcupado());
        assertFalse(funcion21.buscarAsiento("D4").isOcupado());
        assertFalse(plantilla.buscarAsiento("D4").isOcupado());
    }

    @Test
    void ocuparDosVecesElMismoAsientoLanzaError() {
        Sala sala = new Sala(1, "2D", 8, 12).clonar();
        sala.ocuparAsiento("D4");

        assertThrows(IllegalStateException.class, () -> sala.ocuparAsiento("D4"));
    }

    @Test
    void capacidadYDisponibles() {
        Sala sala = new Sala(1, "2D", 8, 12).clonar();
        sala.ocuparAsiento("A1");
        sala.ocuparAsiento("H12");

        assertEquals(96, sala.getCapacidad());
        assertEquals(94, sala.contarDisponibles());
    }

    @Test
    void cadaFuncionRecibeSuPropiaCopiaDeLaSala() {
        Sala plantilla = new Sala(1, "2D", 8, 12);
        Funcion funcion = new Funcion.Builder()
                .conIdFuncion("F-001")
                .conFechaHora(LocalDateTime.of(2026, 10, 9, 19, 0))
                .conIdioma("Español")
                .conSala(plantilla.clonar())
                .conPelicula(new Pelicula())
                .build();

        funcion.getSalaFuncion().ocuparAsiento("D4");

        assertTrue(funcion.getSalaFuncion().buscarAsiento("D4").isOcupado());
        assertFalse(plantilla.buscarAsiento("D4").isOcupado());
    }
}
