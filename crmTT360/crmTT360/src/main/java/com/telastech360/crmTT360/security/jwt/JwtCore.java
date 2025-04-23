package com.telastech360.crmTT360.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders; // Correct import for Decoders
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Componente para generar, parsear y validar JWT
@Component
public class JwtCore {

    private static final Logger logger = LoggerFactory.getLogger(JwtCore.class);

    // Clave secreta para firmar los tokens (CAMBIAR ESTO Y MOVER A application.properties)
    @Value("${crmtt360.app.jwtSecret:estaEsUnaClaveSecretaMuyLargaYSeguraParaJWTtokenscrmtt360}") // Usar valor de properties o default
    private String jwtSecret;

    // Tiempo de vida del token en milisegundos (CAMBIAR ESTO Y MOVER A application.properties)
    @Value("${crmtt360.app.jwtExpirationMs:86400000}") // 24 horas por defecto
    private int jwtExpirationMs;

    // Genera un token a partir de la autenticación del usuario
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // El subject es el email del usuario
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtiene el subject (email del usuario) desde el token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Obtiene la clave de firma a partir del secreto
    private Key key() {
        // Corregido: Usar Decoders.BASE64URL.decode()
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    // Valida el token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }

        return false;
    }
}