package com.example.cinemauq.model;

import java.time.LocalDateTime;

public interface EstadoCompra {

    void validarCancelacion(Compra compra, LocalDateTime horaActual);
    EstadoCompra cancelar(Compra compra, LocalDateTime horaActual);
    String getNombre();

}
