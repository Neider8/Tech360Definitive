// src/main/java/com/telastech360/crmTT360/mapper/CategoriaMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.CategoriaDTO;
import com.telastech360.crmTT360.entity.Categoria;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Categoria}
 * y sus correspondientes DTOs ({@link CategoriaDTO}).
 */
@Component
public class CategoriaMapper {

    /**
     * Convierte un {@link CategoriaDTO} a una entidad {@link Categoria}.
     * El ID de la categoría y la lista de ítems no se establecen aquí.
     *
     * @param categoriaDTO El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Categoria poblada con nombre y descripción, o null si la entrada fue null.
     */
    public Categoria toEntity(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        // El ID se genera en la BD
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        // La relación con Items se maneja a través de la asociación en la entidad Item
        return categoria;
    }

    /**
     * Convierte una entidad {@link Categoria} a un {@link CategoriaDTO}.
     * No incluye la lista de ítems asociados.
     *
     * @param categoria La entidad Categoria a convertir. Si es null, retorna null.
     * @return El DTO {@link CategoriaDTO} poblado con nombre y descripción, o null si la entrada fue null.
     */
    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        // Se podría añadir el ID si es necesario para el frontend
        // categoriaDTO.setCategoriaId(categoria.getCategoriaId());
        categoriaDTO.setNombre(categoria.getNombre());
        categoriaDTO.setDescripcion(categoria.getDescripcion());
        return categoriaDTO;
    }

    /**
     * Actualiza los campos de una entidad {@link Categoria} existente desde un {@link CategoriaDTO}.
     *
     * @param dto El DTO {@link CategoriaDTO} con los datos actualizados.
     * @param categoria La entidad {@link Categoria} a actualizar. No realiza ninguna acción si alguno es null.
     */
    public void updateEntityFromDTO(CategoriaDTO dto, Categoria categoria) {
        if (dto == null || categoria == null) {
            return;
        }
        // El ID no se actualiza
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
    }
}