package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.UsuarioResponseDTO; // Puedes usar este DTO para otras operaciones de usuario
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.ResourceNotFoundException; // Importa tu excepción si la usas
import com.telastech360.crmTT360.repository.UsuarioRepository; // Necesario para obtener el ID del usuario real
import com.telastech360.crmTT360.security.auth.dto.LoginRequest;
import com.telastech360.crmTT360.security.auth.dto.JwtResponse;
import com.telastech360.crmTT360.security.jwt.JwtCore;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // Permite Cross-Origin si es necesario
@RestController
@RequestMapping("/api/auth") // Ruta base para endpoints de autenticación
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtCore jwtCore;

    @Autowired
    UsuarioRepository usuarioRepository; // Inyectar el repositorio para obtener el ID del usuario real

    // Endpoint para el inicio de sesión
    @PostMapping("/login") // O /signin según tu preferencia
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Autentica al usuario usando el AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Establece la autenticación en el contexto de seguridad de Spring
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genera el token JWT
        String jwt = jwtCore.generateToken(authentication);

        // Obtiene los detalles del usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Obtiene el usuario real de la base de datos para conseguir el ID
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException( // CORREGIDO aquí
                        "Usuario no encontrado con email: " + userDetails.getUsername()));


        // Obtiene los roles (y permisos si se mapearon como GrantedAuthorities)
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Devuelve la respuesta con el token y detalles del usuario
        return ResponseEntity.ok(new JwtResponse(jwt,
                usuario.getUsuarioId(), // Usar el ID del usuario real
                userDetails.getUsername(), // El email
                roles)); // La lista de roles/permisos
    }

    // Aquí podrías añadir otros endpoints de autenticación si los necesitas,
    // como /register (que llamaría a AuthService para crear el usuario),
    // /refresh-token, etc.

}