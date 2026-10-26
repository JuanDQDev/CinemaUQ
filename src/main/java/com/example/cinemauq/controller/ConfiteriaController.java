package com.example.cinemauq.controller;

import com.example.cinemauq.model.CarritoCompra;
import com.example.cinemauq.model.Combo;
import com.example.cinemauq.model.ElementoProducto;
import com.example.cinemauq.model.Entrada;
import com.example.cinemauq.service.CinemaFacade;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Paso 3 · Confitería.
 *
 * Gracias al Composite, un producto suelto y un combo son ElementoProducto:
 * la vista los lista igual y obtiene el precio con calcularPrecio(), sin
 * preguntar de qué tipo es cada uno. Solo para mostrar el contenido del combo
 * se consulta si es un Combo.
 */
public class ConfiteriaController implements Controlador {

    @FXML private VBox listaProductos;
    @FXML private Label lblEntradas;
    @FXML private Label lblSubtotalEntradas;
    @FXML private VBox resumenConfiteria;
    @FXML private Label lblTotal;

    private ContextoAplicacion contexto;
    private CinemaFacade fachada;
    private CarritoCompra carrito;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.fachada = contexto.getFachada();
        this.carrito = contexto.getCarrito();

        for (ElementoProducto producto : fachada.getConfiteria()) {
            listaProductos.getChildren().add(crearFila(producto));
        }
        actualizarResumen();
    }

    private HBox crearFila(ElementoProducto producto) {
        boolean esCombo = producto instanceof Combo;

        Label icono = new Label(esCombo ? "C" : producto.getNombre().substring(0, 1));
        icono.getStyleClass().add("icono-producto");
        if (esCombo) {
            icono.getStyleClass().add("icono-combo");
        }

        Label nombre = new Label(producto.getNombre());
        nombre.getStyleClass().add("nombre-pelicula");
        Label detalle = new Label(describir(producto));
        detalle.getStyleClass().add("texto-secundario");
        detalle.setWrapText(true);
        VBox textos = new VBox(3, nombre, detalle);
        if (esCombo) {
            Combo combo = (Combo) producto;
            Label descuento = new Label(Math.round(combo.getDescuento() * 100) + " % de descuento");
            descuento.getStyleClass().add("etiqueta-descuento");
            textos.getChildren().add(descuento);
        }
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label precio = new Label(Formato.moneda(producto.calcularPrecio()));
        precio.getStyleClass().add("titulo-seccion");
        precio.setMinWidth(90);
        precio.setAlignment(Pos.CENTER_RIGHT);

        Label cantidad = new Label(String.valueOf(carrito.cantidadDe(producto)));
        cantidad.getStyleClass().add("cantidad");
        Button menos = new Button("−");
        menos.getStyleClass().add("boton-cantidad");
        Button mas = new Button("+");
        mas.getStyleClass().add("boton-cantidad");
        menos.setDisable(carrito.cantidadDe(producto) == 0);

        mas.setOnAction(evento -> {
            carrito.agregarProducto(producto);
            cantidad.setText(String.valueOf(carrito.cantidadDe(producto)));
            menos.setDisable(false);
            actualizarResumen();
        });
        menos.setOnAction(evento -> {
            carrito.quitarProducto(producto);
            int unidades = carrito.cantidadDe(producto);
            cantidad.setText(String.valueOf(unidades));
            menos.setDisable(unidades == 0);
            actualizarResumen();
        });

        HBox controles = new HBox(6, menos, cantidad, mas);
        controles.setAlignment(Pos.CENTER);

        HBox fila = new HBox(14, icono, textos, precio, controles);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("fila-producto");
        return fila;
    }

    /** Texto con el contenido de un combo o la indicación de producto individual. */
    private String describir(ElementoProducto producto) {
        if (!(producto instanceof Combo)) {
            return "Producto individual";
        }
        List<String> nombres = new ArrayList<>();
        for (ElementoProducto elemento : ((Combo) producto).getElementos()) {
            nombres.add(elemento.getNombre());
        }
        return "Incluye: " + String.join(" + ", nombres);
    }

    private void actualizarResumen() {
        List<String> asientos = carrito.getAsientos();
        Entrada entrada = fachada.getTipoEntrada(carrito.getFuncion());
        lblEntradas.setText(asientos.size() + (asientos.size() == 1 ? " entrada" : " entradas"));
        lblSubtotalEntradas.setText(Formato.moneda(entrada.calcularPrecioEntrada() * asientos.size()));

        resumenConfiteria.getChildren().clear();
        for (ElementoProducto producto : fachada.getConfiteria()) {
            int unidades = carrito.cantidadDe(producto);
            if (unidades > 0) {
                Label nombre = new Label(unidades + " × " + producto.getNombre());
                nombre.setWrapText(true);
                Region espacio = new Region();
                HBox.setHgrow(espacio, Priority.ALWAYS);
                Label valor = new Label(Formato.moneda(producto.calcularPrecio() * unidades));
                valor.setMinWidth(Region.USE_PREF_SIZE);
                resumenConfiteria.getChildren().add(new HBox(8, nombre, espacio, valor));
            }
        }

        // El total lo calcula el modelo (ServicioCompra), no la vista.
        int total = fachada.calcularTotal(carrito.getFuncion(), asientos, carrito.getProductos());
        lblTotal.setText(Formato.moneda(total));
    }

    @FXML
    private void continuar() {
        contexto.mostrar(Vista.PAGO);
    }

    @FXML
    private void volver() {
        contexto.mostrar(Vista.ASIENTOS);
    }
}
