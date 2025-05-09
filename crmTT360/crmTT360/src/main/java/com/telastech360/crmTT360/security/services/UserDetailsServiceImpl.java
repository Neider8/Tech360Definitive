// src/main/java/com/telastech360/crmTT360/security/services/UserDetailsServiceImpl.java
package com.telastech360.crmTT360.security.services;

import com.telastech360.crmTT360.entity.Permiso; // <-- Añadir import
import com.telastech360.crmTT360.entity.Rol; // <-- Añadir import
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.hibernate.Hibernate; // <-- Añadir import (opcional, para inicialización explícita)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection; // <-- Añadir import
import java.util.List;
import java.util.Set; // <-- Añadir import
import java.util.stream.Collectors;

/**
 * Implementación personalizada de {@link UserDetailsService} de Spring Security.
 * Se encarga de cargar los detalles específicos de un usuario (incluyendo contraseña cifrada
 * y sus roles/autoridades) desde la base de datos, utilizando el email como identificador (username).
 * Es fundamental para que Spring Security pueda autenticar usuarios y verificar sus autorizaciones.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    /**
     * Localiza al usuario basándose en su nombre de usuario (email en este caso).
     * Carga el Rol y los Permisos asociados como GrantedAuthorities.
     *
     * @param email El email (actuando como username) del usuario a buscar.
     * @return Un objeto {@link UserDetails} completamente poblado si el usuario es encontrado.
     * @throws UsernameNotFoundException Si el usuario no pudo ser encontrado con el email proporcionado.
     */
    @Override
    @Transactional // <-- Importante para la carga LAZY de permisos
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Intentando cargar usuario por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado en la base de datos con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        log.debug("Usuario encontrado: ID={}, Email={}, Estado={}", usuario.getUsuarioId(), usuario.getEmail(), usuario.getEstado());

        // Construir la lista de GrantedAuthorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        Rol rol = usuario.getRol();

        if (rol != null) {
            // 1. Añadir el Rol principal como autoridad
            String roleName = "ROLE_" + rol.getNombre().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.debug("Rol asignado a {}: {}", email, roleName);

            // --- INICIO DE MODIFICACIÓN ---
            // 2. Añadir los Permisos del Rol como autoridades
            Set<Permiso> permisos = rol.getPermisos();

            // Consideración para carga LAZY:
            // Si la relación Rol -> Permisos es FetchType.LAZY, necesitamos asegurar
            // que la colección 'permisos' se inicialice dentro de esta transacción.
            // Acceder a la colección (ej. size()) o usar Hibernate.initialize() lo fuerza.
            // Descomenta una de las siguientes líneas si es LAZY:
            // if (permisos != null) permisos.size(); // Acceso simple para inicializar
            // Hibernate.initialize(permisos); // Inicialización explícita de Hibernate

            if (permisos != null && !permisos.isEmpty()) {
                log.debug("Inicializando y agregando {} permisos para el rol '{}'", permisos.size(), rol.getNombre());
                permisos.forEach(permiso -> {
                    // Usar el nombre del permiso como autoridad.
                    // Puedes añadir un prefijo si lo deseas (ej. "PERM_"), pero no es obligatorio.
                    authorities.add(new SimpleGrantedAuthority(permiso.getNombre().toUpperCase()));
                    log.trace("Permiso añadido para {}: {}", email, permiso.getNombre().toUpperCase());
                });
            } else {
                log.debug("El rol '{}' del usuario {} no tiene permisos asociados.", rol.getNombre(), email);
            }
            // --- FIN DE MODIFICACIÓN ---

        } else {
            log.warn("El usuario {} no tiene un rol asignado.", email);
        }

        // Determinar si la cuenta está habilitada
        boolean enabled = usuario.getEstado() != null && "ACTIVO".equalsIgnoreCase(usuario.getEstado());
        log.debug("Estado 'enabled' para {}: {}", email, enabled);

        // Crear y devolver UserDetails
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                enabled,
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities // Lista que ahora contiene Rol y Permisos
        );
    }
}