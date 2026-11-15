package com.example.cinemauq.controller;

import com.example.cinemauq.service.ServicioReportes;
import com.example.cinemauq.service.ServicioReportes.VentaPelicula;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;

public class ReportesAdminController extends FormularioAdmin {
    @FXML private DatePicker desde;
    @FXML private DatePicker hasta;
    @FXML private TextArea resultado;
    @FXML private BarChart<String, Number> grafica;

    private final ServicioReportes reportes = new ServicioReportes();

    @Override
    protected void cargarCampos() {
        desde.setValue(LocalDate.now().minusDays(30));
        hasta.setValue(LocalDate.now().plusDays(7));
        generar();
    }

    @FXML
    private void consultar() {
        ejecutar(this::generar);
    }

    private void generar() {
        LocalDate inicio = desde.getValue();
        LocalDate fin = hasta.getValue();
        resultado.setText(reportes.generar(sistema, inicio, fin));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (VentaPelicula venta : reportes.ventasPorPelicula(sistema, inicio, fin)) {
            serie.getData().add(new XYChart.Data<>(venta.getTitulo(), venta.getTotal()));
        }
        grafica.getData().clear();
        grafica.getData().add(serie);
    }

    @FXML
    private void exportar() {
        ejecutar(() -> {
            generar();
            FileChooser selector = new FileChooser();
            selector.setTitle("Guardar reporte CinemaUQ");
            selector.setInitialFileName("reporte-cinemauq.txt");
            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
            File archivo = selector.showSaveDialog(resultado.getScene().getWindow());
            if (archivo == null) return;

            try {
                Files.writeString(archivo.toPath(), resultado.getText(), StandardCharsets.UTF_8);
            } catch (IOException error) {
                throw new IllegalStateException("No se pudo guardar el reporte", error);
            }
            Alertas.informacion("Reporte guardado", archivo.getName());
        });
    }
}
