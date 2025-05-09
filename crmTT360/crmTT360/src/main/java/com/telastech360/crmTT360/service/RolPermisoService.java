package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.RolPermiso; // Necesario si se maneja explícitamente
import com.telastech360.crmTT360.entity.RolPermisoId; // Necesario si se maneja explícitamente
import com.telastech360.crmTT360.exception.DuplicateResourceException; // Para asignación duplicada
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.PermisoRepository;
import com.telastech360.crmTT360.repository.RolPermisoRepository;
import com.telastech360.crmTT360.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la asignación y desasignación de Permisos a Roles.
 * Opera sobre la relación ManyToMany entre Rol y Permiso.
 */
@Service
public class RolPermisoService {

    private static final Logger log = LoggerFactory.getLogger(RolPermisoService.class);

    private final RolPermisoRepository rolPermisoRepository; // Para verificaciones y borrados explícitos si se usa
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    /**
     * Constructor para inyección de dependencias.
     * @param rolPermisoRepository Repositorio para la tabla de unión (si se usa explícitamente).
     * @param rolRepository Repositorio para Roles.
     * @param permisoRepository Repositorio para Permisos.
     */
    @Autowired
    public RolPermisoService(
            RolPermisoRepository rolPermisoRepository,
            RolRepository rolRepository,
            PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    /**
     * Asigna un permiso específico a un rol específico.
     * Verifica que ambas entidades existan y que la asignación no exista previamente.
     * @param rolId ID del rol.
     * @param permisoId ID del permiso.
     * @throws ResourceNotFoundException si el rol o el permiso no existen.
     * @throws DuplicateResourceException si el permiso ya está asignado a ese rol.
     */
    @Transactional
    public void asignarPermiso(Long rolId, Long permisoId) {
        log.info("Intentando asignar permiso ID {} a rol ID {}", permisoId, rolId);

        // Cargar Rol validando existencia
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> {
                    log.warn("Asignación fallida: Rol ID {} no encontrado.", rolId);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
                });

        // Cargar Permiso validando existencia
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> {
                    log.warn("Asignación fallida: Permiso ID {} no encontrado.", permisoId);
                    return new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId);
                });

        // Verificar si la asignación ya existe (usando el repositorio de unión o chequeando la colección)
        if (rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolId, permisoId)) {
            // Alternativa: if (rol.getPermisos().contains(permiso)) {
            log.warn("Asignación fallida: Permiso ID {} ya está asignado a Rol ID {}.", permisoId, rolId);
            throw new DuplicateResourceException("El permiso '" + permiso.getNombre() + "' ya está asignado al rol '" + rol.getNombre() + "'");
        }

        // Realizar la asignación (asumiendo @ManyToMany bidireccional con métodos helper)
        rol.addPermiso(permiso);
        rolRepository.save(rol); // Guardar el lado propietario de la relación si es necesario
        log.info("Permiso '{}' (ID:{}) asignado exitosamente a Rol '{}' (ID:{})", permiso.getNombre(), permisoId, rol.getNombre(), rolId);
    }

    /**
     * Remueve un permiso específico de un rol específico.
     * Verifica que ambas entidades existan y que la asignación exista antes de removerla.
     * @param rolId ID del rol.
     * @param permisoId ID del permiso.
     * @throws ResourceNotFoundException si el rol, el permiso o la asignación no existen.
     */
    @Transactional
    public void removerPermiso(Long rolId, Long permisoId) {
        log.info("Intentando remover permiso ID {} de rol ID {}", permisoId, rolId);

        // Cargar Rol validando existencia
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> {
                    log.warn("Remoción fallida: Rol ID {} no encontrado.", rolId);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
                });

        // Cargar Permiso validando existencia
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> {
                    log.warn("Remoción fallida: Permiso ID {} no encontrado.", permisoId);
                    return new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId);
                });

        // Verificar si la asignación existe antes de intentar remover
        if (!rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolId, permisoId)) {
            // Alternativa: if (!rol.getPermisos().contains(permiso)) {
            log.warn("Remoción fallida: Permiso ID {} no estaba asignado a Rol ID {}.", permisoId, rolId);
            throw new ResourceNotFoundException("La relación entre Rol ID " + rolId + " y Permiso ID " + permisoId + " no existe.");
        }

        // Realizar la remoción
        rol.removePermiso(permiso);
        rolRepository.save(rol); // Guardar el lado propietario
        log.info("Permiso '{}' (ID:{}) removido exitosamente de Rol '{}' (ID:{})", permiso.getNombre(), permisoId, rol.getNombre(), rolId);
    }

    /**
     * Obtiene una lista de los IDs de todos los permisos asignados a un rol específico.
     * @param rolId ID del rol.
     * @return Lista de Long con los IDs de los permisos.
     * @throws ResourceNotFoundException si el rol no existe.
     */
    @Transactional(readOnly = true)
    public List<Long> obtenerPermisosDeRol(Long rolId) {
        log.info("Obteniendo IDs de permisos para rol ID {}", rolId);
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> {
                    log.warn("Búsqueda de permisos fallida: Rol ID {} no encontrado.", rolId);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
                });

        // Usar el repositorio de unión es más directo para obtener solo IDs
        List<Long> permisoIds = rolPermisoRepository.findPermisoIdsByRolId(rolId);
        // Alternativa: mapear desde la colección del rol
        // List<Long> permisoIds = rol.getPermisos().stream()
        //        .map(Permiso::getPermisoId)
        //        .collect(Collectors.toList());
        log.debug("Rol ID {} tiene {} permisos asignados.", rolId, permisoIds.size());
        return permisoIds;
    }

    /**
     * Obtiene el conjunto completo de entidades Permiso asignadas a un rol específico.
     * @param rolId ID del rol.
     * @return Set de entidades Permiso.
     * @throws ResourceNotFoundException si el rol no existe.
     */
    @Transactional(readOnly = true)
    public Set<Permiso> obtenerPermisosCompletosDeRol(Long rolId) {
        log.info("Obteniendo entidades Permiso completas para rol ID {}", rolId);
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> {
                    log.warn("Búsqueda de permisos completos fallida: Rol ID {} no encontrado.", rolId);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
                });

        // Acceder a la colección (puede requerir inicialización si es LAZY)
        Set<Permiso> permisos = rol.getPermisos();
        // Forzar inicialización si es LAZY (opcional, depende de la transacción)
        // Hibernate.initialize(permisos);
        log.debug("Rol ID {} tiene {} permisos completos.", rolId, permisos.size());
        return permisos;
    }

    /**
     * Actualiza el conjunto completo de permisos para un rol, reemplazando las asignaciones existentes.
     * @param rolId ID del rol a actualizar.
     * @param nuevosPermisoIds Conjunto de IDs de los permisos que el rol debe tener.
     * @throws ResourceNotFoundException si el rol o alguno de los permisos no existen.
     */
    @Transactional
    public void actualizarPermisosDeRol(Long rolId, Set<Long> nuevosPermisoIds) {
        log.info("Actualizando conjunto completo de permisos para rol ID {}. Nuevos IDs: {}", rolId, nuevosPermisoIds);
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> {
                    log.warn("Actualización de permisos fallida: Rol ID {} no encontrado.", rolId);
                    return new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
                });

        // Crear el nuevo conjunto de entidades Permiso
        Set<Permiso> nuevosPermisos = new HashSet<>();
        for (Long permisoId : nuevosPermisoIds) {
            Permiso permiso = permisoRepository.findById(permisoId)
                    .orElseThrow(() -> {
                        log.warn("Actualización de permisos fallida: Permiso ID {} no encontrado.", permisoId);
                        return new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId);
                    });
            nuevosPermisos.add(permiso);
        }
        log.debug("Se encontraron {} entidades Permiso para asignar al rol ID {}", nuevosPermisos.size(), rolId);

        // Limpiar permisos actuales y añadir los nuevos
        // (Asume que los métodos add/remove manejan la bidireccionalidad si es necesario)
        // Es más eficiente actualizar la colección directamente
        rol.getPermisos().clear(); // Limpia la colección existente
        rol.getPermisos().addAll(nuevosPermisos); // Añade los nuevos permisos

        // O si usas métodos helper:
        // Set<Permiso> permisosActuales = new HashSet<>(rol.getPermisos()); // Copia para evitar ConcurrentModificationException
        // permisosActuales.forEach(rol::removePermiso);
        // nuevosPermisos.forEach(rol::addPermiso);

        rolRepository.save(rol); // Guardar los cambios en la relación
        log.info("Permisos para rol ID {} actualizados exitosamente.", rolId);
    }

    /**
     * Verifica si existe una relación específica entre un rol y un permiso.
     * @param rolId ID del rol.
     * @param permisoId ID del permiso.
     * @return true si la relación existe, false en caso contrario.
     * @throws ResourceNotFoundException si el Rol o Permiso no existen (implícito si el repositorio de unión no encuentra la clave).
     */
    @Transactional(readOnly = true)
    public boolean existeRelacionRolPermiso(Long rolId, Long permisoId) {
        log.debug("Verificando existencia de relación entre Rol ID {} y Permiso ID {}", rolId, permisoId);
        // Usar el repositorio de unión es lo más directo
        boolean existe = rolPermisoRepository.existsById(new RolPermisoId(rolId, permisoId));
        // Alternativa: Cargar rol y verificar la colección (menos eficiente)
        // Rol rol = rolRepository.findById(rolId).orElse(null);
        // boolean existe = rol != null && rol.getPermisos().stream().anyMatch(p -> p.getPermisoId().equals(permisoId));
        log.debug("La relación entre Rol ID {} y Permiso ID {} {}", rolId, permisoId, existe ? "existe." : "no existe.");
        return existe;
    }
}