package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ProductoDTO;
import com.telastech360.crmTT360.entity.Producto;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Producto.TipoPrenda; // Importar enum TipoPrenda
import com.telastech360.crmTT360.entity.Producto.Talla; // Importar enum Talla
import com.telastech360.crmTT360.exception.InvalidDataException; // Importar InvalidDataException
import org.springframework.stereotype.Component;

// Mapper para convertir entre la entidad Producto y el DTO ProductoDTO.
@Component
public class ProductoMapper {

    // Método para convertir una entidad Producto a un ProductoDTO
    public ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();

        // Mapeo de atributos heredados de Item
        dto.setItemId(producto.getItemId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setUnidadMedida(producto.getUnidadMedida());
        dto.setPrecio(producto.getPrecio());
        dto.setStockDisponible(producto.getStockDisponible());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setStockMaximo(producto.getStockMaximo());
        dto.setFechaVencimiento(producto.getFechaVencimiento());


        if (producto.getEstado() != null) {
            dto.setEstadoId(producto.getEstado().getEstadoId());
        }
        if (producto.getProveedor() != null) {
            dto.setProveedorId(producto.getProveedor().getProveedorId());
        }
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getCategoriaId());
        }
        if (producto.getBodega() != null) {
            dto.setBodegaId(producto.getBodega().getBodegaId());
        }
        if (producto.getUsuario() != null) {
            dto.setUsuarioId(producto.getUsuario().getUsuarioId());
        }

        // Mapeo de atributos específicos de Producto
        if (producto.getTipoPrenda() != null) {
            dto.setTipoPrenda(producto.getTipoPrenda().toString());
        } else {
            dto.setTipoPrenda(null);
        }
        if (producto.getTalla() != null) {
            dto.setTalla(producto.getTalla().toString());
        } else {
            dto.setTalla(null);
        }
        dto.setColor(producto.getColor());
        dto.setTemporada(producto.getTemporada());
        dto.setFechaFabricacion(producto.getFechaFabricacion());

        return dto;
    }

    // Método para convertir un ProductoDTO a una entidad Producto.
    // Requiere las entidades relacionadas (Bodega, Categoria, Estado, Proveedor, Usuario) ya cargadas.
    public Producto toEntity(ProductoDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        Producto producto = new Producto();

        // Mapeo de atributos heredados de Item
        // El itemId no se setea aquí (se genera automáticamente al guardar)
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecio(dto.getPrecio());
        producto.setStockDisponible(dto.getStockDisponible());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockMaximo(dto.getStockMaximo());
        producto.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones con las entidades cargadas (heredadas)
        producto.setBodega(bodega);
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setProveedor(proveedor);
        producto.setUsuario(usuario);

        // Mapeo del String a enum TipoPrenda
        if (dto.getTipoPrenda() != null && !dto.getTipoPrenda().isEmpty()) {
            try {
                producto.setTipoPrenda(TipoPrenda.valueOf(dto.getTipoPrenda()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de prenda inválido: " + dto.getTipoPrenda(), e);
            }
        } else {
            throw new InvalidDataException("Tipo de prenda es obligatorio"); // O manejar como error si es obligatorio
        }

        // Mapeo del String a enum Talla
        if (dto.getTalla() != null && !dto.getTalla().isEmpty()) {
            try {
                producto.setTalla(Talla.valueOf(dto.getTalla()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Talla inválida: " + dto.getTalla(), e);
            }
        } else {
            throw new InvalidDataException("Talla es obligatoria"); // O manejar como error si es obligatorio
        }


        // Mapeo de atributos específicos de Producto
        producto.setColor(dto.getColor());
        producto.setTemporada(dto.getTemporada());
        producto.setFechaFabricacion(dto.getFechaFabricacion());

        // El tipo de item (PRODUCTO_TERMINADO) ya se establece en el constructor de Producto

        return producto;
    }

    // Método para actualizar una entidad Producto existente a partir de un ProductoDTO.
    // Requiere las entidades relacionadas (Bodega, Categoria, Estado, Proveedor, Usuario) ya cargadas.
    public void updateEntityFromDTO(ProductoDTO dto, Producto producto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        // Actualizar atributos heredados de Item
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecio(dto.getPrecio());
        producto.setStockDisponible(dto.getStockDisponible());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockMaximo(dto.getStockMaximo());
        producto.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones heredadas
        producto.setBodega(bodega);
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setProveedor(proveedor);
        producto.setUsuario(usuario);

        // Actualizar el enum TipoPrenda
        if (dto.getTipoPrenda() != null && !dto.getTipoPrenda().isEmpty()) {
            try {
                producto.setTipoPrenda(TipoPrenda.valueOf(dto.getTipoPrenda()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de prenda inválido: " + dto.getTipoPrenda(), e);
            }
        } else {
            throw new InvalidDataException("Tipo de prenda es obligatorio"); // O manejar como error si es obligatorio
        }

        // Actualizar el enum Talla
        if (dto.getTalla() != null && !dto.getTalla().isEmpty()) {
            try {
                producto.setTalla(Talla.valueOf(dto.getTalla()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Talla inválida: " + dto.getTalla(), e);
            }
        } else {
            throw new InvalidDataException("Talla es obligatoria"); // O manejar como error si es obligatorio
        }


        // Actualizar atributos específicos de Producto
        producto.setColor(dto.getColor());
        producto.setTemporada(dto.getTemporada());
        producto.setFechaFabricacion(dto.getFechaFabricacion());

        // La fecha de ingreso no se actualiza
    }
}