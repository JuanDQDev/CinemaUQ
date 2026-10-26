package com.example.cinemauq.controller;

import com.example.cinemauq.model.Funcion;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatos de presentación (moneda y fechas) usados por las vistas. */
public final class Formato {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA_FUNCION =
            DateTimeFormatter.ofPattern("EEE d 'de' MMM · h:mm a", COLOMBIA);

    private Formato() {
    }

    public static String moneda(double valor) {
        long redondeado = Math.round(valor);
        String texto = String.format(COLOMBIA, "$%,d", Math.abs(redondeado));
        return redondeado < 0 ? "− " + texto : texto;
    }

    /** Monto con signo, para los movimientos de la tarjeta. */
    public static String montoConSigno(int valor) {
        return (valor > 0 ? "+ " : valor < 0 ? "− " : "") + moneda(Math.abs(valor));
    }

    public static String fechaFuncion(Funcion funcion) {
        return funcion.getFechaHoraFuncion().format(FECHA_FUNCION);
    }

    /** Ejemplo: "sáb 27 de oct · 6:00 p. m. · Sala 3 (IMAX 3D) · Doblada". */
    public static String funcion(Funcion funcion) {
        return fechaFuncion(funcion)
                + " · Sala " + funcion.getSalaFuncion().getNumero()
                + " (" + funcion.getSalaFuncion().getFormato() + ")"
                + " · " + funcion.getIdioma();
    }
}
