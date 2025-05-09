// src/main/java/com/telastech360/crmTT360/service/RolService.java
package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.ResourceInUseException; // Importación para 409 Conflict
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Añadir si se usa Optional.ifPresent

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Roles de usuario.
 */
@Service
public class RolService {

    private static final Logger log = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyección de dependencias.
     * @param rolRepository Repositorio para acceso a datos de Rol.
     * @param usuarioRepository Repositorio para verificar si hay usuarios con un rol.
     */
    @Autowired
    public RolService(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene un rol por su ID.
     * @param id ID del rol a buscar.
     * @return La entidad Rol encontrada.
     * @throws ResourceNotFoundException Si el rol no existe.
     */
    @Transactional(readOnly = true)
    public Rol obtenerRolPorId(Long id) {
        log.info("Buscando rol por ID: {}", id);
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rol con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + id);
                });
        log.debug("Rol encontrado: {} (ID: {})", rol.getNombre(), id);
        return rol;
    }

    /**
     * Lista todos los roles disponibles en el sistema, ordenados por nombre.
     * @return Lista de entidades Rol.
     */
    @Transactional(readOnly = true)
    public List<Rol> listarTodosLosRoles() {
        log.info("Listando todos los roles...");
        List<Rol> roles = rolRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} roles.", roles.size());
        return roles;
    }

    /**
     * Crea un nuevo rol en el sistema.
     * Valida que el nombre del rol no esté duplicado (ignorando mayúsculas/minúsculas).
     * @param rol La entidad Rol (sin ID) con los datos a crear. Se recomienda guardar nombres en mayúsculas.
     * @return La entidad Rol creada y guardada.
     * @throws DuplicateResourceException si ya existe un rol con ese nombre.
     */
    @Transactional
    public Rol crearRol(Rol rol) {
        log.info("Intentando crear rol con nombre: '{}'", rol.getNombre());
        String nombreUpper = rol.getNombre().toUpperCase(); // Estandarizar a mayúsculas
        // Usar findByNombreIgnoreCase para validación case-insensitive
        rolRepository.findByNombreIgnoreCase(nombreUpper).ifPresent(existingRol -> {
            log.warn("Intento de crear rol con nombre duplicado: {}", nombreUpper);
            throw new DuplicateResourceException("Ya existe un rol con el nombre: " + nombreUpper);
        });
        rol.setNombre(nombreUpper); // Guardar en mayúsculas
        Rol rolGuardado = rolRepository.save(rol);
        log.info("Rol '{}' creado exitosamente con ID: {}", rolGuardado.getNombre(), rolGuardado.getRolId());
        return rolGuardado;
    }

    /**
     * Actualiza un rol existente.
     * Valida que el nuevo nombre no entre en conflicto con otro rol existente.
     * @param id ID del rol a actualizar.
     * @param rolActualizado Entidad Rol con los datos actualizados (nombre, descripción).
     * @return La entidad Rol actualizada.
     * @throws ResourceNotFoundException si el rol con el ID dado no existe.
     * @throws DuplicateResourceException si el nuevo nombre ya está en uso por otro rol.
     */
    @Transactional
    public Rol actualizarRol(Long id, Rol rolActualizado) {
        log.info("Intentando actualizar rol con ID: {}", id);
        Rol rolExistente = obtenerRolPorId(id); // Valida existencia

        String nombreNuevoUpper = rolActualizado.getNombre().toUpperCase();

        // Validar nombre único solo si el nombre ha cambiado (case-insensitive)
        if (!rolExistente.getNombre().equalsIgnoreCase(nombreNuevoUpper)) {
            log.debug("El nombre del rol ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, nombreNuevoUpper);
            // Verificar si existe OTRO rol con el mismo nombre
            Optional<Rol> rolConMismoNombreOpt = rolRepository.findByNombreIgnoreCase(nombreNuevoUpper);
            if (rolConMismoNombreOpt.isPresent() && !rolConMismoNombreOpt.get().getRolId().equals(id)) {
                log.warn("Conflicto: El nombre '{}' ya está en uso por otro rol (ID: {}).", nombreNuevoUpper, rolConMismoNombreOpt.get().getRolId());
                throw new DuplicateResourceException("Ya existe un rol con el nombre: " + nombreNuevoUpper);
            }
            log.debug("Nombre '{}' disponible.", nombreNuevoUpper);
            rolExistente.setNombre(nombreNuevoUpper); // Actualizar nombre en mayúsculas
        }

        rolExistente.setDescripcion(rolActualizado.getDescripcion());
        log.debug("Descripción actualizada para rol ID {}.", id);

        Rol rolGuardado = rolRepository.save(rolExistente);
        log.info("Rol ID {} actualizado exitosamente.", id);
        return rolGuardado;
    }

    /**
     * Elimina un rol del sistema.
     * Verifica que el rol no esté asignado a ningún usuario antes de eliminar.
     * @param id ID del rol a eliminar.
     * @throws ResourceNotFoundException si el rol no existe.
     * @throws ResourceInUseException si el rol está asignado a uno o más usuarios.
     */
    @Transactional
    public void eliminarRol(Long id) {
        log.info("Intentando eliminar rol con ID: {}", id);
        Rol rol = obtenerRolPorId(id); // Valida existencia

        // Validar que no esté en uso por usuarios
        long userCount = usuarioRepository.countByRol_Nombre(rol.getNombre());
        if (userCount > 0) {
            log.warn("Intento de eliminar rol ID {} ('{}') que está asignado a {} usuario(s).", id, rol.getNombre(), userCount);
            // --- CORRECCIÓN APLICADA: Lanzar ResourceInUseException ---
            throw new ResourceInUseException("No se puede eliminar el rol '" + rol.getNombre() + "' porque está asignado a " + userCount + " usuario(s).");
        }
        log.debug("El rol ID {} ('{}') no está asignado a usuarios.", id, rol.getNombre());

        // Considerar eliminar relaciones en rol_permiso explícitamente si no hay CASCADE
        // rolPermisoRepository.deleteAllByRolId(id); // Si es necesario

        rolRepository.delete(rol);
        log.info("Rol ID {} ('{}') eliminado exitosamente.", id, rol.getNombre());
    }
}