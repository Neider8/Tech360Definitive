// src/main/java/com/telastech360/crmTT360/mapper/ProveedorMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ProveedorDTO;
import com.telastech360.crmTT360.entity.Proveedor;
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Proveedor}
 * y sus correspondientes DTOs ({@link ProveedorDTO}).
 */
@Component
public class ProveedorMapper {

    /**
     * Convierte una entidad {@link Proveedor} a un {@link ProveedorDTO}.
     *
     * @param proveedor La entidad Proveedor a convertir. Si es null, retorna null.
     * @return El DTO {@link ProveedorDTO} poblado, o null si la entrada fue null.
     */
    public ProveedorDTO toDTO(Proveedor proveedor) {
        if (proveedor == null) {
            return null;
        }
        ProveedorDTO dto = new ProveedorDTO();
        dto.setProveedorId(proveedor.getProveedorId());
        dto.setNombre(proveedor.getNombre());
        dto.setDireccion(proveedor.getDireccion());
        dto.setTelefono(proveedor.getTelefono());
        dto.setEmail(proveedor.getEmail());
        // La lista de Items no se mapea aquí
        return dto;
    }

    /**
     * Convierte un {@link ProveedorDTO} a una entidad {@link Proveedor}.
     * El ID del proveedor y la lista de ítems no se establecen aquí.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Proveedor poblada con los datos básicos, o null si la entrada fue null.
     */
    public Proveedor toEntity(ProveedorDTO dto) {
        if (dto == null) {
            return null;
        }
        Proveedor proveedor = new Proveedor();
        // El proveedorId se genera en la BD
        proveedor.setNombre(dto.getNombre());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        // La relación con Items se maneja a través de la asociación en la entidad Item
        return proveedor;
    }

    /**
     * Actualiza los campos de una entidad {@link Proveedor} existente desde un {@link ProveedorDTO}.
     * No actualiza el ID del proveedor ni la lista de ítems.
     *
     * @param dto El DTO {@link ProveedorDTO} con los datos actualizados.
     * @param proveedor La entidad {@link Proveedor} a actualizar. No realiza ninguna acción si alguno es null.
     */
    public void updateEntityFromDTO(ProveedorDTO dto, Proveedor proveedor) {
        if (dto == null || proveedor == null) {
            return;
        }
        // El proveedorId no se actualiza
        proveedor.setNombre(dto.getNombre());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        // La relación con Items no se actualiza aquí
    }
}