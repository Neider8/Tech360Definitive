// src/main/java/com/telastech360/crmTT360/mapper/BodegaMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.BodegaDTO;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Estado; // Necesario si se mapean relaciones completas
import com.telastech360.crmTT360.entity.Usuario; // Necesario si se mapean relaciones completas
import com.telastech360.crmTT360.exception.InvalidDataException; // Para manejar errores de enum
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Bodega}
 * y sus correspondientes DTOs ({@link BodegaDTO}).
 * Facilita la transferencia de datos segura y estructurada entre capas,
 * desacoplando la representación interna de la externa.
 */
@Component
public class BodegaMapper {

    /**
     * Convierte un {@link BodegaDTO} a una entidad {@link Bodega} básica.
     * <strong>Importante:</strong> No asigna las entidades Estado o Responsable (Usuario).
     * La lógica de buscar estas entidades por los IDs proporcionados en el DTO
     * debe realizarse en la capa de servicio.
     *
     * @param bodegaDTO El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Bodega parcialmente poblada (sin relaciones Estado/Responsable),
     * o null si la entrada fue null.
     * @throws InvalidDataException si el valor de tipoBodega en el DTO no es válido.
     */
    public Bodega toEntity(BodegaDTO bodegaDTO) {
        if (bodegaDTO == null) {
            return null;
        }
        Bodega bodega = new Bodega();
        // El ID se genera en la BD, no se establece aquí

        bodega.setNombre(bodegaDTO.getNombre());

        // Mapea el String del DTO al Enum de la entidad con validación
        if (bodegaDTO.getTipoBodega() != null) {
            try {
                // Convertir a mayúsculas para coincidir con los nombres de enum estándar
                bodega.setTipoBodega(Bodega.TipoBodega.valueOf(bodegaDTO.getTipoBodega().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de bodega inválido: " + bodegaDTO.getTipoBodega() +
                        ". Valores permitidos: MATERIA_PRIMA, PRODUCTO_TERMINADO, TEMPORAL");
            }
        } // Si es null, se dejará null en la entidad (requiere validación @NotNull en DTO)

        bodega.setCapacidadMaxima(bodegaDTO.getCapacidadMaxima());
        bodega.setUbicacion(bodegaDTO.getUbicacion());

        // Las relaciones Estado y Responsable deben ser asignadas en el Servicio
        // buscando las entidades correspondientes por los IDs (estadoId, responsableId) presentes en el DTO.
        // Aquí solo se copian los atributos directos.

        // La fecha de creación se establece automáticamente en la entidad

        return bodega;
    }

    /**
     * Convierte una entidad {@link Bodega} a un {@link BodegaDTO}.
     * Mapea los IDs de las entidades relacionadas (Estado, Responsable) si existen.
     *
     * @param bodega La entidad Bodega a convertir. Si es null, retorna null.
     * @return El DTO {@link BodegaDTO} poblado, o null si la entrada fue null.
     */
    public BodegaDTO toDTO(Bodega bodega) {
        if (bodega == null) {
            return null;
        }
        BodegaDTO bodegaDTO = new BodegaDTO();

        // Considera añadir el ID al DTO si es necesario para el frontend
        // bodegaDTO.setBodegaId(bodega.getBodegaId()); // Asumiendo que BodegaDTO tiene un campo bodegaId

        bodegaDTO.setNombre(bodega.getNombre());

        // Mapea el Enum de la entidad al String del DTO
        if (bodega.getTipoBodega() != null) {
            bodegaDTO.setTipoBodega(bodega.getTipoBodega().name()); // .name() devuelve el nombre del enum como String
        }

        bodegaDTO.setCapacidadMaxima(bodega.getCapacidadMaxima());
        bodegaDTO.setUbicacion(bodega.getUbicacion());

        // Mapea el ID del Estado asociado
        if (bodega.getEstado() != null) {
            bodegaDTO.setEstadoId(bodega.getEstado().getEstadoId());
        }

        // Considera mapear el ID del Responsable si BodegaDTO lo requiere
        /*
        if (bodega.getResponsable() != null) {
            // Asumiendo que BodegaDTO tiene un campo responsableId y su setter
            bodegaDTO.setResponsableId(bodega.getResponsable().getUsuarioId()); // Asumiendo getters
        }
        */
        // La fecha de creación generalmente no se incluye en el DTO de respuesta

        return bodegaDTO;
    }

    /**
     * Actualiza los campos básicos de una entidad {@link Bodega} existente desde un {@link BodegaDTO}.
     * <strong>Importante:</strong> No actualiza las relaciones (Estado, Responsable).
     * La lógica para actualizar relaciones buscando por ID debe estar en la capa de servicio.
     *
     * @param dto El DTO {@link BodegaDTO} con los datos actualizados.
     * @param bodega La entidad {@link Bodega} a actualizar. No realiza ninguna acción si alguno de los parámetros es null.
     * @throws InvalidDataException si el valor de tipoBodega en el DTO no es válido.
     */
    public void updateEntityFromDTO(BodegaDTO dto, Bodega bodega) {
        if (dto == null || bodega == null) {
            return; // No hacer nada si no hay datos o entidad
        }

        // Actualiza campos directos
        bodega.setNombre(dto.getNombre());
        bodega.setCapacidadMaxima(dto.getCapacidadMaxima());
        bodega.setUbicacion(dto.getUbicacion());

        // Actualiza el tipo de bodega con validación
        if (dto.getTipoBodega() != null) {
            try {
                // Convertir a mayúsculas para coincidir con los nombres de enum estándar
                bodega.setTipoBodega(Bodega.TipoBodega.valueOf(dto.getTipoBodega().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de bodega inválido: " + dto.getTipoBodega() +
                        ". Valores permitidos: MATERIA_PRIMA, PRODUCTO_TERMINADO, TEMPORAL");
            }
        } // Si es null, no se actualiza (o se podría establecer a null si la lógica lo permite)

        // Las relaciones Estado y Responsable deben actualizarse en el servicio
        // buscando las entidades por los IDs dto.getEstadoId() y dto.getResponsableId()
    }
}