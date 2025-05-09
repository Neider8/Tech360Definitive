// src/main/java/com/telastech360/crmTT360/security/auth/AuthEntryPointJwt.java
package com.telastech360.crmTT360.security.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada para la autenticación JWT.
 * Esta clase se invoca cuando un usuario no autenticado intenta acceder a un recurso seguro.
 * Rechaza la solicitud con un error 401 (Unauthorized).
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Método invocado cuando falla la autenticación o no se proporcionan credenciales.
     * Registra el error y envía una respuesta HTTP 401.
     *
     * @param request La solicitud que resultó en una AuthenticationException.
     * @param response La respuesta para poder enviar el error 401.
     * @param authException La excepción que se lanzó.
     * @throws IOException Si ocurre un error de entrada/salida al enviar el error.
     * @throws ServletException Si ocurre un error interno del servlet.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Registra el intento de acceso no autorizado
        logger.error("Error de acceso no autorizado: {}", authException.getMessage());
        // Podrías añadir más detalles del request si fuera necesario para depuración
        // logger.debug("Request URI: {}", request.getRequestURI());

        // Envía la respuesta estándar 401 Unauthorized.
        // Se podría personalizar para devolver un cuerpo JSON si la API lo requiere.
        // Ejemplo: response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //          final Map<String, Object> body = new HashMap<>();
        //          body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        //          body.put("error", "Unauthorized");
        //          body.put("message", authException.getMessage());
        //          body.put("path", request.getServletPath());
        //          final ObjectMapper mapper = new ObjectMapper();
        //          mapper.writeValue(response.getOutputStream(), body);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}