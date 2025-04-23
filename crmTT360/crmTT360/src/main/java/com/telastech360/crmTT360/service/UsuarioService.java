package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import com.telastech360.crmTT360.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Importaciones para logging manual
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Elimina @Slf4j si estaba aquí
@Service
public class UsuarioService {

    // Inicialización manual del logger
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        log.info("Iniciando operación de servicio: crear usuario con email: {}", usuario.getEmail());
        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Intento de crear usuario con email duplicado: {}", usuario.getEmail());
            throw new DuplicateResourceException("El email ya está registrado: " + usuario.getEmail());
        }

        // Codificar contraseña
        // Asegúrate de que getPasswordHash() existe en tu entidad Usuario y no estás usando getPassword()
        String rawPassword = usuario.getPasswordHash(); // Obtén la contraseña sin codificar
        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        log.debug("Contraseña codificada para el usuario {}.", usuario.getEmail());


        // Validar y asignar rol si viene en el request
        if (usuario.getRol() != null && usuario.getRol().getRolId() != null) {
            log.debug("Buscando rol con ID {} para asignar al usuario {}.", usuario.getRol().getRolId(), usuario.getEmail());
            Rol rol = rolRepository.findById(usuario.getRol().getRolId())
                    .orElseThrow(() -> {
                        log.warn("Rol con ID {} no encontrado al crear usuario {}.", usuario.getRol().getRolId(), usuario.getEmail());
                        return new ResourceNotFoundException("Rol no encontrado con ID: " + usuario.getRol().getRolId());
                    });
            usuario.setRol(rol);
            log.debug("Rol '{}' asignado al usuario {}.", rol.getNombre(), usuario.getEmail());
        } else {
            log.debug("No se especificó rol para el usuario {}.", usuario.getEmail());
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        // CORRECCIÓN: Usar getUsuarioId() en lugar de getId()
        log.info("Operación de servicio completada: Usuario con ID {} creado exitosamente.", nuevoUsuario.getUsuarioId());
        return nuevoUsuario;
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        log.info("Iniciando operación de servicio: obtener usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
                });
        // CORRECCIÓN: Usar getUsuarioId() en lugar de getId() si necesitas loggear el ID del objeto recuperado
        log.debug("Operación de servicio completada: Usuario con ID {} encontrado ({}).", id, usuario.getEmail());
        return usuario;
    }

    public List<Usuario> listarTodosLosUsuarios() {
        log.info("Iniciando operación de servicio: listar todos los usuarios.");
        List<Usuario> usuarios = usuarioRepository.findAllByOrderByNombreAsc();
        log.debug("Operación de servicio completada: {} usuarios encontrados.", usuarios.size());
        return usuarios;
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        log.info("Iniciando operación de servicio: actualizar usuario con ID: {}", id);
        Usuario usuarioExistente = obtenerUsuarioPorId(id); // Reutiliza el método que verifica existencia (ya loggea)
        log.debug("Usuario existente con ID {} encontrado para actualizar.", id);

        // Validar email único si cambia
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
            log.warn("Intento de actualizar usuario ID {} a email duplicado: {}", id, usuarioActualizado.getEmail());
            throw new DuplicateResourceException("El email ya está registrado: " + usuarioActualizado.getEmail());
        }
        log.debug("Email actualizado para usuario ID {}: '{}'.", id, usuarioActualizado.getEmail());


        // Actualizar campos básicos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setEstado(usuarioActualizado.getEstado());
        log.debug("Campos básicos (nombre, email, estado) actualizados para usuario ID {}.", id);

        // Actualizar contraseña solo si se proporciona una nueva
        // Asegúrate de que getPasswordHash() existe en tu entidad Usuario y no estás usando getPassword()
        if (usuarioActualizado.getPasswordHash() != null && !usuarioActualizado.getPasswordHash().isEmpty()) {
            log.debug("Actualizando contraseña para usuario ID {}.", id);
            usuarioExistente.setPasswordHash(passwordEncoder.encode(usuarioActualizado.getPasswordHash()));
        } else {
            log.debug("No se proporcionó nueva contraseña para usuario ID {}.", id);
        }


        // Actualizar rol si viene en el request
        // Ten cuidado si el DTO/entidad permite anular el rol pasándole null.
        if (usuarioActualizado.getRol() != null && usuarioActualizado.getRol().getRolId() != null) {
            log.debug("Buscando rol con ID {} para actualizar en usuario ID {}.", usuarioActualizado.getRol().getRolId(), id);
            Rol rol = rolRepository.findById(usuarioActualizado.getRol().getRolId())
                    .orElseThrow(() -> {
                        log.warn("Rol con ID {} no encontrado al actualizar usuario ID {}.", usuarioActualizado.getRol().getRolId(), id);
                        return new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioActualizado.getRol().getRolId());
                    });
            usuarioExistente.setRol(rol);
            log.debug("Rol '{}' asignado al usuario ID {}.", rol.getNombre(), id);
        } else if (usuarioActualizado.getRol() == null && usuarioExistente.getRol() != null) {
            log.debug("Eliminando rol del usuario ID {}.", id);
            usuarioExistente.setRol(null);
        } else {
            log.debug("El rol no se modificó para usuario ID {}.", id);
        }


        Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
        // CORRECCIÓN: Usar getUsuarioId() en lugar de getId()
        log.info("Operación de servicio completada: Usuario con ID {} actualizado exitosamente.", usuarioGuardado.getUsuarioId());
        return usuarioGuardado;
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Iniciando operación de servicio: eliminar usuario con ID: {}", id);
        Usuario usuario = obtenerUsuarioPorId(id); // Verifica que existe antes de eliminar (ya loggea)
        log.debug("Usuario existente con ID {} encontrado para eliminar ({}).", id, usuario.getEmail());

        // Validar que no sea el último administrador
        if (usuario.getRol() != null && "ADMIN".equals(usuario.getRol().getNombre())) {
            long adminCount = usuarioRepository.countByRol_Nombre("ADMIN");
            log.debug("Contando administradores. Total: {}", adminCount);
            if (adminCount <= 1) {
                log.warn("Intento de eliminar el último administrador del sistema: ID {}", id);
                throw new IllegalOperationException("No se puede eliminar el único administrador del sistema");
            }
        }

        usuarioRepository.delete(usuario);
        log.info("Operación de servicio completada: Usuario con ID {} eliminado exitosamente.", id);
    }

    // ========== MÉTODOS ADICIONALES ========== //

    public List<Usuario> buscarUsuariosPorNombreOEmail(String termino) {
        log.info("Iniciando operación de servicio: buscar usuarios por término: '{}'.", termino);
        // CORRECCIÓN: Asegúrate de que el método en el repositorio se llama así
        List<Usuario> usuarios = usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(termino, termino); // Asumo este método o similar
        log.debug("Operación de servicio completada: {} usuarios encontrados para el término '{}'.", usuarios.size(), termino);
        return usuarios;
    }

    public List<Usuario> listarUsuariosPorRol(String rolNombre) {
        log.info("Iniciando operación de servicio: listar usuarios por rol: '{}'.", rolNombre);
        List<Usuario> usuarios = usuarioRepository.findByRol_Nombre(rolNombre); // Asumo este método
        log.debug("Operación de servicio completada: {} usuarios encontrados con el rol '{}'.", usuarios.size(), rolNombre);
        return usuarios;
    }

    public List<Usuario> listarUsuariosPorEstado(String estado) {
        log.info("Iniciando operación de servicio: listar usuarios por estado: '{}'.", estado);
        List<Usuario> usuarios = usuarioRepository.findByEstado(estado); // Asumo este método
        log.debug("Operación de servicio completada: {} usuarios encontrados con el estado '{}'.", usuarios.size(), estado);
        return usuarios;
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioConEmail(String email) {
        log.info("Verificando existencia de usuario con email: '{}'.", email);
        boolean existe = usuarioRepository.existsByEmail(email);
        log.debug("Resultado de verificación de email '{}': {}", email, existe);
        return existe;
    }

    @Transactional
    public Usuario cambiarEstadoUsuario(Long id, String nuevoEstado) {
        log.info("Iniciando operación de servicio: cambiar estado de usuario ID {} a '{}'.", id, nuevoEstado);
        Usuario usuario = obtenerUsuarioPorId(id); // Ya loggea
        String estadoAnterior = usuario.getEstado();
        usuario.setEstado(nuevoEstado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        // CORRECCIÓN: Usar getUsuarioId() en lugar de getId()
        log.info("Operación de servicio completada: Estado de usuario ID {} cambiado de '{}' a '{}'.", usuarioActualizado.getUsuarioId(), estadoAnterior, nuevoEstado);
        return usuarioActualizado;
    }
}