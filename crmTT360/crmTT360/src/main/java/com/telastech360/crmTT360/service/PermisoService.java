package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.PermisoRepository;
import com.telastech360.crmTT360.repository.RolPermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermisoService {

    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    @Autowired
    public PermisoService(PermisoRepository permisoRepository,
                          RolPermisoRepository rolPermisoRepository) {
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public Permiso crearPermiso(Permiso permiso) {
        if (permisoRepository.existsByNombre(permiso.getNombre())) {
            throw new DuplicateResourceException("Ya existe un permiso con el nombre: " + permiso.getNombre());
        }
        return permisoRepository.save(permiso);
    }

    public Permiso obtenerPermisoPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
    }

    public List<Permiso> listarTodosLosPermisos() {
        return permisoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        Permiso permisoExistente = obtenerPermisoPorId(id);

        if (!permisoExistente.getNombre().equals(permisoActualizado.getNombre()) &&
                permisoRepository.existsByNombre(permisoActualizado.getNombre())) {
            throw new DuplicateResourceException("Nombre no disponible: " + permisoActualizado.getNombre());
        }

        permisoExistente.setNombre(permisoActualizado.getNombre());
        permisoExistente.setDescripcion(permisoActualizado.getDescripcion());
        return permisoRepository.save(permisoExistente);
    }

    @Transactional
    public void eliminarPermiso(Long id) {
        Permiso permiso = obtenerPermisoPorId(id);

        // Eliminar relaciones con roles primero
        rolPermisoRepository.deleteAllByPermisoId(id);

        permisoRepository.delete(permiso);
    }

    // ========== MÉTODOS ADICIONALES ========== //

    public List<Permiso> buscarPermisosPorNombre(String nombre) {
        return permisoRepository.findByNombreContaining(nombre);
    }

    public List<Permiso> buscarPermisosPorDescripcion(String texto) {
        return permisoRepository.buscarPorDescripcion(texto);
    }

    @Transactional(readOnly = true)
    public boolean existePermisoEnRoles(Long permisoId) {
        return rolPermisoRepository.countRolesWithPermiso(permisoId) > 0;
    }
}