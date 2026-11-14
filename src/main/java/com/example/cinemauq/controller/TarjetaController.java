package com.example.cinemauq.controller;

import com.example.cinemauq.model.MovimientoTarjeta;
import com.example.cinemauq.model.TarjetaVirtual;
import com.example.cinemauq.service.CinemaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.List;

/**
 * Vista de la tarjeta virtual: saldo, estado (activa/inactiva), recarga
 * autorizada por el administrador y tabla de movimientos (compras,
 * recargas y reembolsos registrados por el modelo).
 */
public class TarjetaController implements Controlador {

    @FXML private Label lblNumero;
    @FXML private Label lblSaldo;
    @FXML private Label lblTitular;
    @FXML private Label lblEstado;
    @FXML private Button btnEstado;
    @FXML private TextField txtMonto;
    @FXML private TableView<MovimientoTarjeta> tablaMovimientos;
    @FXML private TableColumn<MovimientoTarjeta, String> colFecha;
    @FXML private TableColumn<MovimientoTarjeta, String> colTipo;
    @FXML private TableColumn<MovimientoTarjeta, String> colDescripcion;
    @FXML private TableColumn<MovimientoTarjeta, Number> colMonto;
    @FXML private TableColumn<MovimientoTarjeta, String> colSaldo;

    private ContextoAplicacion contexto;
    private CinemaFacade fachada;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.fachada = contexto.getFachada();
        configurarTabla();
        txtMonto.setOnAction(evento -> recargar());
        refrescar();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getFechaHora()));
        colTipo.setCellValueFactory(celda -> new SimpleStringProperty(nombreTipo(celda.getValue())));
        colDescripcion.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getDescripcion()));
        colSaldo.setCellValueFactory(celda ->
                new SimpleStringProperty(Formato.moneda(celda.getValue().getSaldoFinal())));
        colMonto.setCellValueFactory(celda ->
                new javafx.beans.property.SimpleIntegerProperty(celda.getValue().getMonto()));
        colMonto.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number monto, boolean vacia) {
                super.updateItem(monto, vacia);
                getStyleClass().removeAll("monto-positivo", "monto-negativo");
                if (vacia || monto == null) {
                    setText(null);
                } else {
                    setText(Formato.montoConSigno(monto.intValue()));
                    getStyleClass().add(monto.intValue() >= 0 ? "monto-positivo" : "monto-negativo");
                }
            }
        });
        tablaMovimientos.setPlaceholder(new Label("La tarjeta todavía no tiene movimientos."));
    }

    private String nombreTipo(MovimientoTarjeta movimiento) {
        switch (movimiento.getTipo()) {
            case COMPRA: return "Compra";
            case RECARGA: return "Recarga";
            case REEMBOLSO: return "Reembolso";
            default: return movimiento.getTipo().name();
        }
    }

    private void refrescar() {
        TarjetaVirtual tarjeta = fachada.getTarjeta();
        lblNumero.setText(tarjeta.getNumero());
        lblSaldo.setText(Formato.moneda(tarjeta.getSaldo()));
        lblTitular.setText(fachada.getCliente().getNombre());

        lblEstado.getStyleClass().removeAll("estado-activa", "estado-inactiva");
        lblEstado.setText(tarjeta.isActiva() ? "Activa" : "Inactiva");
        lblEstado.getStyleClass().add(tarjeta.isActiva() ? "estado-activa" : "estado-inactiva");
        btnEstado.setText(tarjeta.isActiva() ? "Bloquear tarjeta" : "Activar tarjeta");

        // Los más recientes primero.
        List<MovimientoTarjeta> movimientos = tarjeta.getMovimientos();
        Collections.reverse(movimientos);
        tablaMovimientos.getItems().setAll(movimientos);

        contexto.actualizarSaldo();
    }

    @FXML
    private void recargar() {
        int monto;
        try {
            monto = Integer.parseInt(txtMonto.getText().replace(".", "").replace("$", "").trim());
        } catch (NumberFormatException error) {
            Alertas.error(new IllegalArgumentException("Escribe el monto en números, por ejemplo 50000."));
            return;
        }
        if (monto <= 0) {
            Alertas.error(new IllegalArgumentException("El monto de la recarga debe ser mayor que cero."));
            return;
        }
        TextField txtAdministrador = new TextField();
        txtAdministrador.setPromptText(
                "Identificación del administrador");

        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Clave del administrador");

        VBox campos = new VBox(10);
        campos.getChildren().addAll(
                new Label("Identificación:"),
                txtAdministrador,
                new Label("Clave:"),
                txtClave);

        Alert autorizacion = new Alert(Alert.AlertType.CONFIRMATION);
        autorizacion.setTitle("Autorización de recarga");
        autorizacion.setHeaderText(
                "El administrador debe autorizar " + Formato.moneda(monto));
        autorizacion.getDialogPane().setContent(campos);

        Optional<ButtonType> respuesta = autorizacion.showAndWait();

        if (!respuesta.isPresent() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            fachada.recargarTarjeta(
                    monto,
                    txtAdministrador.getText().trim(),
                    txtClave.getText());

            txtMonto.clear();
            refrescar();

        } catch (RuntimeException error) {
            Alertas.error(error);

        } finally {
            txtClave.clear();
        }
    }

    @FXML
    private void cambiarEstado() {
        TarjetaVirtual tarjeta = fachada.getTarjeta();
        tarjeta.setActiva(!tarjeta.isActiva());
        refrescar();
    }

    @FXML
    private void sugerir20() {
        txtMonto.setText("20000");
    }

    @FXML
    private void sugerir50() {
        txtMonto.setText("50000");
    }

    @FXML
    private void sugerir100() {
        txtMonto.setText("100000");
    }
}
