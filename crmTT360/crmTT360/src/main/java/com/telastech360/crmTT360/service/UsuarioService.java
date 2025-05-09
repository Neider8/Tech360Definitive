package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.exception.InvalidDataException;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import com.telastech360.crmTT360.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Usuarios.
 * Incluye operaciones CRUD, búsquedas, cambios de estado y validaciones específicas.
 * Asegura la integridad de los datos y las reglas de negocio (ej. no eliminar último admin).
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private static final int MIN_PASSWORD_LENGTH = 8; // Longitud mínima para contraseñas

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor para inyección de dependencias.
     * @param usuarioRepository Repositorio para acceso a datos de Usuario.
     * @param rolRepository Repositorio para acceso a datos de Rol.
     * @param passwordEncoder Codificador para manejar contraseñas.
     */
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Valida la complejidad básica de una contraseña.
     * Actualmente solo verifica la longitud mínima. Podría extenderse para incluir otras reglas.
     *
     * @param password La contraseña a validar. Si es nula o vacía, no se valida (útil en actualizaciones).
     * @throws InvalidDataException si la contraseña proporcionada no cumple los requisitos de longitud.
     */
    private void validatePasswordComplexity(String password) {
        if (!StringUtils.hasText(password)) {
            // No se valida si no se provee (ej., en actualización sin cambio de contraseña)
            return;
        }
        log.debug("Validando complejidad de contraseña...");
        if (password.length() < MIN_PASSWORD_LENGTH) {
            log.warn("Intento de usar contraseña demasiado corta ({} caracteres)", password.length());
            throw new InvalidDataException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        // Añadir más reglas aquí si se desea (mayúsculas, números, especiales)
        // ej. if (!password.matches(".*[A-Z].*")) { throw new InvalidDataException("..."); }
        log.debug("Validación de complejidad de contraseña superada.");
    }

    /**
     * Registra un nuevo usuario en el sistema a partir de los datos proporcionados en un DTO.
     * Realiza validaciones de unicidad de email, existencia del rol y complejidad de contraseña.
     * Codifica la contraseña antes de guardarla. Establece el estado inicial como "ACTIVO".
     *
     * @param usuarioDto DTO {@link UsuarioRequestDTO} con los datos del usuario a crear.
     * @return La entidad {@link Usuario} creada y persistida.
     * @throws DuplicateResourceException si el email proporcionado ya está registrado.
     * @throws ResourceNotFoundException si el Rol especificado por {@code rolId} en el DTO no existe.
     * @throws InvalidDataException si la contraseña es inválida (corta) o no se proporciona.
     */
    @Transactional
    public Usuario registrarNuevoUsuario(UsuarioRequestDTO usuarioDto) {
        log.info("Iniciando registro de nuevo usuario para email: {}", usuarioDto.getEmail());

        // 1. Validar email único
        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            log.warn("Email duplicado detectado: {}", usuarioDto.getEmail());
            throw new DuplicateResourceException("El email ya está registrado: " + usuarioDto.getEmail());
        }
        log.debug("Email {} disponible.", usuarioDto.getEmail());

        // 2. Validar y obtener Rol
        Rol rol = rolRepository.findById(usuarioDto.getRolId())
                .orElseThrow(() -> {
                    log.warn("Rol con ID {} no encontrado al registrar usuario {}.", usuarioDto.getRolId(), usuarioDto.getEmail());
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDto.getRolId());
                });
        log.debug("Rol '{}' (ID: {}) encontrado para asignar.", rol.getNombre(), rol.getRolId());

        // 3. Crear entidad Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(usuarioDto.getNombre());
        nuevoUsuario.setEmail(usuarioDto.getEmail());

        // 4. Validar contraseña obligatoria y complejidad
        if (!StringUtils.hasText(usuarioDto.getPassword())) {
            log.error("Contraseña no proporcionada para el nuevo usuario {}.", usuarioDto.getEmail());
            throw new InvalidDataException("La contraseña es obligatoria para crear un nuevo usuario.");
        }
        validatePasswordComplexity(usuarioDto.getPassword()); // Lanza excepción si falla

        // 5. Codificar contraseña y asignar Rol/Estado
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(usuarioDto.getPassword()));
        log.debug("Contraseña codificada para {}.", usuarioDto.getEmail());
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstado("ACTIVO"); // Estado por defecto

        // 6. Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        log.info("Usuario {} (ID: {}) registrado exitosamente con Rol: {}", usuarioGuardado.getEmail(), usuarioGuardado.getUsuarioId(), rol.getNombre());
        return usuarioGuardado;
    }

    /**
     * Obtiene un usuario por su ID único.
     * Carga la entidad Usuario completa desde la base de datos.
     *
     * @param id El ID del usuario a buscar.
     * @return La entidad {@link Usuario} encontrada.
     * @throws ResourceNotFoundException si no se encuentra ningún usuario con el ID proporcionado.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
                });
        log.debug("Usuario encontrado: {} (ID: {})", usuario.getEmail(), id);
        return usuario;
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema, ordenados por nombre ascendente.
     *
     * @return Una lista de entidades {@link Usuario}.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodosLosUsuarios() {
        log.info("Listando todos los usuarios...");
        List<Usuario> usuarios = usuarioRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} usuarios.", usuarios.size());
        return usuarios;
    }

    /**
     * Actualiza los datos de un usuario existente a partir de un DTO.
     * Permite cambiar nombre, email, contraseña (si se provee una nueva) y rol.
     * Incluye validaciones para email único y existencia de rol, además de la regla del último admin.
     *
     * @param id ID del usuario a actualizar.
     * @param usuarioDto DTO {@link UsuarioRequestDTO} con los datos actualizados.
     * @return La entidad {@link Usuario} actualizada y persistida.
     * @throws ResourceNotFoundException si el usuario o el nuevo rol no existen.
     * @throws DuplicateResourceException si el nuevo email ya está en uso por otro usuario.
     * @throws InvalidDataException si la nueva contraseña no cumple los requisitos de complejidad.
     * @throws IllegalOperationException si se intenta modificar al último administrador de forma no permitida.
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioRequestDTO usuarioDto) {
        log.info("Iniciando actualización de usuario ID: {}", id);
        Usuario usuarioExistente = obtenerUsuarioPorId(id); // Reutiliza para obtener y validar existencia

        // Validar email si cambia
        if (!usuarioExistente.getEmail().equalsIgnoreCase(usuarioDto.getEmail())) {
            log.debug("El email ha cambiado para usuario ID {}. Verificando disponibilidad de '{}'...", id, usuarioDto.getEmail());
            if(usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
                log.warn("Intento de actualizar usuario ID {} a email duplicado: {}", id, usuarioDto.getEmail());
                throw new DuplicateResourceException("El email ya está registrado: " + usuarioDto.getEmail());
            }
            log.debug("Email '{}' disponible.", usuarioDto.getEmail());
            usuarioExistente.setEmail(usuarioDto.getEmail());
        }

        // Actualizar nombre
        usuarioExistente.setNombre(usuarioDto.getNombre());

        // Validar y actualizar contraseña SOLO si se proporciona una nueva
        if (StringUtils.hasText(usuarioDto.getPassword())) {
            log.debug("Actualizando contraseña para usuario ID {}. Validando...", id);
            validatePasswordComplexity(usuarioDto.getPassword()); // Lanza excepción si falla
            usuarioExistente.setPasswordHash(passwordEncoder.encode(usuarioDto.getPassword()));
            log.debug("Nueva contraseña codificada y asignada para usuario ID {}.", id);
        }

        // Actualizar rol con validación de último ADMIN
        actualizarRolConValidacionAdmin(usuarioExistente, usuarioDto.getRolId());

        // Guardar cambios
        Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario ID {} actualizado exitosamente.", usuarioGuardado.getUsuarioId());
        return usuarioGuardado;
    }

    /**
     * Método auxiliar privado para manejar la lógica de actualización del rol de un usuario,
     * incluyendo la validación crítica para no modificar/quitar el rol del último administrador.
     *
     * @param usuarioExistente La entidad Usuario que se está actualizando.
     * @param nuevoRolId El ID del nuevo rol a asignar. Si es null, se intentará quitar el rol actual.
     * @throws ResourceNotFoundException Si el {@code nuevoRolId} proporcionado no corresponde a un rol existente.
     * @throws IllegalOperationException Si se intenta cambiar o quitar el rol del último usuario con rol 'ADMIN'.
     */
    private void actualizarRolConValidacionAdmin(Usuario usuarioExistente, Long nuevoRolId) {
        Long rolActualId = (usuarioExistente.getRol() != null) ? usuarioExistente.getRol().getRolId() : null;
        String rolActualNombre = (usuarioExistente.getRol() != null) ? usuarioExistente.getRol().getNombre() : null;
        boolean esAdminActual = "ADMIN".equalsIgnoreCase(rolActualNombre); // Verifica si el usuario es ADMIN actualmente
        long adminCount = -1; // Contador de administradores, inicializado a -1 para control

        // Determinar si es necesario contar los administradores (solo si el usuario es admin y se intenta cambiar/quitar su rol)
        boolean necesitaConteoAdmin = esAdminActual && (nuevoRolId == null || !nuevoRolId.equals(rolActualId));

        if (necesitaConteoAdmin) {
            adminCount = usuarioRepository.countByRol_Nombre("ADMIN"); // Contar cuántos usuarios tienen el rol ADMIN
            log.debug("Usuario ID {} es ADMIN. Conteo de admins: {}. Intentando cambiar a Rol ID: {}", usuarioExistente.getUsuarioId(), adminCount, nuevoRolId);
        }

        // Lógica principal de actualización/eliminación del rol
        if (nuevoRolId != null) { // Caso 1: Se especifica un nuevo Rol ID
            if (!nuevoRolId.equals(rolActualId)) { // Solo proceder si el rol es realmente diferente
                // Validar si es el último admin intentando cambiar de rol
                if (esAdminActual && adminCount <= 1) {
                    log.warn("Intento de cambiar el rol del último administrador ID {}", usuarioExistente.getUsuarioId());
                    throw new IllegalOperationException("No se puede cambiar el rol del último administrador.");
                }
                log.debug("Intentando cambiar rol para usuario ID {} de {} a {}", usuarioExistente.getUsuarioId(), rolActualId, nuevoRolId);
                // Buscar y asignar el nuevo rol
                Rol nuevoRol = rolRepository.findById(nuevoRolId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + nuevoRolId));
                usuarioExistente.setRol(nuevoRol);
                log.debug("Rol '{}' asignado al usuario ID {}.", nuevoRol.getNombre(), usuarioExistente.getUsuarioId());
            } else {
                log.debug("El rol ID {} es el mismo que el actual, no se actualiza el rol para usuario ID {}.", nuevoRolId, usuarioExistente.getUsuarioId());
            }
        } else { // Caso 2: Se intenta quitar el rol (nuevoRolId es null)
            if (rolActualId != null) { // Solo proceder si tenía un rol asignado previamente
                // Validar si es el último admin intentando quitar su rol
                if (esAdminActual && adminCount <= 1) {
                    log.warn("Intento de quitar el rol del último administrador ID {}", usuarioExistente.getUsuarioId());
                    throw new IllegalOperationException("No se puede quitar el rol del último administrador.");
                }
                log.debug("Quitando rol al usuario ID {}.", usuarioExistente.getUsuarioId());
                usuarioExistente.setRol(null); // Quitar la asociación con el rol
            } else {
                log.debug("Usuario ID {} ya no tenía rol asignado.", usuarioExistente.getUsuarioId());
            }
        }
    }

    /**
     * Elimina un usuario por su ID.
     * Realiza una validación para prevenir la eliminación si es el único usuario con el rol 'ADMIN'.
     *
     * @param id ID del usuario a eliminar.
     * @throws ResourceNotFoundException si el usuario con el ID especificado no existe.
     * @throws IllegalOperationException si se intenta eliminar el único administrador del sistema.
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Iniciando eliminación de usuario ID: {}", id);
        Usuario usuario = obtenerUsuarioPorId(id); // Obtiene o lanza ResourceNotFoundException

        // Prevenir eliminación del último ADMIN
        if (usuario.getRol() != null && "ADMIN".equalsIgnoreCase(usuario.getRol().getNombre())) {
            long adminCount = usuarioRepository.countByRol_Nombre("ADMIN");
            log.debug("Verificando si es el último admin antes de eliminar ID {}. Count: {}", id, adminCount);
            if (adminCount <= 1) {
                log.warn("Intento de eliminar el último administrador del sistema: ID {}", id);
                throw new IllegalOperationException("No se puede eliminar el único administrador del sistema.");
            }
        }

        // Considerar dependencias adicionales antes de eliminar (ej. si es responsable de algo)
        // if (clienteRepository.existsByResponsable(usuario)) {
        //     throw new IllegalOperationException("Usuario es responsable de clientes.");
        // }

        usuarioRepository.delete(usuario);
        log.info("Usuario ID {} eliminado exitosamente.", id);
    }

    /**
     * Busca usuarios cuyo nombre o email contengan el término de búsqueda proporcionado.
     * La búsqueda no distingue entre mayúsculas y minúsculas.
     *
     * @param termino El texto a buscar en el nombre o email del usuario.
     * @return Una lista de entidades {@link Usuario} que coinciden con el término de búsqueda.
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosPorNombreOEmail(String termino) {
        log.info("Buscando usuarios por término: '{}'", termino);
        // Utiliza el método del repositorio que busca ignorando mayúsculas/minúsculas
        List<Usuario> usuarios = usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(termino, termino);
        log.debug("Búsqueda por término '{}' encontró {} usuarios.", termino, usuarios.size());
        return usuarios;
    }

    /**
     * Obtiene una lista de usuarios que pertenecen a un rol específico, identificado por su nombre.
     *
     * @param rolNombre El nombre exacto del rol a buscar (ej. "ADMIN", "OPERARIO"). Case-sensitive.
     * @return Una lista de entidades {@link Usuario} que tienen asignado el rol especificado.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorRol(String rolNombre) {
        log.info("Listando usuarios por rol: '{}'", rolNombre);
        // Utiliza el método del repositorio que busca por nombre de rol
        List<Usuario> usuarios = usuarioRepository.findByRol_Nombre(rolNombre);
        log.debug("Se encontraron {} usuarios con el rol '{}'.", usuarios.size(), rolNombre);
        return usuarios;
    }

    /**
     * Obtiene una lista de usuarios filtrados por su estado actual (ej. "ACTIVO", "INACTIVO").
     *
     * @param estado El estado por el cual filtrar (debe coincidir exactamente, case-sensitive).
     * @return Una lista de entidades {@link Usuario} que tienen el estado especificado.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorEstado(String estado) {
        log.info("Listando usuarios por estado: '{}'", estado);
        // Considerar validar el valor del estado aquí si es un conjunto finito
        // if (!"ACTIVO".equalsIgnoreCase(estado) && !"INACTIVO".equalsIgnoreCase(estado)) {
        //    log.warn("Intento de listar usuarios con estado inválido: {}", estado);
        //    return new ArrayList<>(); // o lanzar excepción
        // }
        List<Usuario> usuarios = usuarioRepository.findByEstado(estado);
        log.debug("Se encontraron {} usuarios con el estado '{}'.", usuarios.size(), estado);
        return usuarios;
    }

    /**
     * Verifica si ya existe un usuario registrado con el email proporcionado.
     *
     * @param email El email a verificar.
     * @return {@code true} si ya existe un usuario con ese email, {@code false} en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existeUsuarioConEmail(String email) {
        log.debug("Verificando existencia de email: '{}'", email);
        boolean existe = usuarioRepository.existsByEmail(email);
        log.trace("Resultado de verificación para email '{}': {}", email, existe);
        return existe;
    }

    /**
     * Cambia el estado de un usuario existente a "ACTIVO" o "INACTIVO".
     * Incluye una validación para prevenir la inactivación del último administrador activo.
     *
     * @param id ID del usuario cuyo estado se va a cambiar.
     * @param nuevoEstado El nuevo estado deseado ("ACTIVO" o "INACTIVO", case-insensitive).
     * @return La entidad {@link Usuario} con el estado actualizado.
     * @throws ResourceNotFoundException si el usuario no existe.
     * @throws IllegalArgumentException si el {@code nuevoEstado} no es "ACTIVO" ni "INACTIVO".
     * @throws IllegalOperationException si se intenta inactivar al último administrador activo.
     */
    @Transactional
    public Usuario cambiarEstadoUsuario(Long id, String nuevoEstado) {
        log.info("Intentando cambiar estado para usuario ID {} a '{}'", id, nuevoEstado);
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        String estadoUpper = nuevoEstado.toUpperCase(); // Normalizar a mayúsculas para comparación y guardado

        // Validar que el estado sea ACTIVO o INACTIVO
        if (!"ACTIVO".equals(estadoUpper) && !"INACTIVO".equals(estadoUpper)) {
            log.warn("Estado inválido proporcionado: {}", nuevoEstado);
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado + ". Debe ser 'ACTIVO' o 'INACTIVO'.");
        }

        Usuario usuario = obtenerUsuarioPorId(id); // Obtiene o lanza ResourceNotFoundException
        String estadoAnterior = usuario.getEstado(); // Guardar estado actual para logs/comparación

        // No hacer nada si el estado ya es el deseado
        if (estadoUpper.equals(estadoAnterior)) {
            log.info("Usuario ID {} ya se encuentra en estado '{}'. No se requiere cambio.", id, estadoUpper);
            return usuario;
        }

        // Prevenir inactivación del último ADMIN ACTIVO
        if ("INACTIVO".equals(estadoUpper) &&
                usuario.getRol() != null && "ADMIN".equalsIgnoreCase(usuario.getRol().getNombre())) {

            long adminCount = usuarioRepository.countByRol_Nombre("ADMIN");
            log.debug("Usuario ID {} es ADMIN. Conteo de admins: {}. Intentando cambiar estado a INACTIVO.", id, adminCount);

            // Contar admins activos explícitamente si solo hay un admin en total
            if (adminCount <= 1) {
                long activeAdminCount = usuarioRepository.findAll().stream()
                        .filter(u -> "ADMIN".equalsIgnoreCase(u.getRol() != null ? u.getRol().getNombre() : "") &&
                                "ACTIVO".equalsIgnoreCase(u.getEstado()))
                        .count();
                log.debug("Conteo de admins ACTIVOS: {}", activeAdminCount);
                if (activeAdminCount <= 1) {
                    log.warn("Intento de inactivar al último administrador activo: ID {}", id);
                    throw new IllegalOperationException("No se puede inactivar al único administrador activo del sistema.");
                }
            }
        }

        // Cambiar y guardar el estado
        usuario.setEstado(estadoUpper);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Estado de usuario ID {} cambiado de '{}' a '{}' exitosamente.", id, estadoAnterior, usuarioActualizado.getEstado());
        return usuarioActualizado;
    }
}