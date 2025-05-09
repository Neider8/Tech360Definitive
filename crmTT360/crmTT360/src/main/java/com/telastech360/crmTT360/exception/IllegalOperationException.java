// src/main/java/com/telastech360/crmTT360/exception/IllegalOperationException.java
package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando una operación solicitada no es permitida
 * debido a la lógica de negocio o al estado actual del sistema
 * (ej. intentar eliminar el último administrador, operar sobre un pedido cerrado).
 * Generalmente mapeada a un código HTTP 400 (Bad Request) o 409 (Conflict).
 */
public class IllegalOperationException extends RuntimeException {
    /**
     * Constructor con mensaje de error.
     * @param message Mensaje descriptivo del error.
     */
    public IllegalOperationException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa original.
     * @param message Mensaje descriptivo del error.
     * @param cause La excepción original que causó esta.
     */
    public IllegalOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}