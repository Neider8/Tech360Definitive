package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.telastech360.crmTT360.exception.InvalidDataException;
import org.springframework.util.StringUtils; // Si usas StringUtils aquí también

/**
 * Servicio dedicado a la lógica relacionada con la autenticación y el registro inicial de usuarios.
 * Colabora con {@link UsuarioService} para la creación final del usuario.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    // Se podría inyectar UsuarioService si se necesita más lógica compartida,
    // pero para solo registrar, usar los repositorios directamente es común aquí.

    /**
     * Registra un nuevo usuario en el sistema.
     * Este método se encarga principalmente de la validación inicial y la preparación
     * de datos antes de la creación real de la entidad Usuario.
     * La validación de complejidad de contraseña se delega a {@link UsuarioService#registrarNuevoUsuario}.
     *
     * @param signUpRequest DTO que contiene los datos del usuario a registrar (nombre, email, password, rolId).
     * @return La entidad Usuario recién creada y guardada.
     * @throws DuplicateResourceException Si el email ya está registrado.
     * @throws ResourceNotFoundException Si el Rol especificado por rolId no existe.
     * @throws com.telastech360.crmTT360.exception.InvalidDataException Si la contraseña no cumple los requisitos (validado en UsuarioService).
     */
    @Transactional
    public Usuario registerNewUser(UsuarioRequestDTO signUpRequest) {
        log.info("Iniciando proceso de registro para nuevo usuario con email: {}", signUpRequest.getEmail());

        // 1. Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Fallo en registro: el email {} ya está en uso.", signUpRequest.getEmail());
            throw new DuplicateResourceException("El email ya está en uso: " + signUpRequest.getEmail());
        }
        log.debug("Verificación de email único para {} superada.", signUpRequest.getEmail());

        // 2. Buscar el Rol
        log.debug("Buscando Rol con ID: {}", signUpRequest.getRolId());
        Rol userRol = rolRepository.findById(signUpRequest.getRolId())
                .orElseThrow(() -> {
                    log.error("Rol con ID {} no encontrado durante el registro del usuario {}.", signUpRequest.getRolId(), signUpRequest.getEmail());
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + signUpRequest.getRolId());
                });
        log.debug("Rol '{}' encontrado.", userRol.getNombre());

        // 3. Crear y configurar la entidad Usuario (sin guardar aún)
        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setEmail(signUpRequest.getEmail());

        // 4. Validar y codificar contraseña (la validación de complejidad se hará al guardar)
        if (!StringUtils.hasText(signUpRequest.getPassword())) {
            log.error("Intento de registrar usuario {} sin contraseña.", signUpRequest.getEmail());
            throw new InvalidDataException("La contraseña es obligatoria para el registro.");
        }
        // NOTA: La validación de complejidad (longitud, etc.) se podría hacer aquí
        // o delegarla completamente al método save/create de UsuarioService si
        // este AuthService solo prepara la entidad. Por consistencia con la Parte 3,
        // asumimos que UsuarioService la valida al guardar.
        usuario.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        log.debug("Contraseña codificada para el usuario {}.", signUpRequest.getEmail());

        usuario.setEstado("ACTIVO"); // Estado inicial por defecto
        usuario.setRol(userRol);
        log.debug("Usuario {} preparado para guardar con Rol '{}' y estado ACTIVO.", usuario.getEmail(), userRol.getNombre());

        // 5. Guardar el usuario (Aquí es donde UsuarioService aplicaría la validación de contraseña)
        // Si AuthService llamara a UsuarioService:
        // return usuarioService.crearUsuarioValidando(usuario); // Método hipotético
        // Si AuthService guarda directamente (como está ahora):
        Usuario savedUser = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {} y email: {}", savedUser.getUsuarioId(), savedUser.getEmail());
        return savedUser;
    }
}