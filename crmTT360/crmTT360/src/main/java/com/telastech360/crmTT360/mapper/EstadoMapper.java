// src/main/java/com/telastech360/crmTT360/mapper/EstadoMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.entity.Estado;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Estado}
 * y sus correspondientes DTOs ({@link EstadoDTO}).
 */
@Component
public class EstadoMapper {

    /**
     * Convierte una entidad {@link Estado} a un {@link EstadoDTO}.
     *
     * @param estado La entidad Estado a convertir. Si es null, retorna null.
     * @return El DTO {@link EstadoDTO} poblado, o null si la entrada fue null.
     */
    public EstadoDTO toDTO(Estado estado) {
        if (estado == null) return null;
        EstadoDTO dto = new EstadoDTO();
        // Considera mapear el ID si lo necesitas en el DTO para alguna lógica del frontend
        // dto.setEstadoId(estado.getEstadoId()); // Asumiendo que EstadoDTO tiene un campo estadoId
        dto.setTipoEstado(estado.getTipoEstado());
        dto.setValor(estado.getValor());
        return dto;
    }

    /**
     * Convierte un {@link EstadoDTO} a una entidad {@link Estado}.
     * El ID del estado no se establece aquí (se genera en la base de datos).
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Estado poblada con tipo y valor, o null si la entrada fue null.
     */
    public Estado toEntity(EstadoDTO dto) {
        if (dto == null) return null;
        Estado estado = new Estado();
        // El ID no se mapea (autogenerado)
        estado.setTipoEstado(dto.getTipoEstado());
        estado.setValor(dto.getValor());
        return estado;
    }

    /**
     * Actualiza los campos de una entidad {@link Estado} existente desde un {@link EstadoDTO}.
     * No actualiza el ID del estado.
     *
     * @param dto El DTO {@link EstadoDTO} con los datos actualizados.
     * @param estado La entidad {@link Estado} a actualizar. No realiza ninguna acción si alguno de los parámetros es null.
     */
    public void updateEntityFromDTO(EstadoDTO dto, Estado estado) {
        if (dto == null || estado == null) return;
        // El ID no se actualiza
        estado.setTipoEstado(dto.getTipoEstado());
        estado.setValor(dto.getValor());
    }
}