package com.example.cinemauq.controller;

import com.example.cinemauq.model.Entrada;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.Pelicula;
import com.example.cinemauq.model.RestriccionPelicula;
import com.example.cinemauq.service.CinemaFacade;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Paso 1 · Cartelera: muestra las películas, sus funciones y el precio de la
 * entrada según el formato de la sala (calculado por el Decorator de Entrada).
 */
public class CarteleraController implements Controlador {

    /** Colores de los pósters generados (no hay imágenes de las películas). */
    private static final String[][] COLORES_POSTER = {
            {"#1d3557", "#457b9d"}, {"#6a040f", "#d00000"}, {"#3c096c", "#7b2cbf"},
            {"#1b4332", "#40916c"}, {"#7f4f24", "#b08968"}
    };

    @FXML private FlowPane panelPeliculas;
    @FXML private Label lblTitulo;
    @FXML private Label lblClasificacion;
    @FXML private Label lblDescripcion;
    @FXML private ListView<Funcion> listaFunciones;
    @FXML private Label lblPrecio;
    @FXML private Button btnElegirAsientos;

    private ContextoAplicacion contexto;
    private CinemaFacade fachada;
    private Pelicula peliculaSeleccionada;
    private VBox tarjetaSeleccionada;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.fachada = contexto.getFachada();

        listaFunciones.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Funcion funcion, boolean vacia) {
                super.updateItem(funcion, vacia);
                setWrapText(true);
                setPrefWidth(0);
                if (vacia || funcion == null) {
                    setText(null);
                } else {
                    setText(Formato.funcion(funcion) + "\n"
                            + funcion.getSalaFuncion().contarDisponibles() + " de "
                            + funcion.getSalaFuncion().getCapacidad() + " asientos libres");
                }
            }
        });
        listaFunciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, funcion) -> mostrarPrecio(funcion));
        listaFunciones.setOnMouseClicked(evento -> {
            if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2) {
                elegirAsientos();
            }
        });

        List<Pelicula> cartelera = fachada.getCartelera();
        VBox primera = null;
        VBox previa = null;
        for (int i = 0; i < cartelera.size(); i++) {
            Pelicula pelicula = cartelera.get(i);
            VBox tarjeta = crearTarjeta(pelicula, COLORES_POSTER[i % COLORES_POSTER.length]);
            panelPeliculas.getChildren().add(tarjeta);
            if (primera == null) primera = tarjeta;
            if (contexto.getCarrito().getPelicula() == pelicula) previa = tarjeta;
        }

        if (previa != null) {
            seleccionar(contexto.getCarrito().getPelicula(), previa);
            listaFunciones.getSelectionModel().select(contexto.getCarrito().getFuncion());
        } else if (primera != null) {
            seleccionar(cartelera.get(0), primera);
        } else {
            lblTitulo.setText("No hay películas en cartelera");
            btnElegirAsientos.setDisable(true);
        }
    }

    private VBox crearTarjeta(Pelicula pelicula, String[] colores) {
        Label tituloPoster = new Label(pelicula.getNombrePelicula());
        tituloPoster.getStyleClass().add("poster-titulo");
        tituloPoster.setWrapText(true);
        StackPane poster = new StackPane(tituloPoster);
        poster.getStyleClass().add("poster");
        poster.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + colores[0] + ", " + colores[1] + ");");
        poster.setPrefSize(190, 230);
        poster.setAlignment(Pos.BOTTOM_LEFT);

        Label nombre = new Label(pelicula.getNombrePelicula());
        nombre.getStyleClass().add("nombre-pelicula");
        nombre.setMaxWidth(166);

        int numeroFunciones = fachada.getFunciones(pelicula).size();
        Label datos = new Label(textoClasificacion(pelicula.getRestriccionPelicula())
                + " · " + numeroFunciones + (numeroFunciones == 1 ? " función" : " funciones"));
        datos.getStyleClass().add("texto-secundario");

        VBox textos = new VBox(4, nombre, datos);
        textos.setStyle("-fx-padding: 0 12 0 12;");

        VBox tarjeta = new VBox(10, poster, textos);
        tarjeta.getStyleClass().add("tarjeta-pelicula");
        tarjeta.setPrefWidth(194);
        tarjeta.setOnMouseClicked(evento -> seleccionar(pelicula, tarjeta));
        return tarjeta;
    }

    private void seleccionar(Pelicula pelicula, VBox tarjeta) {
        if (tarjetaSeleccionada != null) {
            tarjetaSeleccionada.getStyleClass().remove("tarjeta-pelicula-seleccionada");
        }
        tarjetaSeleccionada = tarjeta;
        tarjeta.getStyleClass().add("tarjeta-pelicula-seleccionada");
        peliculaSeleccionada = pelicula;

        lblTitulo.setText(pelicula.getNombrePelicula());
        lblClasificacion.setText(textoClasificacion(pelicula.getRestriccionPelicula()));
        lblDescripcion.setText(pelicula.getDescripcion());

        listaFunciones.getItems().setAll(fachada.getFunciones(pelicula));
        if (!listaFunciones.getItems().isEmpty()) {
            listaFunciones.getSelectionModel().selectFirst();
        } else {
            mostrarPrecio(null);
        }
    }

    private void mostrarPrecio(Funcion funcion) {
        btnElegirAsientos.setDisable(funcion == null);
        if (funcion == null) {
            lblPrecio.setText(listaFunciones.getItems().isEmpty()
                    ? "Esta película no tiene funciones programadas."
                    : "Selecciona una función.");
            return;
        }
        Entrada entrada = fachada.getTipoEntrada(funcion);
        lblPrecio.setText(entrada.getDetalle() + ": " + Formato.moneda(entrada.calcularPrecioEntrada())
                + " por persona");
    }

    private String textoClasificacion(RestriccionPelicula restriccion) {
        return restriccion == null ? "Todo público" : "+" + restriccion.getEdadMinima();
    }

    @FXML
    private void elegirAsientos() {
        Funcion funcion = listaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null || peliculaSeleccionada == null) {
            return;
        }
        contexto.getCarrito().seleccionarFuncion(peliculaSeleccionada, funcion);
        contexto.mostrar(Vista.ASIENTOS);
    }
}
