// src/main/java/com/telastech360/crmTT360/mapper/ItemMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ItemDTO;
import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.Item.TipoItem; // Importar el enum TipoItem
import com.telastech360.crmTT360.exception.InvalidDataException; // Importar InvalidDataException
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre la entidad base {@link Item}
 * y su correspondiente DTO genérico ({@link ItemDTO}).
 * Facilita la transferencia de datos comunes a todos los tipos de ítems.
 */
@Component
public class ItemMapper {

    /**
     * Convierte una entidad {@link Item} (o sus subclases) a un {@link ItemDTO} genérico.
     * Mapea los campos comunes, el ID del ítem y los IDs de las relaciones.
     *
     * @param item La entidad Item (o Producto, MateriaPrima) a convertir. Si es null, retorna null.
     * @return El DTO {@link ItemDTO} poblado con los datos comunes, o null si la entrada fue null.
     */
    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }
        ItemDTO dto = new ItemDTO();

        // --- CAMBIO: Añadido mapeo de itemId ---
        dto.setItemId(item.getItemId()); // Copiar el ID de la entidad al DTO

        // Mapeo de atributos directos (existentes)
        dto.setCodigo(item.getCodigo());
        dto.setNombre(item.getNombre());
        dto.setDescripcion(item.getDescripcion());
        dto.setUnidadMedida(item.getUnidadMedida());
        dto.setPrecio(item.getPrecio());
        dto.setStockDisponible(item.getStockDisponible());
        dto.setStockMinimo(item.getStockMinimo());
        dto.setStockMaximo(item.getStockMaximo());
        dto.setFechaVencimiento(item.getFechaVencimiento());

        // Mapeo de IDs de relaciones (existentes)
        if (item.getEstado() != null) {
            dto.setEstadoId(item.getEstado().getEstadoId());
        }
        if (item.getProveedor() != null) {
            dto.setProveedorId(item.getProveedor().getProveedorId());
        }
        if (item.getCategoria() != null) {
            dto.setCategoriaId(item.getCategoria().getCategoriaId());
        }
        if (item.getBodega() != null) {
            dto.setBodegaId(item.getBodega().getBodegaId());
        }
        if (item.getUsuario() != null) {
            dto.setUsuarioId(item.getUsuario().getUsuarioId());
        }

        // Mapeo del enum TipoItem a String (existente)
        if (item.getTipoItem() != null) {
            dto.setTipoItem(item.getTipoItem().name());
        } else {
            dto.setTipoItem(null);
        }

        return dto;
    }

    /**
     * Convierte un {@link ItemDTO} a una entidad {@link Item} base.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.)
     * ya cargadas para establecer las asociaciones. Esta carga debe hacerse en el servicio.
     * Valida el valor de `tipoItem` en el DTO.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @param bodega La entidad {@link Bodega} asociada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada (ya cargada).
     * @param estado La entidad {@link Estado} asociada (ya cargada).
     * @param proveedor La entidad {@link Proveedor} asociada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada (ya cargada).
     * @return Una entidad Item poblada con datos comunes y relaciones, o null si el DTO fue null.
     * @throws InvalidDataException si el valor de `tipoItem` en el DTO no es válido.
     * @throws NullPointerException si alguna de las entidades relacionadas requeridas es null.
     */
    public Item toEntity(ItemDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        if (dto == null) {
            return null;
        }
        // Validar que las entidades relacionadas no sean null
        if (bodega == null || categoria == null || estado == null || proveedor == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas (Bodega, Categoria, Estado, Proveedor, Usuario) no pueden ser null al mapear ItemDTO a Entidad.");
        }

        Item item = new Item();
        // El ID se genera en la BD

        // Mapeo de atributos directos
        item.setCodigo(dto.getCodigo());
        item.setNombre(dto.getNombre());
        item.setDescripcion(dto.getDescripcion());
        item.setUnidadMedida(dto.getUnidadMedida());
        item.setPrecio(dto.getPrecio());
        item.setStockDisponible(dto.getStockDisponible());
        item.setStockMinimo(dto.getStockMinimo());
        item.setStockMaximo(dto.getStockMaximo());
        item.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones con las entidades ya cargadas
        item.setBodega(bodega);
        item.setCategoria(categoria);
        item.setEstado(estado);
        item.setProveedor(proveedor);
        item.setUsuario(usuario);

        // Mapeo del String a enum TipoItem con validación robusta
        if (dto.getTipoItem() != null && !dto.getTipoItem().isEmpty()) {
            try {
                item.setTipoItem(TipoItem.valueOf(dto.getTipoItem().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de item inválido: " + dto.getTipoItem() +
                        ". Valores permitidos: MATERIA_PRIMA, PRODUCTO_TERMINADO");
            }
        } else {
            throw new InvalidDataException("El tipo de item es obligatorio.");
        }

        return item;
    }

    /**
     * Actualiza los campos comunes de una entidad {@link Item} existente desde un {@link ItemDTO}.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.)
     * ya cargadas para actualizar las asociaciones. Esta carga debe hacerse en el servicio.
     * Valida el valor de `tipoItem` en el DTO.
     *
     * @param dto El DTO {@link ItemDTO} con los datos actualizados.
     * @param item La entidad {@link Item} a actualizar.
     * @param bodega La entidad {@link Bodega} asociada actualizada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada actualizada (ya cargada).
     * @param estado La entidad {@link Estado} asociada actualizada (ya cargada).
     * @param proveedor La entidad {@link Proveedor} asociada actualizada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada actualizada (ya cargada).
     * @throws InvalidDataException si el valor de `tipoItem` en el DTO no es válido.
     * @throws NullPointerException si alguna de las entidades relacionadas requeridas es null.
     */
    public void updateEntityFromDTO(ItemDTO dto, Item item, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        if (dto == null || item == null) {
            return; // No hacer nada si no hay datos o entidad
        }
        // Validar que las entidades relacionadas no sean null
        if (bodega == null || categoria == null || estado == null || proveedor == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas (Bodega, Categoria, Estado, Proveedor, Usuario) no pueden ser null al actualizar la entidad Item.");
        }

        // Actualizar atributos directos
        item.setCodigo(dto.getCodigo());
        item.setNombre(dto.getNombre());
        item.setDescripcion(dto.getDescripcion());
        item.setUnidadMedida(dto.getUnidadMedida());
        item.setPrecio(dto.getPrecio());
        item.setStockDisponible(dto.getStockDisponible());
        item.setStockMinimo(dto.getStockMinimo());
        item.setStockMaximo(dto.getStockMaximo());
        item.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones con las entidades ya cargadas
        item.setBodega(bodega);
        item.setCategoria(categoria);
        item.setEstado(estado);
        item.setProveedor(proveedor);
        item.setUsuario(usuario);

        // Actualizar el enum TipoItem con validación robusta
        if (dto.getTipoItem() != null && !dto.getTipoItem().isEmpty()) {
            try {
                item.setTipoItem(TipoItem.valueOf(dto.getTipoItem().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de item inválido: " + dto.getTipoItem() +
                        ". Valores permitidos: MATERIA_PRIMA, PRODUCTO_TERMINADO");
            }
        } else {
            throw new InvalidDataException("El tipo de item no puede ser nulo durante la actualización.");
        }
        // El ID no se actualiza
    }
}