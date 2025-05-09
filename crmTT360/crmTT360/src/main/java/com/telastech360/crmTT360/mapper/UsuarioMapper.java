package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO; // Asegurar importaciones necesarias
import com.telastech360.crmTT360.dto.UsuarioResponseDTO;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Rol;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Usuario}
 * y sus correspondientes DTOs ({@link UsuarioRequestDTO}, {@link UsuarioResponseDTO}).
 * Facilita la transferencia de datos segura y estructurada entre capas.
 */
@Component
public class UsuarioMapper {

    /**
     * Convierte una entidad {@link Usuario} a un {@link UsuarioResponseDTO}.
     * Excluye información sensible como el hash de la contraseña.
     *
     * @param usuario La entidad Usuario a convertir. Si es null, retorna null.
     * @return El DTO {@link UsuarioResponseDTO} poblado, o null si la entrada fue null.
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setUsuarioId(usuario.getUsuarioId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getRolId());
        } else {
            dto.setRolId(null);
        }
        dto.setEstado(usuario.getEstado());
        return dto;
    }

    // --- Métodos comentados eliminados ---

}