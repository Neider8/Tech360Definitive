package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.CategoriaDTO;
import com.telastech360.crmTT360.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        return categoria;
    }

    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNombre(categoria.getNombre());
        categoriaDTO.setDescripcion(categoria.getDescripcion());
        return categoriaDTO;
    }
}