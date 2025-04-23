package com.telastech360.crmTT360.advice;

import com.telastech360.crmTT360.exception.ResourceNotFoundException; // Tu excepción personalizada
import com.telastech360.crmTT360.exception.DuplicateResourceException; // Tu excepción personalizada
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Para errores de validación de DTOs
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable; // Para obtener mensajes de errores de validación
import org.springframework.web.context.request.WebRequest; // Para obtener detalles de la solicitud

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Para procesar errores de validación

// Esta clase manejará excepciones globalmente en todos los controladores
@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja la excepción ResourceNotFoundException (cuando un recurso no se encuentra)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // Usa el mensaje de tu excepción
        body.put("path", request.getDescription(false).replace("uri=", "")); // Obtiene la ruta de la solicitud

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Maneja la excepción DuplicateResourceException (cuando se intenta crear un recurso duplicado)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.CONFLICT.value()); // Código 409 Conflict es apropiado para duplicados
        body.put("error", "Conflict");
        body.put("message", ex.getMessage()); // Usa el mensaje de tu excepción
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Maneja los errores de validación (@Valid en)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400 Bad Request para errores de validación
        body.put("error", "Bad Request");
        body.put("message", "Errores de validación");

        // Obtiene todos los errores de validación
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("errors", errors); // Incluye la lista de errores específicos
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    // Maneja excepciones generales no capturadas por otros manejadores (debe ser el último)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {

        ex.printStackTrace(); // Considera usar un logger adecuado en lugar de printStackTrace

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // Código 500 para errores inesperados
        body.put("error", "Internal Server Error");
        body.put("message", "Ocurrió un error inesperado en el servidor."); // Mensaje genérico por seguridad
        body.put("path", request.getDescription(false).replace("uri=", ""));


        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Puedes añadir más métodos @ExceptionHandler para otros tipos de excepciones si es necesario
    // Por ejemplo, para excepciones de seguridad AccessDeniedException (403 Forbidden)
    /*
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
         Map<String, Object> body = new LinkedHashMap<>();
         body.put("timestamp", new Date());
         body.put("status", HttpStatus.FORBIDDEN.value());
         body.put("error", "Forbidden");
         body.put("message", "No tienes permiso para acceder a este recurso."); // Mensaje específico
         body.put("path", request.getDescription(false).replace("uri=", ""));
         return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
     */
}