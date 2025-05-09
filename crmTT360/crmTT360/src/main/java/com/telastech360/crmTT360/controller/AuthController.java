// src/main/java/com/telastech360/crmTT360/controller/AuthController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.UsuarioResponseDTO; // No se usa directamente aquí, pero podría ser útil en el futuro
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import com.telastech360.crmTT360.security.auth.dto.LoginRequest;
import com.telastech360.crmTT360.security.auth.dto.JwtResponse;
import com.telastech360.crmTT360.security.jwt.JwtCore;
// Removido import no usado: import com.telastech360.crmTT360.service.AuthService;
import jakarta.validation.Valid; // Importar @Valid
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// Importar la anotación RequestBody de Swagger, no la de Spring Web (que ya se usa en el parámetro)
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para gestionar las operaciones de autenticación.
 * Incluye el endpoint para iniciar sesión y generar un token JWT.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones de Autenticación y Autorización (Login)")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtCore jwtCore;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Autentica a un usuario con su email y contraseña.
     * Si la autenticación es exitosa, genera y devuelve un token JWT junto con
     * información básica del usuario (ID, email, roles).
     *
     * @param loginRequest DTO {@link LoginRequest} que contiene el email y la contraseña del usuario.
     * @return ResponseEntity con {@link JwtResponse} que contiene el token y datos del usuario (HTTP 200 OK),
     * o una respuesta de error (ej. HTTP 401 Unauthorized) si la autenticación falla.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión de usuario", description = "Autentica un usuario usando email y contraseña, devuelve un token JWT si es exitoso.")
    @RequestBody(description = "Credenciales de inicio de sesión (email y contraseña)", required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class)))
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token JWT y datos del usuario devueltos.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas (Unauthorized)", content = @Content)
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (formato email, contraseña corta, etc.)", content = @Content) // Añadido 400
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado (Error interno post-autenticación)", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la autenticación", content = @Content)
    public ResponseEntity<?> authenticateUser(
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest // Usar @Valid de jakarta.validation y @RequestBody de Spring
    ) {
        log.info("Intento de autenticación para el usuario: {}", loginRequest.getEmail());
        Authentication authentication = null; // Definir fuera del try para usar en logs de error
        String jwt = null; // Definir fuera del try para logs
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            log.debug("Autenticación exitosa en AuthenticationManager para: {}", loginRequest.getEmail());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar Token
            jwt = jwtCore.generateToken(authentication);
            // Log movido a después de la creación del JwtResponse para confirmar

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Obtener entidad Usuario para ID y otros datos si es necesario
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> {
                        log.error("Inconsistencia: Usuario {} autenticado pero no encontrado en repositorio.", userDetails.getUsername());
                        return new ResourceNotFoundException("Usuario no encontrado después de la autenticación: " + userDetails.getUsername());
                    });
            log.debug("Entidad Usuario encontrada (ID: {}) para: {}", usuario.getUsuarioId(), loginRequest.getEmail());

            // Obtener Roles/Autoridades
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            log.debug("Roles/Autoridades para {}: {}", loginRequest.getEmail(), roles);

            // ---- Logs de Depuración Añadidos ----
            log.debug("Token generado para JWT Response: [{}]", jwt); // Verifica que 'jwt' no sea null o vacío
            JwtResponse jwtResponse = new JwtResponse(jwt,
                    usuario.getUsuarioId(),
                    userDetails.getUsername(),
                    roles);
            // Usar getters para asegurar que el objeto se creó bien
            log.debug("Objeto JwtResponse creado: Token=[{}], Email=[{}], ID=[{}]", jwtResponse.getAccessToken(), jwtResponse.getEmail(), jwtResponse.getId());
            // -------------------------------------

            log.info("Autenticación completada y token JWT devuelto para: {}", loginRequest.getEmail());
            return ResponseEntity.ok(jwtResponse); // Devolver respuesta OK

        } catch (BadCredentialsException e) {
            log.warn("Fallo de autenticación para {}: Credenciales inválidas.", loginRequest.getEmail());
            // Devolver 401 Unauthorized específicamente para credenciales incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Credenciales inválidas");
        } catch (ResourceNotFoundException e) {
            // Este error indica un problema interno si el usuario se autentica pero no se encuentra
            log.error("Error interno: Usuario no encontrado post-autenticación para {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Usuario no encontrado después de autenticar.");
        } catch (Exception e) {
            // Captura cualquier otro error inesperado durante la autenticación
            log.error("Error inesperado durante la autenticación para {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno durante la autenticación: " + e.getMessage());
        }
    }
}