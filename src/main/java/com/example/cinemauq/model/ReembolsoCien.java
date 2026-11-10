package com.example.cinemauq.model;

public class ReembolsoCien implements IPoliticaReembolso {
    @Override
    public double calcularReembolso(double valorPagado) {
        return valorPagado; // 100% de devolución
    }
}
