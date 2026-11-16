package com.example.cinemauq.controller;

import com.example.cinemauq.model.CarritoCompra;
import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.ElementoProducto;
import com.example.cinemauq.model.Entrada;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.MontoPuntos;
import com.example.cinemauq.model.TarjetaVirtual;
import com.example.cinemauq.service.CinemaFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Paso 4 · Resumen y pago.
 *
 * La vista solo muestra los datos y llama a la fachada. La verificación de
 * saldo, el descuento y el registro del movimiento los hace el modelo
 * (ServicioCompra + AdaptadorTarjetaVirtual + TarjetaVirtual). Si el modelo
 * lanza una excepción del dominio, aquí se muestra al usuario.
 */
public class PagoController implements Controlador {

    @FXML private VBox panelResumen;
    @FXML private Label lblNumeroTarjeta;
    @FXML private Label lblSaldoActual;
    @FXML private Label lblEstado;
    @FXML private Label lblTotal;
    @FXML private Label lblSaldoDespues;
    @FXML private Label lblAviso;
    @FXML private Button btnPagar;
    @FXML private Label lblPuntos;
    @FXML private Label lblPuntosUsados;
    @FXML private Spinner<Integer> spEntradasPuntos;
    @FXML private VBox panelProductosPuntos;

    /** Un CheckBox por cada producto del carrito que se puede pagar con puntos. */
    private final List<CheckBox> casillasPuntos = new ArrayList<>();
    private final List<ElementoProducto> productosCasillas = new ArrayList<>();

    private ContextoAplicacion contexto;
    private CinemaFacade fachada;
    private CarritoCompra carrito;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.fachada = contexto.getFachada();
        this.carrito = contexto.getCarrito();
        mostrarResumen();
        prepararPuntos();
        mostrarTarjeta();
    }

    /** Controles para elegir qué se paga con puntos. Cada cambio actualiza los totales. */
    private void prepararPuntos() {
        spEntradasPuntos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, carrito.getAsientos().size(), 0));
        spEntradasPuntos.valueProperty().addListener((obs, anterior, nuevo) -> mostrarTarjeta());
        for (ElementoProducto producto : carrito.getProductos()) {
            CheckBox casilla = new CheckBox(producto.getNombre() + " (" + Math.round(producto.calcularPrecio()) + " pts)");
            casilla.setOnAction(evento -> mostrarTarjeta());
            casillasPuntos.add(casilla);
            productosCasillas.add(producto);
        }
        panelProductosPuntos.getChildren().setAll(casillasPuntos);
    }

    private List<ElementoProducto> productosConPuntos() {
        List<ElementoProducto> elegidos = new ArrayList<>();
        for (int i = 0; i < casillasPuntos.size(); i++) {
            if (casillasPuntos.get(i).isSelected()) elegidos.add(productosCasillas.get(i));
        }
        return elegidos;
    }

    private void mostrarResumen() {
        Funcion funcion = carrito.getFuncion();
        List<String> asientos = carrito.getAsientos();
        Entrada entrada = fachada.getTipoEntrada(funcion);

        panelResumen.getChildren().setAll(
                titulo("Película"),
                texto(carrito.getPelicula().getNombrePelicula()),
                titulo("Función"),
                texto(Formato.funcion(funcion)),
                titulo("Asientos"),
                texto(String.join(", ", asientos)),
                new Separator(),
                titulo("Detalle"),
                linea(asientos.size() + " × " + entrada.getDetalle()
                                + " (" + Formato.moneda(entrada.calcularPrecioEntrada()) + ")",
                        entrada.calcularPrecioEntrada() * asientos.size()));

        for (ElementoProducto producto : fachada.getConfiteria()) {
            int unidades = carrito.cantidadDe(producto);
            if (unidades > 0) {
                panelResumen.getChildren().add(
                        linea(unidades + " × " + producto.getNombre(), producto.calcularPrecio() * unidades));
            }
        }
        panelResumen.getChildren().add(texto(fachada.getPromocionActual()));
        if (carrito.getProductos().isEmpty()) {
            panelResumen.getChildren().add(texto("Sin productos de confitería"));
        }
    }

    private void mostrarTarjeta() {
        TarjetaVirtual tarjeta = fachada.getTarjeta();
        int valorCarrito = fachada.calcularTotal(carrito.getFuncion(), carrito.getAsientos(), carrito.getProductos());
        int disponibles = fachada.getPuntosDisponibles();
        int puntos = Math.min(valorCarrito, fachada.calcularValorPuntos(
                carrito.getFuncion(), spEntradasPuntos.getValue(), productosConPuntos()));
        int total = valorCarrito - puntos;
        int saldoDespues = tarjeta.getSaldo() - total;
        lblPuntos.setText(disponibles + " disponibles");
        lblPuntosUsados.setText(puntos + " pts");
        btnPagar.setDisable(puntos > disponibles);

        lblNumeroTarjeta.setText(tarjeta.getNumero());
        lblSaldoActual.setText(Formato.moneda(tarjeta.getSaldo()));
        lblTotal.setText(Formato.moneda(total));
        lblSaldoDespues.setText(Formato.moneda(saldoDespues));

        lblEstado.getStyleClass().removeAll("estado-activa", "estado-inactiva");
        lblEstado.setText(tarjeta.isActiva() ? "Activa" : "Inactiva");
        lblEstado.getStyleClass().add(tarjeta.isActiva() ? "estado-activa" : "estado-inactiva");

        // Aviso previo para el usuario. La validación real la hace el modelo al pagar.
        lblAviso.getStyleClass().removeAll("aviso-error", "aviso-ok");
        if (puntos > disponibles) {
            lblAviso.setText("No tienes puntos suficientes: la selección cuesta " + puntos
                    + " puntos y tienes " + disponibles + ".");
            lblAviso.getStyleClass().add("aviso-error");
        } else if (!tarjeta.isActiva()) {
            lblAviso.setText("La tarjeta está inactiva. Actívala en «Mi tarjeta» para poder pagar.");
            lblAviso.getStyleClass().add("aviso-error");
        } else if (saldoDespues < 0) {
            lblAviso.setText("El saldo no alcanza: faltan " + Formato.moneda(-saldoDespues)
                    + ". Recarga la tarjeta en «Mi tarjeta».");
            lblAviso.getStyleClass().add("aviso-error");
        } else {
            lblAviso.setText("Al pagar se descontará " + Formato.moneda(total) + " de la tarjeta"
                    + (puntos > 0 ? " y " + puntos + " puntos" : "")
                    + ". Ganarás " + Math.round(total * MontoPuntos.PORCENTAJE_ACUMULACION) + " puntos con esta compra.");
            lblAviso.getStyleClass().add("aviso-ok");
        }
    }

    @FXML
    private void pagar() {
        try {
            Compra compra = fachada.confirmarCompra(
                    carrito.getFuncion(), carrito.getAsientos(), carrito.getProductos(),
                    spEntradasPuntos.getValue(), productosConPuntos());
            carrito.vaciar();
            Alertas.informacion("¡Compra confirmada!",
                    "Factura " + compra.getFactura()
                            + "\nAsientos: " + String.join(", ", compra.getAsientos())
                            + "\nTotal pagado: " + Formato.moneda(compra.getTotal())
                            + (compra.getPuntosRedimidos() > 0 ? "\nPuntos usados: " + compra.getPuntosRedimidos() : "")
                            + "\nSaldo restante: " + Formato.moneda(fachada.getTarjeta().getSaldo()));
            contexto.mostrar(Vista.COMPRAS);
        } catch (RuntimeException error) {
            Alertas.error(error);
            // Si el error fue un asiento ocupado o similar, se refrescan los datos.
            mostrarTarjeta();
        }
    }

    @FXML
    private void volver() {
        contexto.mostrar(Vista.CONFITERIA);
    }

    private Label titulo(String texto) {
        Label etiqueta = new Label(texto.toUpperCase());
        etiqueta.getStyleClass().add("texto-pantalla");
        return etiqueta;
    }

    private Label texto(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("titulo-seccion");
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    private HBox linea(String concepto, double valor) {
        Label izquierda = new Label(concepto);
        izquierda.setWrapText(true);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label derecha = new Label(Formato.moneda(valor));
        derecha.setMinWidth(Region.USE_PREF_SIZE);
        derecha.getStyleClass().add("titulo-seccion");
        return new HBox(10, izquierda, espacio, derecha);
    }
}
