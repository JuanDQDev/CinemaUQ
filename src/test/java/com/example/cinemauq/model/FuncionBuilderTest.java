package com.example.cinemauq.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del patron Builder (RN-001: la funcion debe llevar pelicula, sala, fecha y hora).
 */
class FuncionBuilderTest {

    private Funcion.Builder builderCompleto() {
        return new Funcion.Builder()
                .conIdFuncion("F-001")
                .conFechaHora(LocalDateTime.of(2026, 10, 9, 19, 0))
                .conIdioma("Español")
                .conSala(new Sala(1, "2D", 8, 12))
                .conPelicula(new Pelicula());
    }

    @Test
    void construyeUnaFuncionConTodosLosDatos() {
        Funcion funcion = builderCompleto().build();

        assertEquals("F-001", funcion.getIdFuncion());
        assertEquals(LocalDateTime.of(2026, 10, 9, 19, 0), funcion.getFechaHoraFuncion());
        assertEquals("Español", funcion.getIdioma());
        assertNotNull(funcion.getSalaFuncion());
        assertNotNull(funcion.getPeliculaFuncion());
    }

    @Test
    void cp01CrearFuncionSinSalaSeRechaza() {
        Funcion.Builder sinSala = builderCompleto().conSala(null);

        assertThrows(IllegalStateException.class, sinSala::build);
    }

    @Test
    void crearFuncionSinPeliculaSeRechaza() {
        Funcion.Builder sinPelicula = builderCompleto().conPelicula(null);

        assertThrows(IllegalStateException.class, sinPelicula::build);
    }

    @Test
    void crearFuncionSinFechaSeRechaza() {
        Funcion.Builder sinFecha = builderCompleto().conFechaHora(null);

        assertThrows(IllegalStateException.class, sinFecha::build);
    }
}
