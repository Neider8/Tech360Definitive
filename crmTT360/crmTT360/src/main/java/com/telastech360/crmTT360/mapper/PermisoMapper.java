// src/main/java/com/telastech360/crmTT360/mapper/PermisoMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PermisoDTO;
import com.telastech360.crmTT360.entity.Permiso;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Permiso}
 * y sus correspondientes DTOs ({@link PermisoDTO}).
 */
@Component
public class PermisoMapper {

    /**
     * Convierte una entidad {@link Permiso} a un {@link PermisoDTO}.
     *
     * @param permiso La entidad Permiso a convertir. Si es null, retorna null.
     * @return El DTO {@link PermisoDTO} poblado, o null si la entrada fue null.
     */
    public PermisoDTO toDTO(Permiso permiso) {
        if (permiso == null) {
            return null;
        }
        PermisoDTO dto = new PermisoDTO();
        dto.setPermisoId(permiso.getPermisoId());
        dto.setNombre(permiso.getNombre());
        dto.setDescripcion(permiso.getDescripcion());
        // La relación con Roles no se mapea en el DTO de Permiso
        return dto;
    }

    /**
     * Convierte un {@link PermisoDTO} a una entidad {@link Permiso}.
     * El ID del permiso y la relación con Roles no se establecen aquí.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Permiso poblada con nombre y descripción, o null si la entrada fue null.
     */
    public Permiso toEntity(PermisoDTO dto) {
        if (dto == null) {
            return null;
        }
        Permiso permiso = new Permiso();
        // El permisoId se genera en la BD
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        // Las relaciones con Roles se manejan a través de la tabla de unión (RolPermiso)
        // y se gestionan en RolPermisoService.
        return permiso;
    }

    /**
     * Actualiza los campos de una entidad {@link Permiso} existente desde un {@link PermisoDTO}.
     * No actualiza el ID del permiso ni la relación con Roles.
     *
     * @param dto El DTO {@link PermisoDTO} con los datos actualizados.
     * @param permiso La entidad {@link Permiso} a actualizar. No realiza ninguna acción si alguno es null.
     */
    public void updateEntityFromDTO(PermisoDTO dto, Permiso permiso) {
        if (dto == null || permiso == null) {
            return;
        }
        // El permisoId no se actualiza
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        // Las relaciones con Roles no se actualizan aquí
    }
}