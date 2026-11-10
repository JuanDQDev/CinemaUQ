package com.example.cinemauq.model;

public class ReembolsoOchenta implements IPoliticaReembolso{
    @Override
    public double calcularReembolso(double valorPagado) {
        return valorPagado*0.8;
    }
}
