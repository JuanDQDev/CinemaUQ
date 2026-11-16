package com.example.cinemauq.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Reporte en texto plano con columnas alineadas. No necesita librerías adicionales. */
public class ExportadorReporteTxt implements ExportadorReporte {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String getExtension() { return "txt"; }

    @Override
    public String formatear(Reporte reporte) {
        if (reporte == null) throw new IllegalArgumentException("No hay reporte para exportar");
        List<String> encabezados = reporte.getEncabezados();
        List<List<String>> filas = reporte.getFilas();
        int[] anchos = new int[encabezados.size()];
        for (int i = 0; i < anchos.length; i++) anchos[i] = encabezados.get(i).length();
        for (List<String> fila : filas) {
            for (int i = 0; i < anchos.length; i++) anchos[i] = Math.max(anchos[i], fila.get(i).length());
        }

        StringBuilder texto = new StringBuilder();
        texto.append("CINEMAUQ\n").append(reporte.getTitulo().toUpperCase()).append("\n")
                .append("Fecha de generación: ").append(reporte.getFechaGeneracion().format(FECHA_HORA)).append("\n")
                .append("Periodo consultado: ").append(reporte.getDesde().format(FECHA)).append(" a ")
                .append(reporte.getHasta().format(FECHA)).append("\n")
                .append("Registros encontrados: ").append(reporte.getRegistrosEncontrados()).append("\n\n");

        String linea = linea(encabezados, anchos);
        texto.append(linea).append("\n").append("-".repeat(linea.length())).append("\n");
        if (filas.isEmpty()) texto.append("(sin registros en el periodo)\n");
        for (List<String> fila : filas) texto.append(linea(fila, anchos)).append("\n");

        texto.append("\nTOTALES\n");
        for (String[] total : reporte.getTotales()) {
            texto.append(total[0]).append(": ").append(total[1]).append("\n");
        }
        return texto.toString();
    }

    @Override
    public void exportar(Reporte reporte, Path archivo) throws IOException {
        if (archivo == null) throw new IllegalArgumentException("Selecciona dónde guardar el reporte");
        Files.writeString(archivo, formatear(reporte), StandardCharsets.UTF_8);
    }

    private String linea(List<String> valores, int[] anchos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) linea.append(" | ");
            String valor = valores.get(i);
            linea.append(valor).append(" ".repeat(anchos[i] - valor.length()));
        }
        return linea.toString().stripTrailing();
    }
}
