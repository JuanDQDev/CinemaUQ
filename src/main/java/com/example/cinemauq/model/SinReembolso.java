package com.example.cinemauq.model;

public class SinReembolso implements IPoliticaReembolso {
        @Override
        public double calcularReembolso(double valorPagado) {
            return 0.0; // 0% de devolución
        }
}
