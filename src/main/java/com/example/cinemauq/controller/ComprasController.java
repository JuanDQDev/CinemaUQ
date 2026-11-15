package com.example.cinemauq.controller;

import com.example.cinemauq.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.*;

public class ComprasController implements Controlador {
    @FXML private Label lblResumen, lblCancelacion;
    @FXML private Button btnCancelar;
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colFactura, colFecha, colPelicula, colFuncion,
            colAsientos, colConfiteria, colTotal, colEstado, colReembolso;
    private ContextoAplicacion contexto;

    @Override public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        colFactura.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFactura()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombrePelicula()));
        colFuncion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDetalleFuncion()));
        colAsientos.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getAsientos())));
        colConfiteria.setCellValueFactory(c -> new SimpleStringProperty(describirConfiteria(c.getValue())));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(Formato.moneda(c.getValue().getTotal())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));
        colReembolso.setCellValueFactory(c -> new SimpleStringProperty(Formato.moneda(c.getValue().getReembolso())));
        tablaCompras.setPlaceholder(new Label("Todavía no has comprado entradas."));
        tablaCompras.getSelectionModel().selectedItemProperty().addListener((o, anterior, compra) -> actualizarCancelacion());
        refrescar();
    }

    private void refrescar() {
        Compra elegida = tablaCompras.getSelectionModel().getSelectedItem();
        List<Compra> compras = contexto.getFachada().getCompras();
        long pagado = 0, devuelto = 0;
        for (Compra compra : compras) { pagado += compra.getTotal(); devuelto += compra.getReembolso(); }
        Collections.reverse(compras);
        tablaCompras.getItems().setAll(compras);
        if (elegida != null) tablaCompras.getSelectionModel().select(elegida);
        tablaCompras.refresh();
        lblResumen.setText(compras.size() + " compras · pagado " + Formato.moneda(pagado)
                + " · reembolsado " + Formato.moneda(devuelto) + " · gasto neto " + Formato.moneda(pagado - devuelto));
        actualizarCancelacion();
    }

    private void actualizarCancelacion() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        btnCancelar.setDisable(true);
        if (compra == null) { lblCancelacion.setText("Selecciona una compra de la tabla para ver su reembolso."); return; }
        if (compra.estaCancelada()) {
            lblCancelacion.setText(compra.getMotivoCancelacion() + ". Ya se devolvieron "
                    + Formato.moneda(compra.getReembolso()) + " a tu tarjeta. No se puede reembolsar otra vez.");
            return;
        }
        try {
            int monto = contexto.getFachada().calcularReembolso(compra);
            lblCancelacion.setText("Al cancelar " + compra.getFactura() + " recibirás " + Formato.moneda(monto)
                    + " en tu tarjeta. Se liberarán los asientos y se retirarán los puntos de esa compra.");
            btnCancelar.setDisable(false);
        } catch (RuntimeException e) { lblCancelacion.setText(e.getMessage()); }
    }

    @FXML private void cancelarCompra() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        try {
            int monto = contexto.getFachada().calcularReembolso(compra);
            if (!Alertas.confirmar("Cancelar compra " + compra.getFactura(),
                    "Se devolverán " + Formato.moneda(monto) + " a tu tarjeta y se liberarán tus asientos. ¿Continuar?")) return;
            contexto.getFachada().cancelarCompra(compra, monto);
            refrescar();
            Alertas.informacion("Compra cancelada", "Reembolso de " + Formato.moneda(monto) + " registrado en Mi tarjeta.");
        } catch (RuntimeException e) { Alertas.error(e); actualizarCancelacion(); }
    }

    private String describirConfiteria(Compra compra) {
        List<String> nombres = new ArrayList<>();
        for (ElementoProducto p : compra.getConfiteria()) if (!nombres.contains(p.getNombre())) nombres.add(p.getNombre());
        List<String> partes = new ArrayList<>();
        for (String nombre : nombres) {
            int cantidad = 0;
            for (ElementoProducto p : compra.getConfiteria()) if (p.getNombre().equals(nombre)) cantidad++;
            partes.add(cantidad + " × " + nombre);
        }
        return partes.isEmpty() ? "—" : String.join(", ", partes);
    }

    @FXML private void irACartelera() { contexto.mostrar(Vista.CARTELERA); }
}
