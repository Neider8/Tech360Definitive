// src/main/java/com/telastech360/crmTT360/exception/ResourceNotFoundException.java
package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando se intenta acceder o modificar un recurso
 * que no existe en la base de datos (ej. buscar usuario por ID inexistente).
 * Generalmente mapeada a un código HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructor con mensaje de error.
     * @param message Mensaje descriptivo del error, usualmente indicando el ID o clave del recurso no encontrado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}