// src/main/java/com/telastech360/crmTT360/mapper/ProductoMapper.java
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

/**
 * Componente Mapper responsable de convertir entre entidades {@link Producto}
 * y sus correspondientes DTOs ({@link ProductoDTO}).
 * Maneja tanto los campos comunes heredados de Item como los específicos de Producto.
 */
@Component
public class ProductoMapper {

    /**
     * Convierte una entidad {@link Producto} a un {@link ProductoDTO}.
     * Incluye campos base de Item y campos específicos de Producto.
     * Mapea IDs de entidades relacionadas y convierte enums a Strings.
     *
     * @param producto La entidad Producto a convertir. Si es null, retorna null.
     * @return El DTO {@link ProductoDTO} poblado, o null si la entrada fue null.
     */
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        ProductoDTO dto = new ProductoDTO();

        // --- Mapeo de atributos heredados de Item ---
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

        // Mapeo de IDs de relaciones heredadas
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

        // --- Mapeo de atributos específicos de Producto ---
        // Mapeo del enum TipoPrenda a String
        if (producto.getTipoPrenda() != null) {
            dto.setTipoPrenda(producto.getTipoPrenda().name());
        } else {
            dto.setTipoPrenda(null);
        }
        // Mapeo del enum Talla a String
        if (producto.getTalla() != null) {
            dto.setTalla(producto.getTalla().name());
        } else {
            dto.setTalla(null);
        }
        dto.setColor(producto.getColor());
        dto.setTemporada(producto.getTemporada());
        // dto.setComposicion(producto.getComposicion()); // Descomentar si existe en DTO
        dto.setFechaFabricacion(producto.getFechaFabricacion());

        return dto;
    }

    /**
     * Convierte un {@link ProductoDTO} a una entidad {@link Producto}.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.)
     * ya cargadas. Esta carga debe hacerse en el servicio.
     * Valida los valores de `tipoPrenda` y `talla`.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @param bodega La entidad {@link Bodega} asociada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada (ya cargada).
     * @param estado La entidad {@link Estado} asociada (ya cargada).
     * @param proveedor La entidad {@link Proveedor} general asociada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada (ya cargada).
     * @return Una entidad Producto poblada, o null si el DTO fue null.
     * @throws InvalidDataException si los valores de `tipoPrenda` o `talla` en el DTO no son válidos.
     * @throws NullPointerException si alguna de las entidades relacionadas obligatorias es null.
     */
    public Producto toEntity(ProductoDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        if (dto == null) {
            return null;
        }
        // Validar relaciones base obligatorias
        if (bodega == null || categoria == null || estado == null || proveedor == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas base (Bodega, Categoria, Estado, Proveedor, Usuario) no pueden ser null al mapear ProductoDTO a Entidad.");
        }

        Producto producto = new Producto();
        // El itemId se genera en la BD

        // --- Mapeo de atributos heredados de Item ---
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecio(dto.getPrecio());
        producto.setStockDisponible(dto.getStockDisponible());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockMaximo(dto.getStockMaximo());
        producto.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones base con las entidades cargadas
        producto.setBodega(bodega);
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setProveedor(proveedor);
        producto.setUsuario(usuario);

        // --- Mapeo de atributos específicos de Producto ---
        // Mapeo del String a enum TipoPrenda con validación
        if (dto.getTipoPrenda() != null && !dto.getTipoPrenda().isEmpty()) {
            try {
                producto.setTipoPrenda(TipoPrenda.valueOf(dto.getTipoPrenda().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de prenda inválido: " + dto.getTipoPrenda());
            }
        } else {
            throw new InvalidDataException("Tipo de prenda es obligatorio.");
        }

        // Mapeo del String a enum Talla con validación
        if (dto.getTalla() != null && !dto.getTalla().isEmpty()) {
            try {
                producto.setTalla(Talla.valueOf(dto.getTalla().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Talla inválida: " + dto.getTalla());
            }
        } else {
            throw new InvalidDataException("Talla es obligatoria.");
        }

        producto.setColor(dto.getColor());
        producto.setTemporada(dto.getTemporada());
        // producto.setComposicion(dto.getComposicion()); // Descomentar si existe en DTO
        producto.setFechaFabricacion(dto.getFechaFabricacion());

        // El tipo de item (PRODUCTO_TERMINADO) se establece en el constructor de Producto
        // La fecha de ingreso se establece automáticamente

        return producto;
    }

    /**
     * Actualiza una entidad {@link Producto} existente a partir de un {@link ProductoDTO}.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.)
     * ya cargadas. Esta carga debe hacerse en el servicio.
     * Valida los valores de `tipoPrenda` y `talla`.
     *
     * @param dto El DTO {@link ProductoDTO} con los datos actualizados.
     * @param producto La entidad {@link Producto} a actualizar.
     * @param bodega La entidad {@link Bodega} asociada actualizada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada actualizada (ya cargada).
     * @param estado La entidad {@link Estado} asociada actualizada (ya cargada).
     * @param proveedor La entidad {@link Proveedor} general asociada actualizada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada actualizada (ya cargada).
     * @throws InvalidDataException si los valores de `tipoPrenda` o `talla` en el DTO no son válidos.
     * @throws NullPointerException si alguna de las entidades relacionadas obligatorias o el DTO/entidad son null.
     */
    public void updateEntityFromDTO(ProductoDTO dto, Producto producto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        if (dto == null || producto == null) {
            return; // No hacer nada
        }
        // Validar relaciones base obligatorias
        if (bodega == null || categoria == null || estado == null || proveedor == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas base no pueden ser null al actualizar Producto.");
        }

        // --- Actualizar atributos heredados de Item ---
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecio(dto.getPrecio());
        producto.setStockDisponible(dto.getStockDisponible());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockMaximo(dto.getStockMaximo());
        producto.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones base
        producto.setBodega(bodega);
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setProveedor(proveedor);
        producto.setUsuario(usuario);

        // --- Actualizar atributos específicos de Producto ---
        // Actualizar el enum TipoPrenda con validación
        if (dto.getTipoPrenda() != null && !dto.getTipoPrenda().isEmpty()) {
            try {
                producto.setTipoPrenda(TipoPrenda.valueOf(dto.getTipoPrenda().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de prenda inválido: " + dto.getTipoPrenda());
            }
        } else {
            throw new InvalidDataException("Tipo de prenda es obligatorio durante la actualización.");
        }

        // Actualizar el enum Talla con validación
        if (dto.getTalla() != null && !dto.getTalla().isEmpty()) {
            try {
                producto.setTalla(Talla.valueOf(dto.getTalla().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Talla inválida: " + dto.getTalla());
            }
        } else {
            throw new InvalidDataException("Talla es obligatoria durante la actualización.");
        }

        producto.setColor(dto.getColor());
        producto.setTemporada(dto.getTemporada());
        // producto.setComposicion(dto.getComposicion()); // Descomentar si existe
        producto.setFechaFabricacion(dto.getFechaFabricacion());

        // La fecha de ingreso y el ID no se actualizan
    }
}