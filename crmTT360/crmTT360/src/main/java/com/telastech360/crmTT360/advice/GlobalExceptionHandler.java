package com.telastech360.crmTT360.advice;

import com.telastech360.crmTT360.exception.*; // Importar todas las excepciones personalizadas
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException; // Asegurar importación
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones específicas y genéricas para devolver respuestas HTTP estandarizadas.
 */
@ControllerAdvice // Indica que esta clase manejará excepciones globalmente
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Crea un cuerpo de respuesta de error estandarizado.
     * @param status El HttpStatus a incluir.
     * @param errorType El tipo de error (ej. "Bad Request", "Not Found").
     * @param message El mensaje de error principal.
     * @param request La solicitud web actual.
     * @return Un mapa que representa el cuerpo JSON del error.
     */
    private Map<String, Object> createErrorBody(HttpStatus status, String errorType, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", errorType);
        body.put("message", message);
        // Extrae la URI de la descripción de la solicitud
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }

    // === Manejador Específico para Errores de Validación ===
    /**
     * Maneja las excepciones MethodArgumentNotValidException lanzadas cuando fallan
     * las validaciones de @Valid en los @RequestBody de los controladores.
     * Devuelve un HttpStatus 400 (Bad Request) con detalles de los campos erróneos.
     *
     * @param ex La excepción MethodArgumentNotValidException capturada.
     * @param request La solicitud web actual.
     * @return ResponseEntity con código 400 y detalles de los errores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        // Crea el cuerpo base del error 400
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Bad Request", "Errores de validación en la solicitud", request);

        // Extrae los errores específicos de cada campo que falló la validación
        List<String> errors = ex.getBindingResult() // Obtiene el resultado del binding y validación
                .getFieldErrors() // Obtiene solo los errores de campos específicos
                .stream()
                // Mapea cada error de campo a un String "nombreCampo: mensajeError"
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList()); // Recolecta los mensajes en una lista

        body.put("errors", errors); // Añade la lista detallada de errores al cuerpo de la respuesta

        // Loguea los errores específicos para depuración en el servidor
        log.warn("Errores de validación detectados: {} en la URI: {}", errors, request.getDescription(false).replace("uri=", ""));

        // Devuelve la respuesta 400 Bad Request con el cuerpo detallado
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // === Otros Manejadores Específicos (igual que los tenías) ===

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Recurso no encontrado: {} en {}", ex.getMessage(), request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        log.warn("Conflicto - Recurso duplicado: {} en {}", ex.getMessage(), request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex, WebRequest request) {
        log.warn("Datos inválidos en la solicitud: {} en {}", ex.getMessage(), request.getDescription(false));
        // Usar 400 Bad Request o 422 Unprocessable Entity según prefieras semánticamente
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<Object> handleResourceInUseException(ResourceInUseException ex, WebRequest request) {
        log.warn("Conflicto - Recurso en uso: {} en {}", ex.getMessage(), request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<Object> handleIllegalOperationException(IllegalOperationException ex, WebRequest request) {
        log.warn("Operación ilegal intentada: {} en {}", ex.getMessage(), request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsedStateException.class)
    public ResponseEntity<Object> handleUsedStateException(UsedStateException ex, WebRequest request) {
        log.warn("Conflicto - Estado en uso: {} en {}", ex.getMessage(), request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Manejador para errores de autorización (Spring Security)
    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        String errorMessage = "Acceso denegado. Permisos insuficientes.";
        // Podemos intentar obtener un mensaje más específico si es AuthorizationDeniedException
        if (ex instanceof AuthorizationDeniedException && ex.getMessage() != null && !ex.getMessage().isBlank()) {
            errorMessage = ex.getMessage();
        }
        log.warn("Acceso Denegado: {} en {}", errorMessage, request.getDescription(false));
        Map<String, Object> body = createErrorBody(HttpStatus.FORBIDDEN, "Forbidden", errorMessage, request);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }


    // === Manejador Genérico (Fallback) ===
    /**
     * Maneja cualquier otra excepción no capturada por los manejadores específicos.
     * Devuelve un HttpStatus 500 (Internal Server Error).
     * Es importante loguear el stack trace completo aquí para poder diagnosticar errores inesperados.
     *
     * @param ex La excepción genérica capturada.
     * @param request La solicitud web actual.
     * @return ResponseEntity con código 500 y mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        // Loguear el error completo, incluyendo el stack trace (el ', ex' al final lo hace)
        log.error("Error interno inesperado en la solicitud: {} en {}", ex.getMessage(), request.getDescription(false), ex);

        // Crear cuerpo de respuesta genérico para error 500
        Map<String, Object> body = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Ocurrió un error inesperado en el servidor. Por favor, contacte al administrador.", request);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}