package com.example.cinemauq.controller;

import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.ElementoProducto;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Historial de las compras confirmadas en la sesión. */
public class ComprasController implements Controlador {

    @FXML private Label lblResumen;
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colFactura;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colPelicula;
    @FXML private TableColumn<Compra, String> colFuncion;
    @FXML private TableColumn<Compra, String> colAsientos;
    @FXML private TableColumn<Compra, String> colConfiteria;
    @FXML private TableColumn<Compra, String> colTotal;

    private ContextoAplicacion contexto;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;

        colFactura.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFactura()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncion().getPeliculaFuncion().getNombrePelicula()));
        colFuncion.setCellValueFactory(c -> new SimpleStringProperty(Formato.funcion(c.getValue().getFuncion())));
        colAsientos.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getAsientos())));
        colConfiteria.setCellValueFactory(c -> new SimpleStringProperty(describirConfiteria(c.getValue())));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(Formato.moneda(c.getValue().getTotal())));
        tablaCompras.setPlaceholder(new Label("Todavía no has comprado entradas."));

        List<Compra> compras = contexto.getFachada().getCompras();
        int totalGastado = 0;
        for (Compra compra : compras) {
            totalGastado += compra.getTotal();
        }
        Collections.reverse(compras);
        tablaCompras.getItems().setAll(compras);
        lblResumen.setText(compras.isEmpty()
                ? "Aquí verás las compras confirmadas."
                : compras.size() + (compras.size() == 1 ? " compra" : " compras")
                + " · total pagado " + Formato.moneda(totalGastado));
    }

    private String describirConfiteria(Compra compra) {
        if (compra.getConfiteria().isEmpty()) {
            return "—";
        }
        // Agrupa unidades repetidas: "2 × Gaseosa, 1 × Nachos".
        List<ElementoProducto> distintos = new ArrayList<>();
        for (ElementoProducto producto : compra.getConfiteria()) {
            if (!distintos.contains(producto)) {
                distintos.add(producto);
            }
        }
        List<String> partes = new ArrayList<>();
        for (ElementoProducto producto : distintos) {
            partes.add(Collections.frequency(compra.getConfiteria(), producto) + " × " + producto.getNombre());
        }
        return String.join(", ", partes);
    }

    @FXML
    private void irACartelera() {
        contexto.mostrar(Vista.CARTELERA);
    }
}
