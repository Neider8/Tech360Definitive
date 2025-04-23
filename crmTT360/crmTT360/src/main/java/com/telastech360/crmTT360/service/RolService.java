package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    @Autowired
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Obtiene un rol por su ID.
     *
     * @param id ID del rol.
     * @return El rol encontrado.
     * @throws ResourceNotFoundException Si el rol no existe.
     */
    @Transactional(readOnly = true)
    public Rol obtenerRolPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
    }

    /**
     * Lista todos los roles.
     *
     * @return Lista de roles ordenados por nombre.
     */
    @Transactional(readOnly = true)
    public List<Rol> listarTodosLosRoles() {
        return rolRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Crea un nuevo rol.
     *
     * @param rol Datos del rol a crear.
     * @return El rol creado.
     */
    @Transactional
    public Rol crearRol(Rol rol) {
        if (rolRepository.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
        }
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol actualizarRol(Long id, Rol rolActualizado) {
        Rol rolExistente = obtenerRolPorId(id);

        if (!rolExistente.getNombre().equals(rolActualizado.getNombre()) &&
                rolRepository.existsByNombre(rolActualizado.getNombre())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rolActualizado.getNombre());
        }

        rolExistente.setNombre(rolActualizado.getNombre());
        rolExistente.setDescripcion(rolActualizado.getDescripcion());

        return rolRepository.save(rolExistente);
    }

    @Transactional
    public void eliminarRol(Long id) {
        Rol rol = obtenerRolPorId(id);
        rolRepository.delete(rol);
    }
}