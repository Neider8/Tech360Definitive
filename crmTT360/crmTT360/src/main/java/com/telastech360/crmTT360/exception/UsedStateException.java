// src/main/java/com/telastech360/crmTT360/exception/UsedStateException.java
package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando se intenta eliminar un estado que está actualmente
 * en uso por una o más entidades (ej. intentar eliminar el estado "ACTIVO"
 * si hay usuarios activos).
 * Generalmente mapeada a un código HTTP 409 (Conflict).
 */
public class UsedStateException extends RuntimeException {
    /**
     * Constructor con mensaje de error.
     * @param message Mensaje descriptivo indicando por qué no se puede eliminar el estado.
     */
    public UsedStateException(String message) {
        super(message);
    }
}