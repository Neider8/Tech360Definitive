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
import org.springframework.stereotype.Component; // Añadir la anotación @Component

// Mapper para convertir entre la entidad Item y el DTO ItemDTO.
// Se utiliza para desacoplar la capa de servicio de la entidad JPA.
@Component // Anotación para que Spring gestione este Mapper como un bean
public class ItemMapper {

    // Método para convertir una entidad Item a un ItemDTO
    public ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        // Mapeo de atributos directos
        dto.setCodigo(item.getCodigo());
        dto.setNombre(item.getNombre());
        dto.setDescripcion(item.getDescripcion());
        dto.setUnidadMedida(item.getUnidadMedida());
        dto.setPrecio(item.getPrecio()); // Usar getPrecio() según la entidad Item
        dto.setStockDisponible(item.getStockDisponible()); // Usar getStockDisponible()
        dto.setStockMinimo(item.getStockMinimo());
        dto.setStockMaximo(item.getStockMaximo());
        dto.setFechaVencimiento(item.getFechaVencimiento());


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

        // Mapeo del enum TipoItem a String - Verificación para evitar NullPointerException
        if (item.getTipoItem() != null) {
            dto.setTipoItem(item.getTipoItem().toString());
        } else {
            dto.setTipoItem(null);
        }

        return dto;
    }

    // Método para convertir un ItemDTO a una entidad Item.
    // Requiere las entidades relacionadas ya cargadas para establecer las relaciones.
    public Item toEntity(ItemDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        Item item = new Item();
        // Mapeo de atributos directos
        item.setCodigo(dto.getCodigo());
        item.setNombre(dto.getNombre());
        item.setDescripcion(dto.getDescripcion());
        item.setUnidadMedida(dto.getUnidadMedida());
        item.setPrecio(dto.getPrecio()); // Usar setPrecio()
        item.setStockDisponible(dto.getStockDisponible()); // Usar setStockDisponible()
        item.setStockMinimo(dto.getStockMinimo());
        item.setStockMaximo(dto.getStockMaximo());
        item.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones con las entidades ya cargadas
        item.setBodega(bodega);
        item.setCategoria(categoria);
        item.setEstado(estado);
        item.setProveedor(proveedor);
        item.setUsuario(usuario);

        // Mapeo del String a enum TipoItem - Verificación para evitar NullPointerException
        if (dto.getTipoItem() != null && !dto.getTipoItem().isEmpty()) {
            try {
                item.setTipoItem(TipoItem.valueOf(dto.getTipoItem()));
            } catch (IllegalArgumentException e) {
                // Lanzar una excepción si el tipo de item del DTO no es válido
                throw new InvalidDataException("Tipo de item inválido: " + dto.getTipoItem(), e);
            }
        } else {
            item.setTipoItem(null); // o manejar como error si el tipo de item es obligatorio
        }


        // La fecha de ingreso (fechaCreacion) se establece automáticamente en la entidad
        // El itemId se genera automáticamente al guardar

        return item;
    }

    // Método para actualizar una entidad Item existente a partir de un ItemDTO.
    // Requiere las entidades relacionadas ya cargadas para actualizar las relaciones.
    public void updateEntityFromDTO(ItemDTO dto, Item item, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedor, Usuario usuario) {
        // Actualizar atributos directos
        item.setCodigo(dto.getCodigo());
        item.setNombre(dto.getNombre());
        item.setDescripcion(dto.getDescripcion());
        item.setUnidadMedida(dto.getUnidadMedida());
        item.setPrecio(dto.getPrecio()); // Usar setPrecio()
        item.setStockDisponible(dto.getStockDisponible()); // Usar setStockDisponible()
        item.setStockMinimo(dto.getStockMinimo());
        item.setStockMaximo(dto.getStockMaximo());
        item.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones con las entidades ya cargadas
        item.setBodega(bodega);
        item.setCategoria(categoria);
        item.setEstado(estado);
        item.setProveedor(proveedor);
        item.setUsuario(usuario);

        // Actualizar el enum TipoItem - Verificación para evitar NullPointerException
        if (dto.getTipoItem() != null && !dto.getTipoItem().isEmpty()) {
            try {
                item.setTipoItem(TipoItem.valueOf(dto.getTipoItem()));
            } catch (IllegalArgumentException e) {
                // Lanzar una excepción si el tipo de item del DTO no es válido
                throw new InvalidDataException("Tipo de item inválido: " + dto.getTipoItem(), e);
            }
        } else {
            item.setTipoItem(null); // o manejar como error si el tipo de item es obligatorio
        }
    }
}