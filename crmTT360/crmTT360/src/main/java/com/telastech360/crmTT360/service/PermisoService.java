package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.PermisoRepository;
import com.telastech360.crmTT360.repository.RolPermisoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Permisos del sistema.
 */
@Service
public class PermisoService {

    private static final Logger log = LoggerFactory.getLogger(PermisoService.class);

    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository; // Para verificar uso

    /**
     * Constructor para inyección de dependencias.
     * @param permisoRepository Repositorio para acceso a datos de Permiso.
     * @param rolPermisoRepository Repositorio para verificar relaciones Rol-Permiso.
     */
    @Autowired
    public PermisoService(PermisoRepository permisoRepository,
                          RolPermisoRepository rolPermisoRepository) {
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    /**
     * Crea un nuevo permiso.
     * Valida que el nombre del permiso sea único (ignorando mayúsculas/minúsculas).
     * @param permiso Entidad Permiso con los datos a crear.
     * @return La entidad Permiso creada y guardada.
     * @throws DuplicateResourceException si el nombre del permiso ya existe.
     */
    @Transactional
    public Permiso crearPermiso(Permiso permiso) {
        log.info("Intentando crear permiso con nombre: '{}'", permiso.getNombre());
        if (permisoRepository.findByNombreIgnoreCase(permiso.getNombre()).isPresent()) {
            log.warn("Intento de crear permiso con nombre duplicado: {}", permiso.getNombre());
            throw new DuplicateResourceException("Ya existe un permiso con el nombre: " + permiso.getNombre());
        }
        Permiso permisoGuardado = permisoRepository.save(permiso);
        log.info("Permiso '{}' creado exitosamente con ID: {}", permisoGuardado.getNombre(), permisoGuardado.getPermisoId());
        return permisoGuardado;
    }

    /**
     * Obtiene un permiso por su ID.
     * @param id ID del permiso a buscar.
     * @return La entidad Permiso encontrada.
     * @throws ResourceNotFoundException si el permiso no existe.
     */
    @Transactional(readOnly = true)
    public Permiso obtenerPermisoPorId(Long id) {
        log.info("Buscando permiso por ID: {}", id);
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Permiso con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Permiso no encontrado con ID: " + id);
                });
        log.debug("Permiso encontrado: {} (ID: {})", permiso.getNombre(), id);
        return permiso;
    }

    /**
     * Lista todos los permisos registrados, ordenados por nombre.
     * @return Lista de entidades Permiso.
     */
    @Transactional(readOnly = true)
    public List<Permiso> listarTodosLosPermisos() {
        log.info("Listando todos los permisos...");
        List<Permiso> permisos = permisoRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} permisos.", permisos.size());
        return permisos;
    }

    /**
     * Actualiza un permiso existente.
     * Valida que el nuevo nombre no entre en conflicto con otro permiso.
     * @param id ID del permiso a actualizar.
     * @param permisoActualizado Entidad Permiso con los datos actualizados.
     * @return La entidad Permiso actualizada.
     * @throws ResourceNotFoundException si el permiso con el ID dado no existe.
     * @throws DuplicateResourceException si el nuevo nombre ya está en uso por otro permiso.
     */
    @Transactional
    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        log.info("Intentando actualizar permiso con ID: {}", id);
        Permiso permisoExistente = obtenerPermisoPorId(id); // Valida existencia

        // Validar nombre único si cambia
        if (!permisoExistente.getNombre().equalsIgnoreCase(permisoActualizado.getNombre())) {
            log.debug("El nombre del permiso ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, permisoActualizado.getNombre());
            if (permisoRepository.findByNombreIgnoreCase(permisoActualizado.getNombre()).isPresent()) {
                log.warn("Conflicto: El nombre '{}' ya está en uso por otro permiso.", permisoActualizado.getNombre());
                throw new DuplicateResourceException("Nombre de permiso no disponible: " + permisoActualizado.getNombre());
            }
            log.debug("Nombre '{}' disponible.", permisoActualizado.getNombre());
            permisoExistente.setNombre(permisoActualizado.getNombre());
        }

        permisoExistente.setDescripcion(permisoActualizado.getDescripcion());
        log.debug("Descripción actualizada para permiso ID {}.", id);

        Permiso permisoGuardado = permisoRepository.save(permisoExistente);
        log.info("Permiso ID {} actualizado exitosamente.", id);
        return permisoGuardado;
    }

    /**
     * Elimina un permiso del sistema.
     * Verifica que el permiso no esté asignado a ningún rol antes de eliminar.
     * @param id ID del permiso a eliminar.
     * @throws ResourceNotFoundException si el permiso no existe.
     * @throws IllegalOperationException si el permiso está asignado a uno o más roles.
     */
    @Transactional
    public void eliminarPermiso(Long id) {
        log.info("Intentando eliminar permiso con ID: {}", id);
        Permiso permiso = obtenerPermisoPorId(id); // Valida existencia

        // Validar que no esté en uso por roles
        long rolesCount = rolPermisoRepository.countRolesWithPermiso(id); // Método del repositorio RolPermiso
        if (rolesCount > 0) {
            log.warn("Intento de eliminar permiso ID {} ('{}') que está asignado a {} rol(es).", id, permiso.getNombre(), rolesCount);
            throw new IllegalOperationException("No se puede eliminar el permiso '" + permiso.getNombre() + "' porque está asignado a " + rolesCount + " rol(es).");
        }
        log.debug("El permiso ID {} ('{}') no está asignado a roles.", id, permiso.getNombre());

        // Si usas @ManyToMany directo, Hibernate maneja la tabla de unión.
        // Si usas RolPermiso explícito, la eliminación de relaciones ya se hizo en el chequeo anterior
        // o debería hacerse explícitamente: rolPermisoRepository.deleteAllByPermisoId(id);

        permisoRepository.delete(permiso);
        log.info("Permiso ID {} ('{}') eliminado exitosamente.", id, permiso.getNombre());
    }

    // ========== MÉTODOS ADICIONALES ========== //

    /**
     * Busca permisos cuyo nombre contenga el fragmento dado (case-sensitive).
     * @param nombre Fragmento del nombre a buscar.
     * @return Lista de entidades Permiso coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Permiso> buscarPermisosPorNombre(String nombre) {
        log.info("Buscando permisos por nombre que contenga: '{}'", nombre);
        List<Permiso> permisos = permisoRepository.findByNombreContaining(nombre); // Asume case-sensitive
        log.debug("Búsqueda por nombre '{}' encontró {} permisos.", nombre, permisos.size());
        return permisos;
    }

    /**
     * Busca permisos cuya descripción contenga el texto dado (case-insensitive).
     * @param texto Texto a buscar en la descripción.
     * @return Lista de entidades Permiso coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Permiso> buscarPermisosPorDescripcion(String texto) {
        log.info("Buscando permisos por descripción que contenga: '{}'", texto);
        List<Permiso> permisos = permisoRepository.buscarPorDescripcion(texto); // JPQL con LIKE
        log.debug("Búsqueda por descripción '{}' encontró {} permisos.", texto, permisos.size());
        return permisos;
    }

    /**
     * Verifica si un permiso está asignado a algún rol.
     * @param permisoId ID del permiso a verificar.
     * @return true si está asignado a al menos un rol, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existePermisoEnRoles(Long permisoId) {
        log.debug("Verificando si el permiso ID {} está asignado a algún rol.", permisoId);
        return rolPermisoRepository.countRolesWithPermiso(permisoId) > 0;
    }
}