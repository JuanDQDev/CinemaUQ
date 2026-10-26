package com.example.cinemauq.controller;

/**
 * Contrato común de los controladores de las vistas.
 * Después de cargar el FXML, el contexto llama a inicializar() para entregarle
 * al controlador la fachada del modelo y el carrito de compra.
 */
public interface Controlador {
    void inicializar(ContextoAplicacion contexto);
}
