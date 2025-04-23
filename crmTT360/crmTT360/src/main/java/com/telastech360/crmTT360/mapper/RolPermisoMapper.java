package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.RolPermisoDTO;
import com.telastech360.crmTT360.entity.RolPermiso;
import com.telastech360.crmTT360.entity.Rol;     // Importar Rol
import com.telastech360.crmTT360.entity.Permiso; // Importar Permiso
import com.telastech360.crmTT360.entity.RolPermisoId; // Importar RolPermisoId
import org.springframework.stereotype.Component;

// Mapper para convertir entre la entidad RolPermiso y el DTO RolPermisoDTO.
@Component
public class RolPermisoMapper {

    // Método para convertir una entidad RolPermiso a un RolPermisoDTO
    public RolPermisoDTO toDTO(RolPermiso rolPermiso) {
        RolPermisoDTO dto = new RolPermisoDTO();
        // Asegurarse de que la clave compuesta no sea null antes de acceder a los IDs
        if (rolPermiso.getId() != null) {
            dto.setRolId(rolPermiso.getId().getRolId());
            dto.setPermisoId(rolPermiso.getId().getPermisoId());
        }

        /*
        if (rolPermiso.getRol() != null) {
            dto.setRolId(rolPermiso.getRol().getRolId());
        }
        if (rolPermiso.getPermiso() != null) {
            dto.setPermisoId(rolPermiso.getPermiso().getPermisoId());
        }
        */
        return dto;
    }

    // Método para convertir un RolPermisoDTO a una entidad RolPermiso.
    // Requiere las entidades Rol y Permiso ya cargadas.
    public RolPermiso toEntity(RolPermisoDTO dto, Rol rol, Permiso permiso) {
        RolPermiso rolPermiso = new RolPermiso();

        // Crear y establecer la clave compuesta
        RolPermisoId id = new RolPermisoId(dto.getRolId(), dto.getPermisoId());
        rolPermiso.setId(id);

        // Establecer las entidades relacionadas
        rolPermiso.setRol(rol);
        rolPermiso.setPermiso(permiso);

        return rolPermiso;
    }

    // Método para actualizar una entidad RolPermiso existente a partir de un RolPermisoDTO.
    // En el caso de RolPermiso (una tabla de unión pura), la "actualización"
    // generalmente implica crear o eliminar la entrada. Este método podría ser
    // menos útil que para otras entidades, pero se incluye por completitud
    // aunque su uso en el servicio sería limitado (quizás para verificar IDs).
    public void updateEntityFromDTO(RolPermisoDTO dto, RolPermiso rolPermiso) {
        // Para una entidad de unión, los IDs en la clave compuesta
        // y las relaciones no suelen "actualizarse" en el sentido tradicional.
        // Si los IDs cambian, en realidad es una nueva relación.
        // Este método podría usarse solo para validar que los IDs del DTO
        // coinciden con los de la entidad existente antes de una operación,
        // pero la lógica principal de "actualización" de una relación RolPermiso
        // se basa en operaciones de creación/eliminación.
        // No hay atributos adicionales en RolPermiso que necesiten ser mapeados.
    }
}