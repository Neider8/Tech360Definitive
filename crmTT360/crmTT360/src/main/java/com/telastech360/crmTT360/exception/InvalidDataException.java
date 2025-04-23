package com.telastech360.crmTT360.exception;

// Excepción personalizada para manejar errores de datos inválidos,
// como un valor de enum que no coincide.
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}