package com.example.cinemauq.model;

import java.time.LocalDateTime;

public class LotePuntos {
        private double cantidad;
        private LocalDateTime fechaObtencion;

        public LotePuntos(double cantidad) {
            this.cantidad = cantidad;
            this.fechaObtencion = LocalDateTime.now(); // Guarda la fecha y hora exactas
        }

        public double getCantidad() {
            return cantidad;
        }

        public LocalDateTime getFechaObtencion() {
            return fechaObtencion;
        }

        // Método útil para validar si este lote ya cumplió el año y expiró
        public boolean estaVencido() {
            return LocalDateTime.now().isAfter(fechaObtencion.plusYears(1));
        }
    }
