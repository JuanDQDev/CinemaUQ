package com.example.cinemauq.model;

import com.example.cinemauq.exception.SaldoInsuficienteException;
import com.example.cinemauq.exception.TarjetaInactivaException;

import java.util.ArrayList;
import java.util.List;

public class TarjetaVirtual {
    private final String numero;
    private boolean activa = true;
    private int saldo = 0;
    private final List<MovimientoTarjeta> movimientos = new ArrayList<>();
    private final List<ObservadorSaldo> observadores = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número de tarjeta requerido");
        }
        this.numero = numero;
    }

    private void validarMonto(int monto){
        if(monto < 0){
            throw new IllegalArgumentException("El monto debe de ser mayor a 0");
        }
    }

    private void verificarActiva(){
        if(!activa){
            throw new TarjetaInactivaException("la tarjeta virtual esta inactiva");
        }
    }

    public void verificarPago(int monto){
        validarMonto(monto);
        verificarActiva();
        if (saldo < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Disponible: $" + saldo + ", compra: $" + monto);
        }
    }

    public void descontar(int monto, String descripcion){
        verificarPago(monto);
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Se requiere la referencia de la compra");
        }
        for(MovimientoTarjeta movimiento : movimientos){
            if(movimiento.getTipo() == TipoMovimiento.COMPRA && movimiento.getDescripcion().equals(descripcion)){
                throw new IllegalArgumentException("La referencia de pago ya fue utilizada");
            }
        }
        registrarMovimiento(TipoMovimiento.COMPRA, -monto, descripcion);
    }

    public void recargar(Administrador administrador, int monto){
        if(administrador == null){
            throw new IllegalArgumentException("Administrador requerido.");
        }
        validarMonto(monto);
        verificarActiva();
        if (monto > Integer.MAX_VALUE - saldo) {
            throw new IllegalArgumentException("La recarga supera el saldo maximo");
        }
        registrarMovimiento(TipoMovimiento.RECARGA, monto,
                "Recarga autorizada por: " + administrador.getNombre());
    }

    public void otorgarBonoBienvenida() {
        if (saldo != 0 || !movimientos.isEmpty()) {
            throw new IllegalStateException("El bono inicial ya fue asignado");
        }
        registrarMovimiento(TipoMovimiento.BONO_BIENVENIDA, 50000, "Bono de bienvenida");
    }

    public void validarReembolso(int monto, String referenciaCompra) {
        if (monto <= 0) {
            throw new IllegalArgumentException(
                    "El reembolso debe ser mayor que cero");
        }
        if (referenciaCompra == null || referenciaCompra.isBlank()) {
            throw new IllegalArgumentException(
                    "Se requiere la referencia de la compra");
        }

        boolean compraEncontrada = false;
        int valorPagado = 0;
        for (MovimientoTarjeta movimiento : movimientos) {
            if (referenciaCompra.equals(movimiento.getDescripcion())) {
                if (movimiento.getTipo() == TipoMovimiento.REEMBOLSO) {
                    throw new IllegalArgumentException(
                            "Esta compra ya fue reembolsada");
                }
                if (movimiento.getTipo() == TipoMovimiento.COMPRA) {
                    compraEncontrada = true;
                    valorPagado = -movimiento.getMonto();
                }
            }
        }

        if (!compraEncontrada) {
            throw new IllegalArgumentException(
                    "No existe esta referencia de pago");
        }
        if (monto > valorPagado) {
            throw new IllegalArgumentException(
                    "No se puede devolver más de lo pagado");
        }
        if (monto > Integer.MAX_VALUE - saldo) {
            throw new IllegalArgumentException(
                    "El reembolso supera el saldo máximo permitido");
        }

    }

    public void reembolsar(int monto, String referenciaCompra) {
        validarReembolso(monto, referenciaCompra);
        registrarMovimiento(TipoMovimiento.REEMBOLSO, monto, referenciaCompra);
    }

    public void agregarObservador(ObservadorSaldo observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(ObservadorSaldo observador) {
        observadores.remove(observador);
    }

    private void registrarMovimiento(TipoMovimiento tipo, int monto, String descripcion) {
        saldo += monto;
        movimientos.add(new MovimientoTarjeta(tipo, monto, saldo, descripcion));
        if (monto != 0) notificarSaldo();
    }

    private void notificarSaldo() {
        // Copia para que un observador pueda retirarse durante el aviso.
        int nuevoSaldo = saldo;
        for (ObservadorSaldo observador : new ArrayList<>(observadores)) {
            try {
                observador.saldoCambiado(nuevoSaldo);
            } catch (RuntimeException error) {
                // Un fallo de la pantalla no revierte un movimiento ya registrado.
                System.getLogger(TarjetaVirtual.class.getName()).log(
                        System.Logger.Level.WARNING, "No se pudo avisar el nuevo saldo", error);
            }
        }
    }

    public String getNumero() { return numero; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public int getSaldo() { return saldo; }
    public List<MovimientoTarjeta> getMovimientos() { return new ArrayList<>(movimientos); }

}
