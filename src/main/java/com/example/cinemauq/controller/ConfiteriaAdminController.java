package com.example.cinemauq.controller;

import com.example.cinemauq.model.Combo;
import com.example.cinemauq.model.ElementoProducto;
import com.example.cinemauq.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ConfiteriaAdminController extends FormularioAdmin {
    @FXML private ComboBox<Producto> producto;
    @FXML private ComboBox<Combo> combo;
    @FXML private TextField nombre;
    @FXML private TextField precio;
    @FXML private TextField nombreCombo;
    @FXML private TextField descuento;
    @FXML private VBox listaProductos;

    @FXML
    private void initialize() {
        producto.valueProperty().addListener((propiedad, anterior, actual) -> seleccionarProducto());
        combo.valueProperty().addListener((propiedad, anterior, actual) -> seleccionarCombo());
    }

    @Override
    protected void cargarCampos() {
        producto.setValue(null);
        producto.getItems().clear();
        combo.setValue(null);
        combo.getItems().clear();
        listaProductos.getChildren().clear();
        nombre.clear();
        precio.clear();
        nombreCombo.clear();
        descuento.setText("10");

        for (ElementoProducto elemento : sistema.getConfiteria()) {
            if (elemento instanceof Producto) {
                Producto opcion = (Producto) elemento;
                producto.getItems().add(opcion);
                CheckBox casilla = new CheckBox(opcion.getNombre());
                listaProductos.getChildren().add(casilla);
            } else if (elemento instanceof Combo) {
                combo.getItems().add((Combo) elemento);
            }
        }
    }

    private void seleccionarProducto() {
        Producto elegido = producto.getValue();
        if (elegido == null) return;

        nombre.setText(elegido.getNombre());
        precio.setText(String.valueOf(elegido.getPrecioUnitario()));
    }

    private void seleccionarCombo() {
        Combo elegido = combo.getValue();
        if (elegido == null) return;

        nombreCombo.setText(elegido.getNombre());
        descuento.setText(String.valueOf(Math.round(elegido.getDescuento() * 100)));
        List<ElementoProducto> incluidos = elegido.getElementos();
        for (int i = 0; i < producto.getItems().size(); i++) {
            CheckBox casilla = (CheckBox) listaProductos.getChildren().get(i);
            casilla.setSelected(incluidos.contains(producto.getItems().get(i)));
        }
    }

    @FXML
    private void guardarProducto() {
        guardar(() -> sistema.guardarProducto(producto.getValue(), nombre.getText(), entero(precio)));
    }

    @FXML
    private void guardarCombo() {
        guardar(() -> {
            List<ElementoProducto> elegidos = new ArrayList<>();
            for (int i = 0; i < producto.getItems().size(); i++) {
                CheckBox casilla = (CheckBox) listaProductos.getChildren().get(i);
                if (casilla.isSelected()) elegidos.add(producto.getItems().get(i));
            }
            sistema.guardarCombo(combo.getValue(), nombreCombo.getText(), entero(descuento), elegidos);
        });
    }

    @FXML
    private void eliminarProducto() {
        eliminar(producto.getValue(), "Eliminar producto",
                "Si pertenece a un combo, primero debes editar ese combo.");
    }

    @FXML
    private void eliminarCombo() {
        eliminar(combo.getValue(), "Eliminar combo", "Se retirará del catálogo.");
    }

    private void eliminar(ElementoProducto elemento, String titulo, String mensaje) {
        ejecutar(() -> {
            if (Alertas.confirmar(titulo, mensaje)) {
                sistema.eliminarConfiteria(elemento);
                cargarCampos();
            }
        });
    }
}
