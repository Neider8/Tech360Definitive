// src/main/java/com/telastech360/crmTT360/security/jwt/JwtRequestFilter.java
package com.telastech360.crmTT360.security.jwt;

import com.telastech360.crmTT360.security.services.UserDetailsServiceImpl; // Importar si se usa directamente UserDetailsService
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; // Importar NonNull para claridad en parámetros
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar interfaz estándar
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter; // Importar clase base correcta

import java.io.IOException;

/**
 * Filtro de Spring Security que intercepta cada solicitud HTTP entrante una única vez.
 * Su propósito es:
 * 1. Extraer el token JWT del encabezado 'Authorization'.
 * 2. Validar el token usando {@link JwtCore}.
 * 3. Si el token es válido, extraer el nombre de usuario (email).
 * 4. Cargar los detalles del usuario ({@link UserDetails}) usando {@link UserDetailsService}.
 * 5. Crear un objeto de autenticación {@link UsernamePasswordAuthenticationToken}.
 * 6. Establecer la autenticación en el contexto de seguridad de Spring ({@link SecurityContextHolder}).
 * Esto permite que las subsiguientes partes de la aplicación (ej. controladores con @PreAuthorize)
 * reconozcan al usuario como autenticado y autorizado.
 */
// @Component // No se anota como Component aquí; se define como Bean en SecurityConfig
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtCore jwtCore;

    @Autowired
    private UserDetailsService userDetailsService; // Inyectar la interfaz estándar

    /**
     * Lógica principal del filtro que se ejecuta para cada solicitud.
     * Intenta parsear y validar el JWT, y si es exitoso, establece la autenticación del usuario.
     *
     * @param request La solicitud HTTP entrante. Nunca es null.
     * @param response La respuesta HTTP. Nunca es null.
     * @param filterChain La cadena de filtros para continuar el procesamiento. Nunca es null.
     * @throws ServletException Si ocurre un error del servlet durante el procesamiento.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.trace("Procesando solicitud en JwtRequestFilter para URI: {}", request.getRequestURI());
        String jwt = null;
        String username = null;

        try {
            // 1. Extraer JWT
            jwt = parseJwt(request);
            log.trace("Token JWT extraído: {}", (jwt != null ? "[Presente]" : "[Ausente]"));

            // 2. Validar JWT y 3. Extraer Username
            if (jwt != null && jwtCore.validateJwtToken(jwt)) {
                username = jwtCore.getUserNameFromJwtToken(jwt);
                log.debug("Token válido. Username extraído: {}", username);

                // 4. Cargar UserDetails si no hay autenticación previa en el contexto
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Contexto de seguridad vacío para {}. Cargando UserDetails...", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 5. Crear Token de Autenticación de Spring
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, // Principal
                                    null, // Credenciales (no necesarias post-validación JWT)
                                    userDetails.getAuthorities()); // Roles/Permisos

                    // Añadir detalles de la solicitud web a la autenticación
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. Establecer Autenticación en el Contexto de Seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Usuario '{}' autenticado exitosamente vía JWT y contexto establecido.", username);
                } else {
                    // Loguear por qué no se estableció la autenticación
                    if (username == null) log.warn("Username nulo extraído del token JWT válido.");
                    else log.trace("Contexto de seguridad ya contiene autenticación para usuario {}. No se actualiza.", username);
                }
            } else {
                // Loguear la razón por la que el token no fue procesado (si existía)
                if (jwt != null) log.debug("Token JWT presente pero inválido/expirado.");
                // else log.trace("No se encontró token JWT en la cabecera Authorization."); // Logueado en parseJwt
            }
        } catch (UsernameNotFoundException e) {
            // Captura específica si UserDetailsService no encuentra al usuario del token
            log.error("Error en filtro JWT: Usuario '{}' (del token) no encontrado por UserDetailsService: {}", username, e.getMessage());
            // Limpiar contexto por si acaso
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // Captura genérica para otros errores durante el proceso del token o carga de usuario
            log.error("Error durante el procesamiento del filtro JWT para usuario '{}': {}", username, e.getMessage(), e);
            // Limpiar contexto en caso de error
            SecurityContextHolder.clearContext();
        }

        // Continuar con el resto de la cadena de filtros, independientemente del resultado de la autenticación JWT
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT puro (sin el prefijo "Bearer ") del encabezado 'Authorization'.
     *
     * @param request La solicitud HTTP de la cual extraer el encabezado.
     * @return El string del token JWT si se encuentra y tiene el formato "Bearer <token>",
     * o {@code null} si el encabezado no existe, está vacío o no tiene el prefijo correcto.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            log.trace("Encabezado 'Authorization: Bearer ...' encontrado.");
            // Retorna el token quitando "Bearer " (7 caracteres)
            return headerAuth.substring(7);
        }

        log.trace("Encabezado 'Authorization: Bearer ...' no encontrado o con formato incorrecto.");
        return null; // No se encontró o no tiene el formato esperado
    }
}