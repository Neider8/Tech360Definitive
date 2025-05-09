package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.RolPermisoDTO;
import com.telastech360.crmTT360.entity.RolPermiso;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.entity.RolPermisoId;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre la entidad de unión {@link RolPermiso}
 * y su DTO correspondiente ({@link RolPermisoDTO}), que típicamente solo contiene los IDs.
 * Nota: En muchos casos con `@ManyToMany`, esta entidad y mapper explícitos no son necesarios.
 */
@Component
public class RolPermisoMapper {

    /**
     * Convierte una entidad {@link RolPermiso} a un {@link RolPermisoDTO}.
     * Extrae los IDs de la clave compuesta.
     *
     * @param rolPermiso La entidad de unión RolPermiso. Si es null o su ID es null, retorna null.
     * @return El DTO {@link RolPermisoDTO} con los IDs poblados, o null si la entrada fue null.
     */
    public RolPermisoDTO toDTO(RolPermiso rolPermiso) {
        if (rolPermiso == null || rolPermiso.getId() == null) {
            return null;
        }
        RolPermisoDTO dto = new RolPermisoDTO();
        dto.setRolId(rolPermiso.getId().getRolId());
        dto.setPermisoId(rolPermiso.getId().getPermisoId());
        return dto;
    }

    /**
     * Convierte un {@link RolPermisoDTO} a una entidad {@link RolPermiso}.
     * <strong>Importante:</strong> Requiere las entidades {@link Rol} y {@link Permiso}
     * correspondientes a los IDs ya cargadas. Esta carga debe hacerse en el servicio.
     *
     * @param dto El DTO con los IDs del rol y permiso. Si es null, retorna null.
     * @param rol La entidad {@link Rol} asociada (ya cargada). No puede ser null.
     * @param permiso La entidad {@link Permiso} asociada (ya cargada). No puede ser null.
     * @return La entidad de unión {@link RolPermiso} lista para ser guardada, o null si el DTO fue null.
     * @throws NullPointerException si las entidades Rol o Permiso proporcionadas son null.
     * @throws IllegalArgumentException si los IDs no coinciden.
     */
    public RolPermiso toEntity(RolPermisoDTO dto, Rol rol, Permiso permiso) {
        if (dto == null) {
            return null;
        }
        if (rol == null || permiso == null) {
            throw new NullPointerException("Las entidades Rol y Permiso no pueden ser null al mapear RolPermisoDTO a Entidad.");
        }
        if (!rol.getRolId().equals(dto.getRolId()) || !permiso.getPermisoId().equals(dto.getPermisoId())) {
            throw new IllegalArgumentException("Los IDs en el DTO no coinciden con los IDs de las entidades proporcionadas.");
        }

        RolPermiso rolPermiso = new RolPermiso();
        RolPermisoId id = new RolPermisoId(dto.getRolId(), dto.getPermisoId());
        rolPermiso.setId(id);
        rolPermiso.setRol(rol);
        rolPermiso.setPermiso(permiso);

        return rolPermiso;
    }

    // --- Método updateEntityFromDTO eliminado por ser innecesario ---

}