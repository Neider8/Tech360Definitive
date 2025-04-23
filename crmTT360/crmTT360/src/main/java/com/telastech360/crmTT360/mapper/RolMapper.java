package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.RolDTO;
import com.telastech360.crmTT360.entity.Rol;
import org.springframework.stereotype.Component; // Importa la anotación Component

@Component // Marca la clase como un componente de Spring
public class RolMapper {

    // Convertir de DTO a Entidad (método de instancia - SIN static)
    public Rol toEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        // Considera mapear el ID si es relevante para tu caso de uso
        // if (rolDTO.getId() != null) {
        //     rol.setId(rolDTO.getId());
        // }
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());
        return rol;
    }

    // Convertir de Entidad a DTO (método de instancia - SIN static)
    public RolDTO toDTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        // Considera mapear el ID si es relevante para tu caso de uso
        // rolDTO.setId(rol.getId());
        rolDTO.setNombre(rol.getNombre());
        rolDTO.setDescripcion(rol.getDescripcion());
        return rolDTO;
    }
}