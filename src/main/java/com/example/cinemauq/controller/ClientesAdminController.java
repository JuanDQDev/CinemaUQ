package com.example.cinemauq.controller;

import com.example.cinemauq.model.Cliente;
import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.SolicitudRecarga;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientesAdminController extends FormularioAdmin {
    @FXML private ComboBox<Cliente> cliente;
    @FXML private ComboBox<SolicitudRecarga> solicitud;
    @FXML private TextField monto;
    @FXML private Label datos;
    @FXML private TextArea historial;

    @FXML
    private void initialize() {
        cliente.valueProperty().addListener((propiedad, anterior, actual) -> seleccionar());
    }

    @Override
    protected void cargarCampos() {
        cliente.setValue(null);
        cliente.getItems().setAll(sistema.getClientes());
        solicitud.setValue(null);
        solicitud.getItems().setAll(sistema.getSolicitudes());
        monto.setText("50000");
        datos.setText("Selecciona un cliente.");
        historial.clear();
    }

    private void seleccionar() {
        Cliente elegido = cliente.getValue();
        if (elegido == null) return;

        String estado = elegido.getTarjeta().isActiva() ? "activa" : "bloqueada";
        datos.setText(elegido + " · edad " + elegido.getEdad()
                + " · saldo " + Formato.moneda(elegido.getTarjeta().getSaldo())
                + " · tarjeta " + estado
                + " · puntos " + Math.round(elegido.getMontoPuntos().getTotalPuntosVigentes()));

        StringBuilder texto = new StringBuilder();
        for (Compra compra : sistema.comprasParaAdministrador(elegido)) {
            texto.append(compra.getFactura()).append(" · ").append(compra.getNombrePelicula())
                    .append(" · ").append(compra.getEstado())
                    .append(" · pagado ").append(Formato.moneda(compra.getTotal()))
                    .append(" · devuelto ").append(Formato.moneda(compra.getReembolso())).append("\n");
        }
        historial.setText(texto.length() == 0 ? "Sin compras." : texto.toString());
    }

    @FXML
    private void recargar() {
        ejecutar(() -> {
            Cliente elegido = cliente.getValue();
            if (elegido == null) throw new IllegalArgumentException("Selecciona un cliente");
            int valor = entero(monto);
            if (Alertas.confirmar("Confirmar recarga", "¿Acreditar " + Formato.moneda(valor) + " a " + elegido + "?")) {
                sistema.recargarCliente(elegido, valor);
                cargarCampos();
                Alertas.informacion("Recarga aprobada", "El saldo del cliente fue actualizado.");
            }
        });
    }

    @FXML
    private void aprobar() {
        ejecutar(() -> {
            SolicitudRecarga elegida = solicitud.getValue();
            if (elegida == null) throw new IllegalArgumentException("Selecciona una solicitud");
            if (Alertas.confirmar("Aprobar recarga", elegida.toString())) {
                sistema.resolverSolicitud(elegida, true);
                cargarCampos();
            }
        });
    }

    @FXML
    private void rechazar() {
        ejecutar(() -> {
            sistema.resolverSolicitud(solicitud.getValue(), false);
            cargarCampos();
        });
    }
}
