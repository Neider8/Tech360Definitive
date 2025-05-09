// src/main/java/com/telastech360/crmTT360/mapper/RolMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.RolDTO;
import com.telastech360.crmTT360.entity.Rol;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Rol}
 * y sus correspondientes DTOs ({@link RolDTO}).
 */
@Component
public class RolMapper {

    /**
     * Convierte un {@link RolDTO} a una entidad {@link Rol}.
     * El ID del rol y la lista de permisos no se establecen aquí.
     *
     * @param rolDTO El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Rol poblada con nombre y descripción, o null si la entrada fue null.
     */
    public Rol toEntity(RolDTO rolDTO) {
        if (rolDTO == null) {
            return null;
        }
        Rol rol = new Rol();
        // El ID se genera en la BD
        // rol.setRolId(rolDTO.getRolId()); // No mapear ID desde DTO normalmente

        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());
        // La colección de permisos se gestiona en el servicio RolPermisoService
        return rol;
    }

    /**
     * Convierte una entidad {@link Rol} a un {@link RolDTO}.
     * No incluye la lista de permisos asociados.
     *
     * @param rol La entidad Rol a convertir. Si es null, retorna null.
     * @return El DTO {@link RolDTO} poblado con nombre y descripción, o null si la entrada fue null.
     */
    public RolDTO toDTO(Rol rol) {
        if (rol == null) {
            return null;
        }
        RolDTO rolDTO = new RolDTO();
        // Considera mapear el ID si es necesario para el frontend
        // rolDTO.setRolId(rol.getRolId()); // Asumiendo que RolDTO tiene rolId

        rolDTO.setNombre(rol.getNombre());
        rolDTO.setDescripcion(rol.getDescripcion());
        return rolDTO;
    }

    /**
     * Actualiza los campos de una entidad {@link Rol} existente desde un {@link RolDTO}.
     * No actualiza el ID del rol ni la lista de permisos.
     *
     * @param dto El DTO {@link RolDTO} con los datos actualizados.
     * @param rol La entidad {@link Rol} a actualizar. No realiza ninguna acción si alguno es null.
     */
    public void updateEntityFromDTO(RolDTO dto, Rol rol) {
        if (dto == null || rol == null) {
            return;
        }
        // El ID no se actualiza
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        // La colección de permisos se actualiza a través de RolPermisoService
    }
}