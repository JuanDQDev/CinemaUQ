package com.example.cinemauq.controller;

import com.example.cinemauq.model.*;
import com.example.cinemauq.service.CinemaFacade;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import java.util.List;

public class CarteleraController implements Controlador {
    @FXML private FlowPane panelPeliculas;
    @FXML private VBox panelDetalle;
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
                setText(vacia || funcion == null ? null
                        : Formato.funcion(funcion) + "\n"
                        + funcion.getSalaFuncion().contarDisponibles() + " asientos disponibles");
            }
        });
        listaFunciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, funcion) -> mostrarPrecio(funcion));
        listaFunciones.setOnMouseClicked(evento -> {
            if (evento.getButton() != MouseButton.PRIMARY || evento.getClickCount() != 2) return;
            Node objetivo = (Node) evento.getTarget();
            while (objetivo != null && !(objetivo instanceof ListCell)) {
                objetivo = objetivo.getParent();
            }
            if (objetivo instanceof ListCell && !((ListCell<?>) objetivo).isEmpty()) {
                elegirAsientos();
            }
        });
        listaFunciones.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER) elegirAsientos();
        });

        List<Pelicula> peliculas = fachada.getCartelera();
        VBox primera = null;
        VBox previa = null;
        for (Pelicula pelicula : peliculas) {
            VBox tarjeta = crearTarjeta(pelicula);
            panelPeliculas.getChildren().add(tarjeta);
            if (primera == null) primera = tarjeta;
            if (pelicula == contexto.getCarrito().getPelicula()) previa = tarjeta;
        }
        panelPeliculas.widthProperty().addListener((obs, anterior, ancho) -> {
            double anchoTarjeta = Math.max(160, Math.min(260, (ancho.doubleValue() - 80) / 3));
            for (Node nodo : panelPeliculas.getChildren()) {
                ((VBox) nodo).setPrefWidth(anchoTarjeta);
                ((VBox) nodo).setMaxWidth(anchoTarjeta);
            }
        });
        if (previa != null) {
            seleccionar(contexto.getCarrito().getPelicula(), previa);
            listaFunciones.getSelectionModel().select(contexto.getCarrito().getFuncion());
        } else if (primera != null) {
            seleccionar(peliculas.get(0), primera);
        } else {
            lblTitulo.setText("Próximamente, nuevas historias");
            btnElegirAsientos.setDisable(true);
        }
    }

    private VBox crearTarjeta(Pelicula pelicula) {
        ImageView imagen = new ImageView(Visuales.poster(pelicula));
        imagen.setPreserveRatio(true);
        imagen.setSmooth(true);
        StackPane poster = new StackPane(imagen);
        poster.getStyleClass().add("poster");
        poster.setMinWidth(0);
        poster.setMinHeight(Region.USE_PREF_SIZE);
        poster.setMaxHeight(Region.USE_PREF_SIZE);
        imagen.fitWidthProperty().bind(poster.widthProperty());
        imagen.fitHeightProperty().bind(poster.heightProperty());
        Visuales.redondear(poster, 20);

        Label nombre = new Label(pelicula.getNombrePelicula());
        nombre.setWrapText(true);
        nombre.setMinHeight(44);
        nombre.getStyleClass().add("nombre-pelicula");
        int numero = fachada.getFunciones(pelicula).size();
        Label datos = new Label(clasificacion(pelicula) + "  ·  "
                + numero + (numero == 1 ? " función" : " funciones"));
        datos.getStyleClass().add("texto-secundario");
        Label ayuda = new Label("Doble clic para elegir asientos");
        ayuda.getStyleClass().add("ayuda-tarjeta");
        ayuda.setWrapText(true);
        VBox textos = new VBox(6, nombre, datos, ayuda);
        textos.getStyleClass().add("texto-tarjeta");
        VBox tarjeta = new VBox(0, poster, textos);
        tarjeta.setPrefWidth(230);
        tarjeta.setMinWidth(0);
        tarjeta.setMaxHeight(Region.USE_PREF_SIZE);
        tarjeta.getStyleClass().add("tarjeta-pelicula");
        tarjeta.setId("pelicula-" + pelicula.getCodigo());
        tarjeta.setFocusTraversable(true);
        tarjeta.setAccessibleRole(AccessibleRole.BUTTON);
        tarjeta.setAccessibleText(pelicula.getNombrePelicula()
                + ". Doble clic o Enter para elegir asientos.");
        poster.prefHeightProperty().bind(tarjeta.widthProperty().multiply(1.42));
        Visuales.colorear(tarjeta, pelicula);
        Visuales.animarTarjeta(tarjeta, pelicula);

        tarjeta.setOnMouseClicked(evento -> {
            if (evento.getButton() != MouseButton.PRIMARY) return;
            seleccionar(pelicula, tarjeta);
            if (evento.getClickCount() == 2) elegirAsientos();
        });
        tarjeta.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.SPACE) {
                seleccionar(pelicula, tarjeta);
                if (evento.getCode() == KeyCode.ENTER) elegirAsientos();
                evento.consume();
            }
        });
        return tarjeta;
    }

    private void seleccionar(Pelicula pelicula, VBox tarjeta) {
        // El segundo clic no debe borrar una función que ya eligió el usuario.
        if (peliculaSeleccionada == pelicula) return;
        if (tarjetaSeleccionada != null) {
            tarjetaSeleccionada.getStyleClass().remove("tarjeta-pelicula-seleccionada");
        }
        tarjetaSeleccionada = tarjeta;
        tarjeta.getStyleClass().add("tarjeta-pelicula-seleccionada");
        peliculaSeleccionada = pelicula;
        Visuales.colorear(panelDetalle, pelicula);
        lblTitulo.setText(pelicula.getNombrePelicula());
        lblClasificacion.setText(clasificacion(pelicula));
        lblDescripcion.setText(pelicula.getDescripcion());
        listaFunciones.getItems().setAll(fachada.getFunciones(pelicula));
        if (listaFunciones.getItems().isEmpty()) mostrarPrecio(null);
        else listaFunciones.getSelectionModel().selectFirst();
    }

    private String clasificacion(Pelicula pelicula) {
        RestriccionPelicula r = pelicula.getRestriccionPelicula();
        return r == null ? "Todo público" : "+" + r.getEdadMinima() + " años";
    }

    private void mostrarPrecio(Funcion funcion) {
        btnElegirAsientos.setDisable(funcion == null);
        if (funcion == null) {
            lblPrecio.setText("No hay funciones disponibles.");
            return;
        }
        Entrada entrada = fachada.getTipoEntrada(funcion);
        lblPrecio.setText(Formato.moneda(entrada.calcularPrecioEntrada())
                + " / persona\n" + entrada.getDetalle());
    }

    @FXML
    private void elegirAsientos() {
        Funcion funcion = listaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null || peliculaSeleccionada == null) return;
        contexto.getCarrito().seleccionarFuncion(peliculaSeleccionada, funcion);
        contexto.mostrar(Vista.ASIENTOS);
    }
}
