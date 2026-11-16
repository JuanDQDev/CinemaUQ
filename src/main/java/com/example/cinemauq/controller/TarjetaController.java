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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

/**
 * Vista de la tarjeta virtual: saldo, estado (activa/inactiva), recarga
 * autorizada por el administrador y tabla de movimientos (compras,
 * recargas y reembolsos registrados por el modelo).
 */
public class TarjetaController implements Controlador {

    @FXML private Label lblSolicitudes;
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

    private CinemaFacade fachada;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
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
            case BONO_BIENVENIDA: return "Bienvenida";
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

        String texto = "Puntos disponibles: " + fachada.getPuntosDisponibles();
        for (com.example.cinemauq.model.SolicitudRecarga solicitud : fachada.getSolicitudes()) {
            texto += " · " + Formato.moneda(solicitud.getMonto()) + ": " + solicitud.getEstado();
        }
        lblSolicitudes.setText(texto);
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
        try {
            fachada.solicitarRecarga(monto);
            txtMonto.clear();
            refrescar();
            Alertas.informacion("Solicitud enviada", "El administrador podrá aprobarla desde Clientes y recargas.");
        } catch (RuntimeException error) { Alertas.error(error); }
    }

    /** Detalle de puntos: cada acumulación con su vencimiento y las redenciones (consulta de puntos). */
    @FXML
    private void verPuntos() {
        java.time.format.DateTimeFormatter dia = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        com.example.cinemauq.model.MontoPuntos cuenta = fachada.getCliente().getMontoPuntos();
        StringBuilder texto = new StringBuilder("Disponibles para redimir: " + fachada.getPuntosDisponibles()
                + "\nGanas el 10 % de cada compra · 1 punto = $1 · vencen al año\n\nACUMULACIONES\n");
        java.util.List<com.example.cinemauq.model.LotePuntos> lotes = cuenta.getLotes();
        if (lotes.isEmpty()) texto.append("Todavía no has acumulado puntos.\n");
        for (com.example.cinemauq.model.LotePuntos lote : lotes) {
            String estado = lote.isAnulado() ? "anulado (compra cancelada)"
                    : lote.estaVencido() ? "vencido"
                    : "vence el " + lote.getFechaVencimiento().format(dia);
            texto.append(lote.getFechaObtencion().format(dia)).append(" · ").append(lote.getFactura())
                    .append(" · ganados ").append(Math.round(lote.getCantidad()))
                    .append(" · quedan ").append(Math.round(lote.getDisponible()))
                    .append(" · ").append(estado).append("\n");
        }
        texto.append("\nREDENCIONES\n");
        java.util.List<com.example.cinemauq.model.RedencionPuntos> redenciones = cuenta.getRedenciones();
        if (redenciones.isEmpty()) texto.append("Todavía no has redimido puntos.\n");
        for (com.example.cinemauq.model.RedencionPuntos redencion : redenciones) {
            texto.append(redencion.getFecha().format(dia)).append(" · ").append(redencion.getFactura())
                    .append(" · ").append(redencion.getPuntos()).append(" puntos")
                    .append(redencion.isDevuelta() ? " (devueltos por cancelación)" : "").append("\n");
        }
        javafx.scene.control.TextArea detalle = new javafx.scene.control.TextArea(texto.toString());
        detalle.setEditable(false);
        detalle.setPrefSize(560, 320);
        javafx.scene.control.Alert dialogo = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        dialogo.setTitle("Mis puntos");
        dialogo.setHeaderText("Mis puntos");
        dialogo.getDialogPane().setContent(detalle);
        Alertas.prepararDialogo(dialogo);
        dialogo.showAndWait();
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
