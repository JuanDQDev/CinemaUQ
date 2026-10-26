package com.example.cinemauq.controller;

import com.example.cinemauq.model.Asiento;
import com.example.cinemauq.model.CarritoCompra;
import com.example.cinemauq.model.Entrada;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.Sala;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * Paso 2 · Selección gráfica de asientos.
 *
 * Dibuja la sala de la función elegida (la copia que le dio el Prototype) con
 * un botón por asiento. Los ocupados quedan deshabilitados, así que no se pueden
 * elegir (RN-002). Los elegidos se guardan en el CarritoCompra.
 */
public class AsientosController implements Controlador {

    @FXML private Label lblPelicula;
    @FXML private Label lblFuncion;
    @FXML private GridPane gridAsientos;
    @FXML private Label lblAsientos;
    @FXML private Label lblTipoEntrada;
    @FXML private Label lblSubtotal;
    @FXML private Button btnContinuar;

    private ContextoAplicacion contexto;
    private CarritoCompra carrito;
    private Entrada entrada;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.carrito = contexto.getCarrito();
        Funcion funcion = carrito.getFuncion();
        if (funcion == null) {
            lblPelicula.setText("Primero elige una función en la cartelera");
            btnContinuar.setDisable(true);
            return;
        }
        entrada = contexto.getFachada().getTipoEntrada(funcion);

        lblPelicula.setText(carrito.getPelicula().getNombrePelicula());
        lblFuncion.setText(Formato.funcion(funcion));
        lblTipoEntrada.setText(entrada.getDetalle() + ": "
                + Formato.moneda(entrada.calcularPrecioEntrada()) + " c/u");

        dibujarSala(funcion.getSalaFuncion());
        actualizarResumen();
    }

    private void dibujarSala(Sala sala) {
        gridAsientos.getChildren().clear();
        for (int columna = 1; columna <= sala.getColumnas(); columna++) {
            Label numero = new Label(String.valueOf(columna));
            numero.getStyleClass().add("etiqueta-fila");
            numero.setMaxWidth(Double.MAX_VALUE);
            gridAsientos.add(numero, columna, 0);
        }
        for (int fila = 0; fila < sala.getFilas(); fila++) {
            gridAsientos.add(etiquetaFila((char) ('A' + fila)), 0, fila + 1);
            gridAsientos.add(etiquetaFila((char) ('A' + fila)), sala.getColumnas() + 1, fila + 1);
        }
        for (Asiento asiento : sala.getAsientos()) {
            gridAsientos.add(crearBotonAsiento(asiento), asiento.getNumero(), asiento.getFila() - 'A' + 1);
        }
    }

    private Label etiquetaFila(char fila) {
        Label etiqueta = new Label(String.valueOf(fila));
        etiqueta.getStyleClass().add("etiqueta-fila");
        return etiqueta;
    }

    private ToggleButton crearBotonAsiento(Asiento asiento) {
        String codigo = asiento.getCodigo();
        ToggleButton boton = new ToggleButton(String.valueOf(asiento.getNumero()));
        boton.getStyleClass().add("asiento");
        boton.setFocusTraversable(false);
        if (asiento.isOcupado()) {
            boton.getStyleClass().add("asiento-ocupado");
            boton.setDisable(true);
            boton.setTooltip(new Tooltip(codigo + " · ocupado"));
        } else {
            boton.setSelected(carrito.contieneAsiento(codigo));
            boton.setTooltip(new Tooltip(codigo + " · libre"));
            boton.setOnAction(evento -> {
                boolean elegido = carrito.alternarAsiento(codigo);
                boton.setSelected(elegido);
                actualizarResumen();
            });
        }
        return boton;
    }

    private void actualizarResumen() {
        List<String> asientos = carrito.getAsientos();
        if (asientos.isEmpty()) {
            lblAsientos.setText("Aún no has elegido asientos. Haz clic sobre los asientos libres.");
        } else {
            lblAsientos.setText(asientos.size() + (asientos.size() == 1 ? " asiento: " : " asientos: ")
                    + String.join(", ", asientos));
        }
        lblSubtotal.setText(Formato.moneda(entrada.calcularPrecioEntrada() * asientos.size()));
        btnContinuar.setDisable(asientos.isEmpty());
    }

    @FXML
    private void continuar() {
        contexto.mostrar(Vista.CONFITERIA);
    }

    @FXML
    private void volver() {
        contexto.mostrar(Vista.CARTELERA);
    }
}
