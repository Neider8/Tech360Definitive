package com.telastech360.crmTT360.security.services;

import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// Implementación de UserDetailsService para cargar detalles de usuario desde la base de datos
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Construir GrantedAuthorities a partir del Rol del usuario
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.getRol() != null) {
            // Mapear Rol a GrantedAuthority (convención Spring Security: ROLE_ prefijo)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase()));
            // Si mapearas permisos directamente, los añadirías aquí también
        }

        // Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPasswordHash(), // Usar el passwordHash cifrado
                usuario.getEstado() != null && usuario.getEstado().equals("ACTIVO"), // Habilitado si el estado es "ACTIVO"
                true, // Cuenta no expirada (ajusta según tu lógica si tienes campos para esto)
                true, // Credenciales no expiradas (ajusta según tu lógica)
                true, // Cuenta no bloqueada (ajusta según tu lógica)
                authorities); // Roles (y permisos si se mapean)
    }
}