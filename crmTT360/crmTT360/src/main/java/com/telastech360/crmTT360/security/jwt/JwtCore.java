// src/main/java/com/telastech360/crmTT360/security/jwt/JwtCore.java
package com.telastech360.crmTT360.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders; // Importación correcta para Decoders
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Importar específicamente para manejo de firma inválida
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Componente central para la generación, parseo y validación de JSON Web Tokens (JWT).
 * Utiliza una clave secreta y un tiempo de expiración configurables desde las propiedades de la aplicación.
 */
@Component
public class JwtCore {

    private static final Logger logger = LoggerFactory.getLogger(JwtCore.class);

    // Clave secreta leída desde application.properties o variable de entorno
    @Value("${crmtt360.app.jwtSecret:estaEsUnaClaveSecretaMuyLargaYSeguraParaJWTtokenscrmtt360}") // Default seguro si no se configura
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos leído desde application.properties o variable de entorno
    @Value("${crmtt360.app.jwtExpirationMs:86400000}") // 86400000 ms = 24 horas por defecto
    private int jwtExpirationMs;

    /**
     * Genera un token JWT para un usuario autenticado.
     * El token contiene el email del usuario como 'subject', la fecha de emisión y la fecha de expiración.
     * Está firmado usando el algoritmo HS256 con la clave secreta configurada.
     *
     * @param authentication El objeto Authentication de Spring Security que contiene los detalles del usuario autenticado.
     * @return Un String que representa el token JWT compacto.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        logger.debug("Generando token JWT para usuario: {}", userPrincipal.getUsername());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Usar email como subject
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS256) // Firmar con HS256
                .compact();
        logger.trace("Token generado: {}", token); // Loguear token solo en trace por seguridad
        return token;
    }

    /**
     * Extrae el nombre de usuario (email) del 'subject' de un token JWT.
     * Verifica la firma del token usando la clave secreta antes de extraer la información.
     *
     * @param token El token JWT compacto como String.
     * @return El nombre de usuario (email) contenido en el token.
     * @throws io.jsonwebtoken.JwtException Si el token no puede ser parseado o verificado (incluye expiración, firma inválida, etc.).
     */
    public String getUserNameFromJwtToken(String token) {
        logger.trace("Extrayendo username del token JWT...");
        String username = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        logger.trace("Username extraído: {}", username);
        return username;
    }

    /**
     * Genera la clave de firma ({@link Key}) utilizada para firmar y verificar tokens JWT.
     * Decodifica la clave secreta configurada (que se espera esté en formato Base64URL)
     * usando el algoritmo HMAC-SHA adecuado (determinado por el tamaño de la clave resultante).
     *
     * @return Un objeto {@link Key} para usar con el algoritmo HMAC-SHA.
     */
    private Key key() {
        // Decodifica la clave secreta desde Base64URL
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecret);
        // Crea la clave segura para HMAC-SHA (usará HS256, HS384 o HS512 según longitud de keyBytes)
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valida la integridad y vigencia de un token JWT.
     * Verifica la firma usando la clave secreta, comprueba si ha expirado,
     * si el formato es correcto y si es soportado.
     * Registra logs detallados para cada tipo de error de validación.
     *
     * @param authToken El token JWT compacto como String a validar.
     * @return {@code true} si el token es válido en todos los aspectos, {@code false} en caso contrario.
     */
    public boolean validateJwtToken(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            logger.warn("Intento de validar token JWT nulo o vacío.");
            return false;
        }
        try {
            logger.trace("Validando token JWT...");
            // Intenta parsear y verificar el token. Si alguna verificación falla, lanza una excepción específica.
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            logger.trace("Token JWT validado exitosamente.");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido (mal formado): {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT ha expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Esto puede ocurrir si el token string es inválido antes de intentar parsearlo
            logger.error("Argumento inválido al validar token JWT (ej. claims vacíos): {}", e.getMessage());
        } catch (SignatureException e) {
            // Esta es la excepción específica para firma inválida
            logger.error("Firma del token JWT inválida: {}", e.getMessage());
        } catch (Exception e) {
            // Captura genérica para cualquier otro error inesperado durante la validación
            logger.error("Error inesperado durante la validación del token JWT: {}", e.getMessage(), e);
        }

        // Si alguna excepción fue capturada, el token no es válido
        return false;
    }
}