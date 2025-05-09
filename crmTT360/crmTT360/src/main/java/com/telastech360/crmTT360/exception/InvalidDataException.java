// src/main/java/com/telastech360/crmTT360/exception/InvalidDataException.java
package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando los datos proporcionados en una solicitud son inválidos
 * desde el punto de vista semántico o de negocio, aunque puedan ser sintácticamente correctos.
 * (ej. valor de enum incorrecto, contraseña que no cumple complejidad, fecha inválida).
 * Generalmente mapeada a un código HTTP 422 (Unprocessable Entity) o 400 (Bad Request).
 */
public class InvalidDataException extends RuntimeException {
    /**
     * Constructor con mensaje de error.
     * @param message Mensaje descriptivo del error.
     */
    public InvalidDataException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa original.
     * @param message Mensaje descriptivo del error.
     * @param cause La excepción original que causó esta.
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}