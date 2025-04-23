package com.telastech360.crmTT360.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // Importa @Component si no estaba
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Importaciones para logging manual
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Filtro que intercepta las solicitudes para validar el JWT
@Component // Asegúrate de que esté anotado como componente
// Elimina @Slf4j si estaba aquí
public class JwtRequestFilter extends OncePerRequestFilter {

    // Inicialización manual del logger
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtCore jwtCore;

    @Autowired
    private UserDetailsService userDetailsService;

    // Elimina private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class); si estaba aquí

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Procesando solicitud en JwtRequestFilter para path: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request); // Extrae el token del encabezado
            log.debug("Token JWT extraído: {}", jwt != null ? "Presente" : "Ausente");

            if (jwt != null && jwtCore.validateJwtToken(jwt)) { // Valida el token
                String username = jwtCore.getUserNameFromJwtToken(jwt); // Obtiene el email (subject)
                log.debug("Nombre de usuario '{}' extraído del token.", username);

                // Solo cargar UserDetails si el contexto de seguridad está vacío
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Contexto de seguridad nulo para usuario '{}', intentando autenticar.", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Carga los detalles del usuario
                    log.debug("UserDetailsService cargó detalles para usuario: {}", username);

                    // Re-validar el token con UserDetails (esto lo hace el JwtCore.validateJwtToken)
                    // if (jwtCore.validateJwtToken(jwt, userDetails)) { // Esta validación ya se hizo arriba
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, // La contraseña ya no es necesaria aquí
                                    userDetails.getAuthorities()); // Carga los roles/permisos

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication); // Establece la autenticación en el contexto de seguridad
                    log.info("Usuario '{}' autenticado exitosamente.", username);
                } else {
                    log.debug("Contexto de seguridad ya poblado o nombre de usuario nulo para path: {}", request.getRequestURI());
                }

            } else {
                if (jwt != null) {
                    log.debug("Token JWT presente pero inválido o expirado.");
                }
            }
        } catch (Exception e) {
            // Captura cualquier excepción durante el proceso del filtro JWT
            log.error("Error durante el procesamiento del filtro JWT: {}", e.getMessage(), e);
            // No lanzar la excepción aquí, permite que otros filtros o manejadores de Spring la manejen
        }

        filterChain.doFilter(request, response);
    }

    // Método para extraer el JWT del encabezado Authorization (Bearer token)
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            log.trace("Encabezado Authorization encontrado.");
            return headerAuth.substring(7); // Elimina "Bearer " para obtener el token
        }

        log.trace("Encabezado Authorization 'Bearer Token' no encontrado o vacío.");
        return null;
    }
}