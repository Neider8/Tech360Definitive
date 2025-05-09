// src/main/java/com/telastech360/crmTT360/exception/DuplicateResourceException.java
package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe
 * y viola una restricción de unicidad (ej. email, código).
 * Generalmente mapeada a un código HTTP 409 (Conflict).
 */
public class DuplicateResourceException extends RuntimeException {
    /**
     * Constructor con mensaje de error.
     * @param message Mensaje descriptivo del error.
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}