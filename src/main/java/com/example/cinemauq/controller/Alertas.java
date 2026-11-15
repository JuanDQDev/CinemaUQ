package com.example.cinemauq.controller;

import com.example.cinemauq.exception.*;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Avisos propios de CinemaUQ, visibles incluso al cambiar de página. */
public final class Alertas {
    private static VBox contenedor;
    private static Parent raiz;
    private static boolean temaClaro;

    private Alertas() { }

    public static void configurar(VBox panel, Parent raizPrincipal) {
        if (contenedor != null) contenedor.getChildren().clear();
        contenedor = panel;
        raiz = raizPrincipal;
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        prepararDialogo(dialogo);
        return dialogo.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void setTemaClaro(boolean claro) { temaClaro = claro; }

    public static void error(RuntimeException error) {
        mostrar(titulo(error), error.getMessage(), true);
    }

    public static void informacion(String encabezado, String contenido) {
        mostrar(encabezado, contenido, false);
    }

    private static void mostrar(String encabezado, String contenido, boolean error) {
        if (contenedor == null) {
            Alert dialogo = new Alert(error ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
            dialogo.setHeaderText(encabezado);
            dialogo.setContentText(contenido);
            prepararDialogo(dialogo);
            dialogo.showAndWait();
            return;
        }

        Label simbolo = new Label(error ? "!" : "✓");
        simbolo.getStyleClass().add("aviso-icono");
        Label titulo = new Label(encabezado);
        titulo.getStyleClass().add("titulo-seccion");
        titulo.setWrapText(true);
        Label mensaje = new Label(contenido == null ? "Intenta nuevamente." : contenido);
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(310);
        mensaje.getStyleClass().add("texto-secundario");

        VBox textos = new VBox(5, titulo, mensaje);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);
        Button cerrar = new Button("×");
        cerrar.getStyleClass().add("cerrar-aviso");
        cerrar.setAccessibleText("Cerrar notificación");

        HBox aviso = new HBox(12, simbolo, textos, cerrar);
        aviso.setAlignment(Pos.TOP_LEFT);
        aviso.getStyleClass().addAll("notificacion", error ? "notificacion-error" : "notificacion-exito");
        aviso.setMaxWidth(430);
        aviso.setAccessibleText(encabezado + ". " + contenido);
        aviso.setAccessibleRole(javafx.scene.AccessibleRole.TEXT);
        PauseTransition espera = new PauseTransition(Duration.seconds(error ? 10 : 8));
        cerrar.setOnAction(evento -> {
            espera.stop();
            contenedor.getChildren().remove(aviso);
        });
        espera.setOnFinished(evento -> {
            FadeTransition salida = new FadeTransition(Duration.millis(170), aviso);
            salida.setToValue(0);
            salida.setOnFinished(fin -> contenedor.getChildren().remove(aviso));
            salida.play();
        });
        aviso.setOnMouseEntered(evento -> espera.pause());
        aviso.setOnMouseExited(evento -> espera.play());
        if (contenedor.getChildren().size() >= 3) {
            contenedor.getChildren().remove(0);
        }
        contenedor.getChildren().add(aviso);
        Visuales.entrada(aviso);
        espera.play();
    }

    /** Aplica tema, propietario e icono también a la autorización de recarga. */
    public static void prepararDialogo(Dialog<?> dialogo) {
        dialogo.setTitle("CinemaUQ");
        if (raiz != null && raiz.getScene() != null) {
            dialogo.initOwner(raiz.getScene().getWindow());
        }
        dialogo.getDialogPane().getStylesheets().add(
                Alertas.class.getResource("/com/example/cinemauq/css/cinemauq.css").toExternalForm());
        dialogo.getDialogPane().getStyleClass().add("dialogo-cinema");
        if (temaClaro) dialogo.getDialogPane().getStyleClass().add("tema-claro");
        dialogo.setOnShown(evento -> {
            Stage ventana = (Stage) dialogo.getDialogPane().getScene().getWindow();
            ventana.getIcons().setAll(Visuales.logo());
        });
    }

    static String titulo(RuntimeException error) {
        if (error instanceof SaldoInsuficienteException) return "Saldo insuficiente";
        if (error instanceof TarjetaInactivaException) return "Tu tarjeta está inactiva";
        if (error instanceof AsientoOcupadoException) return "Ese asiento ya está ocupado";
        if (error instanceof EdadNoPermitidaException) return "Clasificación de edad";
        if (error instanceof CompraInvalidaException) return "Revisa tu compra";
        return "No pudimos completar la operación";
    }
}
