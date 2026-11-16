package com.example.cinemauq.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Formato de salida de un reporte (taller de integración, parte C).
 * Para agregar otro formato, por ejemplo PDF, se crea otra clase que implemente
 * esta interfaz; los reportes y la vista no cambian.
 */
public interface ExportadorReporte {
    /** Extensión del archivo, sin punto. */
    String getExtension();

    /** Texto completo del reporte, tal como queda en el archivo. */
    String formatear(Reporte reporte);

    /** Escribe el reporte en el archivo indicado. */
    void exportar(Reporte reporte, Path archivo) throws IOException;
}
