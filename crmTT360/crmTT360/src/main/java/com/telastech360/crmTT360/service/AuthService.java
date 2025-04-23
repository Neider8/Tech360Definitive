package com.telastech360.crmTT360.service; // Este AuthService está en el paquete 'service'

import com.telastech360.crmTT360.dto.UsuarioRequestDTO; // DTO de solicitud para crear usuario
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException; // Importa tu excepción si la usas
import com.telastech360.crmTT360.exception.ResourceNotFoundException; // Importa tu excepción si la usas
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Importaciones para logging manual
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Elimina @Slf4j si estaba aquí
@Service
public class AuthService {

    // Inicialización manual del logger
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository; // Para obtener el Rol por ID

    @Autowired
    PasswordEncoder encoder; // El PasswordEncoder configurado en SecurityConfig

    // Método para registrar un nuevo usuario
    @Transactional
    public Usuario registerNewUser(UsuarioRequestDTO signUpRequest) {
        log.info("Iniciando registro de nuevo usuario para email: {}", signUpRequest.getEmail());

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Fallo en registro de usuario: el email {} ya está en uso.", signUpRequest.getEmail());
            throw new DuplicateResourceException("El email ya está en uso: " + signUpRequest.getEmail());
        }
        log.debug("Email {} no encontrado, procediendo con el registro.", signUpRequest.getEmail());


        // Crear la nueva entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setEmail(signUpRequest.getEmail());
        // Cifrar la contraseña ANTES de guardarla
        usuario.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        log.debug("Contraseña codificada para el nuevo usuario {}.", signUpRequest.getEmail());

        usuario.setEstado("ACTIVO"); // Establecer estado inicial (asegúrate que "ACTIVO" sea un estado válido)
        log.debug("Estado inicial 'ACTIVO' asignado al nuevo usuario {}.", signUpRequest.getEmail());


        // Asignar el Rol
        log.debug("Buscando Rol con ID {} para asignar al nuevo usuario {}.", signUpRequest.getRolId(), signUpRequest.getEmail());
        Rol userRol = rolRepository.findById(signUpRequest.getRolId())
                .orElseThrow(() -> {
                    log.warn("Rol con ID {} no encontrado para asignar al nuevo usuario {}.", signUpRequest.getRolId(), signUpRequest.getEmail());
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + signUpRequest.getRolId());
                });
        usuario.setRol(userRol);
        log.debug("Rol '{}' asignado al nuevo usuario {}.", userRol.getNombre(), signUpRequest.getEmail());


        // Guardar el usuario en la base de datos
        Usuario savedUser = usuarioRepository.save(usuario);
        // CORRECCIÓN: Usar getUsuarioId() en lugar de getId()
        log.info("Registro de usuario exitoso para ID: {} con email: {}", savedUser.getUsuarioId(), savedUser.getEmail());

        return savedUser; // Devolver la entidad guardada
    }

    // Puedes añadir logging a otros métodos relacionados con autenticación/autorización si los tienes aquí
}