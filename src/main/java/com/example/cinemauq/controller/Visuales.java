package com.example.cinemauq.controller;

import com.example.cinemauq.model.Pelicula;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public final class Visuales {
    private static final Image TITANIC = cargarImagen("titanic.jpg");
    private static final Image SPIDERMAN = cargarImagen("brandnewday.png");
    private static final Image DOOMSDAY = cargarImagen("doomsday.jpeg");
    private static final Image LOGO = cargarImagen("CinemaUQ.png");

    private Visuales() { }

    private static Image cargarImagen(String nombre) {
        java.net.URL recurso = Visuales.class.getResource("/images/" + nombre);
        if (recurso == null) {
            throw new IllegalStateException("Falta la imagen: " + nombre);
        }
        return new Image(recurso.toExternalForm(), 900, 0, true, true);
    }

    public static Image logo() { return LOGO; }

    public static Image poster(Pelicula pelicula) {
        if ("P01".equals(pelicula.getCodigo())) return TITANIC;
        if ("P02".equals(pelicula.getCodigo())) return SPIDERMAN;
        if ("P03".equals(pelicula.getCodigo())) return DOOMSDAY;
        return LOGO;
    }

    public static String acento(Pelicula pelicula) {
        if ("P01".equals(pelicula.getCodigo())) return "#70b9e8";
        if ("P02".equals(pelicula.getCodigo())) return "#ef5968";
        if ("P03".equals(pelicula.getCodigo())) return "#c7a253";
        return "#8b9fff";
    }

    public static String secundario(Pelicula pelicula) {
        if ("P01".equals(pelicula.getCodigo())) return "#274d78";
        if ("P02".equals(pelicula.getCodigo())) return "#294e95";
        if ("P03".equals(pelicula.getCodigo())) return "#254b36";
        return "#294e95";
    }

    public static void colorear(Node nodo, Pelicula pelicula) {
        nodo.setStyle("-pelicula-acento: " + acento(pelicula)
                + "; -pelicula-secundario: " + secundario(pelicula) + ";");
    }

    public static void redondear(StackPane panel, double radio) {
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(panel.widthProperty());
        recorte.heightProperty().bind(panel.heightProperty());
        recorte.setArcWidth(radio);
        recorte.setArcHeight(radio);
        panel.setClip(recorte);
    }

    public static void ponerFondo(StackPane panel, Pelicula pelicula) {
        panel.setMinWidth(0);
        Image imagen = poster(pelicula);
        panel.setStyle("-fx-background-color: linear-gradient(to right, "
                + secundario(pelicula) + ", " + acento(pelicula) + ");");

        ImageView difuminado = new ImageView(imagen);
        difuminado.setPreserveRatio(false);
        difuminado.fitWidthProperty().bind(panel.widthProperty());
        difuminado.fitHeightProperty().bind(panel.heightProperty());
        difuminado.setEffect(new GaussianBlur(35));
        difuminado.setOpacity(0.48);

        ImageView original = new ImageView(imagen);
        original.setPreserveRatio(true);
        original.fitHeightProperty().bind(panel.heightProperty());
        original.fitWidthProperty().bind(panel.widthProperty());

        panel.getChildren().setAll(difuminado, original);
        redondear(panel, 24);
    }

    public static void animarTarjeta(Node nodo, Pelicula pelicula) {
        DropShadow brillo = new DropShadow(18, Color.web(acento(pelicula), 0.24));
        brillo.setSpread(0.04);
        nodo.setEffect(brillo);
        TranslateTransition elevar = new TranslateTransition(Duration.millis(170), nodo);
        nodo.setOnMouseEntered(evento -> {
            elevar.stop();
            elevar.setToY(-5);
            elevar.play();
            brillo.setRadius(30);
            brillo.setColor(Color.web(acento(pelicula), 0.58));
        });
        nodo.setOnMouseExited(evento -> {
            elevar.stop();
            elevar.setToY(0);
            elevar.play();
            brillo.setRadius(18);
            brillo.setColor(Color.web(acento(pelicula), 0.24));
        });
    }

    public static void animarBotones(Parent raiz) {
        for (Node nodo : raiz.getChildrenUnmodifiable()) {
            if (nodo instanceof ButtonBase
                    && !nodo.getStyleClass().contains("asiento")
                    && !Boolean.TRUE.equals(nodo.getProperties().get("hover-listo"))) {
                nodo.getProperties().put("hover-listo", true);
                ScaleTransition escala = new ScaleTransition(Duration.millis(120), nodo);
                nodo.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, evento -> {
                    escala.stop();
                    escala.setToX(1.025);
                    escala.setToY(1.025);
                    escala.play();
                });
                nodo.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_EXITED, evento -> {
                    escala.stop();
                    escala.setToX(1);
                    escala.setToY(1);
                    escala.play();
                });
            }
            if (nodo instanceof Parent) animarBotones((Parent) nodo);
        }
    }

    public static ParallelTransition entrada(Node nodo) {
        nodo.setOpacity(0);
        nodo.setTranslateY(10);
        FadeTransition aparecer = new FadeTransition(Duration.millis(220), nodo);
        aparecer.setToValue(1);
        TranslateTransition subir = new TranslateTransition(Duration.millis(220), nodo);
        subir.setToY(0);
        ParallelTransition transicion = new ParallelTransition(aparecer, subir);
        transicion.play();
        return transicion;
    }

    public static SVGPath iconoTema(boolean claro) {
        SVGPath icono = new SVGPath();
        if (claro) {
            icono.setContent("M 20 14 A 8 8 0 0 1 10 4 A 8 8 0 1 0 20 14 Z");
        } else {
            icono.setContent("M 16 12 A 4 4 0 1 1 8 12 A 4 4 0 1 1 16 12 "
                    + "M 12 1 L 12 4 M 12 20 L 12 23 M 1 12 L 4 12 "
                    + "M 20 12 L 23 12 M 4 4 L 6 6 M 18 18 L 20 20 "
                    + "M 4 20 L 6 18 M 18 6 L 20 4");
        }
        icono.getStyleClass().add("icono-tema");
        return icono;
    }
}
