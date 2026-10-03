package com.example.cinemauq.model;

/**
 * Patron Prototype: interfaz para objetos que saben crear una copia de si mismos.
 *
 * Se usa una interfaz propia en lugar de java.lang.Cloneable porque Cloneable
 * no declara ningun metodo, obliga a usar Object.clone() (que hace copia
 * superficial y lanza una excepcion verificada) y no permite indicar el tipo
 * que se devuelve. Aqui cada clase decide como copiarse y devuelve su propio tipo.
 *
 * @param <T> tipo del objeto que se clona
 */
public interface Clonable<T> {

    /**
     * Crea una copia independiente del objeto.
     *
     * @return un objeto nuevo con el mismo estado que el original
     */
    T clonar();
}
