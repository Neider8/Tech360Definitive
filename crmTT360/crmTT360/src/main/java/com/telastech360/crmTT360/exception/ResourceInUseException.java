package com.telastech360.crmTT360.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación (ej. eliminar)
 * sobre un recurso que está actualmente en uso por otras entidades.
 * Generalmente mapeada a HTTP 409 (Conflict).
 */
public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}