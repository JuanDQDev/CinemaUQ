package com.example.cinemauq.model;

public class TarjetaVirtual {
    private final String numero;
    private boolean activa = true;
    private int saldo = 0;
    private final List<MovimientoTarjeta> movimientos = new ArrayList<>();

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
        saldo = saldo - monto;
        movimientos.add(new MovimientoTarjeta(TipoMovimiento.COMPRA, -monto, saldo, descripcion));
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
        saldo = saldo + monto;
        movimientos.add(new MovimientoTarjeta(TipoMovimiento.RECARGA, monto,
                saldo, "Recarga autorizada por: " + administrador.getNombre()));
    }

    public void reembolsar(int monto, String referenciaCompra){
        validarMonto(monto);
        verificarActiva();
        if(referenciaCompra == null || referenciaCompra.isBlank()){
            throw new IllegalArgumentException("Se requiere la referencia de la compra");
        }
        boolean encontrada = false;
        for (MovimientoTarjeta movimiento : movimientos){
            if(movimiento.getDescripcion().equals(referenciaCompra)){
                if(movimiento.getTipo() == TipoMovimiento.REEMBOLSO){
                    throw new IllegalArgumentException("Esta referencia ya fue reembolsada");
                }
                if (movimiento.getTipo() == TipoMovimiento.COMPRA && movimiento.getMonto() == -monto) {
                    encontrada = true;
                }
            }
        }
        if(!encontrada){
            throw new IllegalArgumentException("No existe esta referencia de pago");
        }
        if (monto > Integer.MAX_VALUE - saldo) {
            throw new IllegalArgumentException("El reembolso supera el saldo maximo permitido");
        }
        saldo = saldo + monto;
        movimientos.add(new MovimientoTarjeta(TipoMovimiento.REEMBOLSO, monto, saldo, referenciaCompra))
    }

    public String getNumero() { return numero; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public int getSaldo() { return saldo; }
    public List<MovimientoTarjeta> getMovimientos() { return new ArrayList<>(movimientos); }

}
