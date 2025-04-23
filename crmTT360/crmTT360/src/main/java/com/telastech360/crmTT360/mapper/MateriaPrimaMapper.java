package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.MateriaPrimaDTO;
import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.MateriaPrima.TipoMaterial; // Importar el enum TipoMaterial
import com.telastech360.crmTT360.exception.InvalidDataException; // Importar InvalidDataException
import org.springframework.stereotype.Component;

// Mapper para convertir entre la entidad MateriaPrima y el DTO MateriaPrimaDTO.
@Component
public class MateriaPrimaMapper {

    // Método para convertir una entidad MateriaPrima a un MateriaPrimaDTO
    public MateriaPrimaDTO toDTO(MateriaPrima materiaPrima) {
        MateriaPrimaDTO dto = new MateriaPrimaDTO();
        // Mapeo de atributos heredados de Item
        dto.setCodigo(materiaPrima.getCodigo());
        dto.setNombre(materiaPrima.getNombre());
        dto.setDescripcion(materiaPrima.getDescripcion());
        dto.setUnidadMedida(materiaPrima.getUnidadMedida());
        dto.setPrecio(materiaPrima.getPrecio());
        dto.setStockDisponible(materiaPrima.getStockDisponible());
        dto.setStockMinimo(materiaPrima.getStockMinimo());
        dto.setStockMaximo(materiaPrima.getStockMaximo());
        dto.setFechaVencimiento(materiaPrima.getFechaVencimiento());


        if (materiaPrima.getEstado() != null) {
            dto.setEstadoId(materiaPrima.getEstado().getEstadoId());
        }
        if (materiaPrima.getProveedor() != null) { // Proveedor general
            dto.setProveedorId(materiaPrima.getProveedor().getProveedorId());
        }
        if (materiaPrima.getCategoria() != null) {
            dto.setCategoriaId(materiaPrima.getCategoria().getCategoriaId());
        }
        if (materiaPrima.getBodega() != null) {
            dto.setBodegaId(materiaPrima.getBodega().getBodegaId());
        }
        if (materiaPrima.getUsuario() != null) {
            dto.setUsuarioId(materiaPrima.getUsuario().getUsuarioId());
        }

        // Mapeo de atributos específicos de MateriaPrima
        if (materiaPrima.getTipoMaterial() != null) {
            dto.setTipoMaterial(materiaPrima.getTipoMaterial().toString());
        } else {
            dto.setTipoMaterial(null);
        }

        dto.setAnchoRollo(materiaPrima.getAnchoRollo());
        dto.setPesoMetro(materiaPrima.getPesoMetro());

        // Mapeo del ID de proveedor específico de tela
        if (materiaPrima.getProveedorTela() != null) {
            dto.setProveedorTelaId(materiaPrima.getProveedorTela().getProveedorId());
        }


        return dto;
    }

    // Método para convertir un MateriaPrimaDTO a una entidad MateriaPrima.
    // Requiere las entidades relacionadas ya cargadas.
    public MateriaPrima toEntity(MateriaPrimaDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedorGeneral, Usuario usuario, Proveedor proveedorTela) {
        MateriaPrima materiaPrima = new MateriaPrima();
        // Mapeo de atributos heredados de Item
        materiaPrima.setCodigo(dto.getCodigo());
        materiaPrima.setNombre(dto.getNombre());
        materiaPrima.setDescripcion(dto.getDescripcion());
        materiaPrima.setUnidadMedida(dto.getUnidadMedida());
        materiaPrima.setPrecio(dto.getPrecio());
        materiaPrima.setStockDisponible(dto.getStockDisponible());
        materiaPrima.setStockMinimo(dto.getStockMinimo());
        materiaPrima.setStockMaximo(dto.getStockMaximo());
        materiaPrima.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones con las entidades cargadas (heredadas)
        materiaPrima.setBodega(bodega);
        materiaPrima.setCategoria(categoria);
        materiaPrima.setEstado(estado);
        materiaPrima.setProveedor(proveedorGeneral); // Proveedor general
        materiaPrima.setUsuario(usuario);

        // Mapeo del String a enum TipoMaterial
        if (dto.getTipoMaterial() != null && !dto.getTipoMaterial().isEmpty()) {
            try {
                materiaPrima.setTipoMaterial(TipoMaterial.valueOf(dto.getTipoMaterial()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de material inválido: " + dto.getTipoMaterial(), e);
            }
        } else {
            materiaPrima.setTipoMaterial(null); // o manejar como error si es obligatorio
        }


        // Mapeo de atributos específicos de MateriaPrima
        materiaPrima.setAnchoRollo(dto.getAnchoRollo());
        materiaPrima.setPesoMetro(dto.getPesoMetro());

        // Establecer relación con proveedor específico de tela (opcional)
        materiaPrima.setProveedorTela(proveedorTela);


        // El tipo de item (MATERIA_PRIMA) se establece en el constructor
        // La fecha de ingreso se establece automáticamente
        // El itemId se genera automáticamente

        return materiaPrima;
    }

    // Método para actualizar una entidad MateriaPrima existente a partir de un MateriaPrimaDTO.
    // Requiere las entidades relacionadas ya cargadas.
    public void updateEntityFromDTO(MateriaPrimaDTO dto, MateriaPrima materiaPrima, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedorGeneral, Usuario usuario, Proveedor proveedorTela) {
        // Actualizar atributos heredados de Item
        materiaPrima.setCodigo(dto.getCodigo());
        materiaPrima.setNombre(dto.getNombre());
        materiaPrima.setDescripcion(dto.getDescripcion());
        materiaPrima.setUnidadMedida(dto.getUnidadMedida());
        materiaPrima.setPrecio(dto.getPrecio());
        materiaPrima.setStockDisponible(dto.getStockDisponible());
        materiaPrima.setStockMinimo(dto.getStockMinimo());
        materiaPrima.setStockMaximo(dto.getStockMaximo());
        materiaPrima.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones heredadas
        materiaPrima.setBodega(bodega);
        materiaPrima.setCategoria(categoria);
        materiaPrima.setEstado(estado);
        materiaPrima.setProveedor(proveedorGeneral); // Proveedor general
        materiaPrima.setUsuario(usuario);

        // Actualizar el enum TipoMaterial
        if (dto.getTipoMaterial() != null && !dto.getTipoMaterial().isEmpty()) {
            try {
                materiaPrima.setTipoMaterial(TipoMaterial.valueOf(dto.getTipoMaterial()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de material inválido: " + dto.getTipoMaterial(), e);
            }
        } else {
            materiaPrima.setTipoMaterial(null); // o manejar como error
        }


        // Actualizar atributos específicos de MateriaPrima
        materiaPrima.setAnchoRollo(dto.getAnchoRollo());
        materiaPrima.setPesoMetro(dto.getPesoMetro());

        // Actualizar relación con proveedor específico de tela (opcional)
        materiaPrima.setProveedorTela(proveedorTela);

        // La fecha de ingreso no se actualiza
    }
}