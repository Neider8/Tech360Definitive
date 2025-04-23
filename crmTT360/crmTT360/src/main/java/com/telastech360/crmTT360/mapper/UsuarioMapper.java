package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.dto.UsuarioResponseDTO;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Rol; // Importar Rol
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    // Método para convertir una entidad Usuario a un UsuarioResponseDTO
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setUsuarioId(usuario.getUsuarioId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getRolId());
        }
        // Corregido: Usar getEstado()
        dto.setEstado(usuario.getEstado());
        // Removido: Mapeo de fechaRegistro
        // dto.setFechaRegistro(usuario.getFechaRegistro());
        // La contraseña no se incluye en el DTO de respuesta
        return dto;
    }

    // Método para convertir un UsuarioRequestDTO a una entidad Usuario.
    // Requiere la entidad Rol ya cargada.
    // La contraseña se maneja en el servicio (cifrado) antes de guardar.
    // La fecha de registro y el estado inicial se establecen en el servicio (o DB default).
    public Usuario toEntity(UsuarioRequestDTO dto, Rol rol) {
        Usuario usuario = new Usuario();
        // El usuarioId no se setea aquí (se genera automáticamente)
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        // La contraseña se setea en el servicio DESPUÉS del cifrado
        // usuario.setPassword(dto.getPassword()); // NO hacer aquí directamente
        usuario.setRol(rol); // Establecer la relación con el Rol cargado
        // La fecha de registro y el estado inicial se setean en el servicio o por default en DB
        // usuario.setEstado(initialState); // Si se setea en servicio
        return usuario;
    }

    // Método para actualizar una entidad Usuario existente a partir de un UsuarioRequestDTO.
    // Requiere la entidad Rol ya cargada si el rolId cambia.
    // La contraseña no se actualiza directamente aquí; se maneja por separado si se necesita cambiar.
    public void updateEntityFromDTO(UsuarioRequestDTO dto, Usuario usuario, Rol rol) {
        // El usuarioId no se actualiza
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        // La contraseña NO se actualiza aquí (se maneja por separado)

        // Solo actualizar el rol si el rolId en el DTO es diferente de null
        // y si la entidad Rol proporcionada no es null
        if (dto.getRolId() != null && rol != null) {
            usuario.setRol(rol);
        }

        // El estado del usuario generalmente se actualiza a través de un endpoint o lógica específica.
        // Si quisieras permitir la actualización del estado desde este DTO, necesitarías el campo en UsuarioRequestDTO
        // y aquí: usuario.setEstado(dto.getEstado()); // Solo si 'estado' está en UsuarioRequestDTO
    }
}