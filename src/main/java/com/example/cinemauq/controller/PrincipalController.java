package com.example.cinemauq.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Controlador de la ventana principal: barra superior (navegación, cliente y
 * saldo), indicador de pasos de la compra y área central donde se cargan las vistas.
 */
public class PrincipalController implements Controlador {

    @FXML private StackPane contenedor;
    @FXML private Label lblCliente;
    @FXML private Label lblSaldo;
    @FXML private HBox barraPasos;
    @FXML private Label paso1;
    @FXML private Label paso2;
    @FXML private Label paso3;
    @FXML private Label paso4;
    @FXML private Button btnCartelera;
    @FXML private Button btnTarjeta;
    @FXML private Button btnCompras;

    private ContextoAplicacion contexto;

    @Override
    public void inicializar(ContextoAplicacion contexto) {
        this.contexto = contexto;
        contexto.setContenedor(contenedor);

        lblCliente.setText(contexto.getFachada().getCliente().getNombre());
        contexto.saldoProperty().addListener((obs, anterior, nuevo) -> mostrarSaldo(nuevo.intValue()));
        mostrarSaldo(contexto.saldoProperty().get());

        contexto.vistaActualProperty().addListener((obs, anterior, vista) -> actualizarIndicadores(vista));
        contexto.mostrar(Vista.CARTELERA);
    }

    private void mostrarSaldo(int saldo) {
        lblSaldo.setText("Saldo tarjeta: " + Formato.moneda(saldo));
    }

    private void actualizarIndicadores(Vista vista) {
        boolean enCompra = vista.getPaso() > 0;
        barraPasos.setVisible(enCompra);
        barraPasos.setManaged(enCompra);

        Label[] pasos = {paso1, paso2, paso3, paso4};
        for (int i = 0; i < pasos.length; i++) {
            int numero = i + 1;
            pasos[i].getStyleClass().removeAll("paso-activo", "paso-hecho");
            if (numero == vista.getPaso()) {
                pasos[i].getStyleClass().add("paso-activo");
            } else if (numero < vista.getPaso()) {
                pasos[i].getStyleClass().add("paso-hecho");
            }
        }

        marcar(btnCartelera, enCompra);
        marcar(btnTarjeta, vista == Vista.TARJETA);
        marcar(btnCompras, vista == Vista.COMPRAS);
    }

    private void marcar(Button boton, boolean activo) {
        boton.getStyleClass().remove("nav-activo");
        if (activo) {
            boton.getStyleClass().add("nav-activo");
        }
    }

    @FXML
    private void irACartelera() {
        contexto.mostrar(Vista.CARTELERA);
    }

    @FXML
    private void irATarjeta() {
        contexto.mostrar(Vista.TARJETA);
    }

    @FXML
    private void irACompras() {
        contexto.mostrar(Vista.COMPRAS);
    }
}
