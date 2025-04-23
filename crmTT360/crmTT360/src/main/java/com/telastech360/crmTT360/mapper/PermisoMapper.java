package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PermisoDTO;
import com.telastech360.crmTT360.entity.Permiso;
import org.springframework.stereotype.Component;

// Mapper para convertir entre la entidad Permiso y el DTO PermisoDTO.
@Component
public class PermisoMapper {

    // Método para convertir una entidad Permiso a un PermisoDTO
    public PermisoDTO toDTO(Permiso permiso) {
        PermisoDTO dto = new PermisoDTO();
        dto.setPermisoId(permiso.getPermisoId());
        dto.setNombre(permiso.getNombre());
        dto.setDescripcion(permiso.getDescripcion());
        return dto;
    }

    // Método para convertir un PermisoDTO a una entidad Permiso.
    // Nota: El permisoId se genera automáticamente al guardar la entidad.
    // Las relaciones con Roles se manejan por separado en RolPermisoService.
    public Permiso toEntity(PermisoDTO dto) {
        Permiso permiso = new Permiso();
        // El permisoId no se setea aquí (se genera automáticamente)
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        // Las relaciones con Roles se manejan por separado
        return permiso;
    }

    // Método para actualizar una entidad Permiso existente a partir de un PermisoDTO.
    // Nota: El permisoId no se actualiza. Las relaciones con Roles se manejan por separado.
    public void updateEntityFromDTO(PermisoDTO dto, Permiso permiso) {
        // El permisoId no se actualiza
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        // Las relaciones con Roles no se actualizan aquí
    }
}