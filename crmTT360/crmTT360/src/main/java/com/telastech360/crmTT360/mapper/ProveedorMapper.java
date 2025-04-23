package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ProveedorDTO;
import com.telastech360.crmTT360.entity.Proveedor;
import org.springframework.stereotype.Component;

// Mapper para convertir entre la entidad Proveedor y el DTO ProveedorDTO.
@Component
public class ProveedorMapper {

    // Método para convertir una entidad Proveedor a un ProveedorDTO
    public ProveedorDTO toDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setProveedorId(proveedor.getProveedorId());
        dto.setNombre(proveedor.getNombre());
        dto.setDireccion(proveedor.getDireccion());
        dto.setTelefono(proveedor.getTelefono());
        dto.setEmail(proveedor.getEmail());
        return dto;
    }

    // Método para convertir un ProveedorDTO a una entidad Proveedor.
    // Nota: El proveedorId se genera automáticamente al guardar la entidad.
    // La relación con Items se maneja en las entidades Item/Producto/MateriaPrima.
    public Proveedor toEntity(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        // El proveedorId no se setea aquí (se genera automáticamente)
        proveedor.setNombre(dto.getNombre());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        // La relación con Items se maneja por separado
        return proveedor;
    }

    // Método para actualizar una entidad Proveedor existente a partir de un ProveedorDTO.
    // Nota: El proveedorId no se actualiza. La relación con Items no se actualiza aquí.
    public void updateEntityFromDTO(ProveedorDTO dto, Proveedor proveedor) {
        // El proveedorId no se actualiza
        proveedor.setNombre(dto.getNombre());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        // La relación con Items no se actualiza aquí
    }
}