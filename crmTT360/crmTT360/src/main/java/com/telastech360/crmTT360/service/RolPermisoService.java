package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.PermisoRepository;
import com.telastech360.crmTT360.repository.RolPermisoRepository;
import com.telastech360.crmTT360.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolPermisoService {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    @Autowired
    public RolPermisoService(
            RolPermisoRepository rolPermisoRepository,
            RolRepository rolRepository,
            PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Transactional
    public void asignarPermiso(Long rolId, Long permisoId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));

        if (rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolId, permisoId)) {
            throw new RuntimeException("El permiso ya está asignado al rol");
        }

        rol.addPermiso(permiso);
        rolRepository.save(rol);
    }

    @Transactional
    public void removerPermiso(Long rolId, Long permisoId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));

        if (!rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolId, permisoId)) {
            throw new RuntimeException("La relación Rol-Permiso no existe");
        }

        rol.removePermiso(permiso);
        rolRepository.save(rol);
    }

    @Transactional(readOnly = true)
    public List<Long> obtenerPermisosDeRol(Long rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        return rol.getPermisos().stream()
                .map(permiso -> permiso.getPermisoId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Set<Permiso> obtenerPermisosCompletosDeRol(Long rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        return rol.getPermisos();
    }

    @Transactional
    public void actualizarPermisosDeRol(Long rolId, Set<Long> nuevosPermisoIds) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        Set<Permiso> nuevosPermisos = new HashSet<>();
        for (Long permisoId : nuevosPermisoIds) {
            Permiso permiso = permisoRepository.findById(permisoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));
            nuevosPermisos.add(permiso);
        }

        rol.getPermisos().clear();
        nuevosPermisos.forEach(rol::addPermiso);
        rolRepository.save(rol);
    }

    @Transactional(readOnly = true)
    public boolean existeRelacionRolPermiso(Long rolId, Long permisoId) {
        return rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolId, permisoId);
    }
}