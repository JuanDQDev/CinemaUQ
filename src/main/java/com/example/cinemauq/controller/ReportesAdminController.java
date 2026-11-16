package com.example.cinemauq.controller;

import com.example.cinemauq.model.ResumenEstadistico;
import com.example.cinemauq.service.ExportadorReporte;
import com.example.cinemauq.service.ExportadorReporteTxt;
import com.example.cinemauq.service.Reporte;
import com.example.cinemauq.service.ServicioReportes;
import com.example.cinemauq.service.TipoReporte;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Reportes y estadísticas del administrador.
 *
 * El controlador no calcula: pide el reporte a ServicioReportes, lo muestra con el
 * mismo formato del archivo (ExportadorReporte) y entrega a cada gráfico un Map
 * que ya viene calculado por EstadisticasCine (taller de integración, parte C).
 */
public class ReportesAdminController extends FormularioAdmin {
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");

    @FXML private ComboBox<TipoReporte> tipo;
    @FXML private DatePicker desde;
    @FXML private DatePicker hasta;
    @FXML private Label indicadores;
    @FXML private TextArea resultado;
    @FXML private BarChart<String, Number> grafica;
    @FXML private PieChart graficaIngresos;
    @FXML private LineChart<String, Number> graficaVentas;

    private final ServicioReportes reportes = new ServicioReportes();
    private final ExportadorReporte exportador = new ExportadorReporteTxt();
    private Reporte reporteActual;

    @Override
    protected void cargarCampos() {
        tipo.getItems().setAll(TipoReporte.values());
        tipo.setValue(TipoReporte.RESUMEN);
        desde.setValue(LocalDate.now().minusDays(30));
        hasta.setValue(LocalDate.now());
        generar();
    }

    @FXML
    private void consultar() {
        if (sistema == null || tipo.getValue() == null) return; // la vista aún se está cargando
        ejecutar(this::generar);
    }

    private void generar() {
        LocalDate inicio = desde.getValue();
        LocalDate fin = hasta.getValue();
        reporteActual = reportes.crear(sistema, tipo.getValue(), inicio, fin);
        resultado.setText(exportador.formatear(reporteActual));

        ResumenEstadistico datos = reportes.estadisticas(sistema, inicio, fin);
        indicadores.setText("Compras: " + datos.getCompras()
                + "   ·   Entradas: " + datos.getEntradasVendidas()
                + "   ·   Ingresos brutos: " + Formato.moneda(datos.getIngresosBrutos())
                + "   ·   Reembolsos: " + Formato.moneda(datos.getReembolsos())
                + "   ·   Ocupación: " + Math.round(datos.getOcupacionPromedio()) + " %");
        dibujarBarras(datos.getEntradasPorPelicula());
        dibujarPastel(datos.getIngresosPorPelicula());
        dibujarLineas(datos.getVentasPorDia());
    }

    /** Barras: recibe título de película → entradas vendidas. */
    private void dibujarBarras(Map<String, Integer> entradasPorPelicula) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<String, Integer> pelicula : entradasPorPelicula.entrySet()) {
            serie.getData().add(new XYChart.Data<>(pelicula.getKey(), pelicula.getValue()));
        }
        grafica.getData().clear();
        grafica.getData().add(serie);
    }

    /** Pastel: recibe título de película → ingresos brutos. */
    private void dibujarPastel(Map<String, Long> ingresosPorPelicula) {
        graficaIngresos.getData().clear();
        for (Map.Entry<String, Long> pelicula : ingresosPorPelicula.entrySet()) {
            graficaIngresos.getData().add(new PieChart.Data(
                    pelicula.getKey() + " · " + Formato.moneda(pelicula.getValue()), pelicula.getValue()));
        }
    }

    /** Líneas: recibe día → ventas brutas (incluye los días sin ventas). */
    private void dibujarLineas(Map<LocalDate, Long> ventasPorDia) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<LocalDate, Long> dia : ventasPorDia.entrySet()) {
            serie.getData().add(new XYChart.Data<>(dia.getKey().format(DIA), dia.getValue()));
        }
        graficaVentas.getData().clear();
        graficaVentas.getData().add(serie);
    }

    @FXML
    private void exportar() {
        ejecutar(() -> {
            generar();
            FileChooser selector = new FileChooser();
            selector.setTitle("Guardar reporte CinemaUQ");
            selector.setInitialFileName("reporte-" + tipo.getValue().name().toLowerCase() + "." + exportador.getExtension());
            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*." + exportador.getExtension()));
            File archivo = selector.showSaveDialog(resultado.getScene().getWindow());
            if (archivo == null) return;

            try {
                exportador.exportar(reporteActual, archivo.toPath());
            } catch (IOException error) {
                throw new IllegalStateException("No se pudo guardar el reporte", error);
            }
            Alertas.informacion("Reporte guardado", archivo.getName());
        });
    }
}
