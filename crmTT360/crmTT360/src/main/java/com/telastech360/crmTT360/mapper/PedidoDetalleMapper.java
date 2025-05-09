// src/main/java/com/telastech360/crmTT360/mapper/PedidoDetalleMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PedidoDetalleDTO;
import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.entity.Item; // Usar Item en lugar de Producto
import com.telastech360.crmTT360.entity.Pedido; // Necesario si se mapean relaciones completas
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link PedidoDetalle}
 * y sus correspondientes DTOs ({@link PedidoDetalleDTO}).
 * Maneja la información de un ítem específico dentro de un pedido.
 */
@Component
public class PedidoDetalleMapper {

    /**
     * Convierte una entidad {@link PedidoDetalle} a un {@link PedidoDetalleDTO}.
     * Mapea el ID del Item asociado, cantidad y precio unitario.
     *
     * @param pedidoDetalle La entidad PedidoDetalle a convertir. Si es null, retorna null.
     * @return El DTO {@link PedidoDetalleDTO} poblado, o null si la entrada fue null.
     */
    public PedidoDetalleDTO toDTO(PedidoDetalle pedidoDetalle) {
        if (pedidoDetalle == null) return null;

        PedidoDetalleDTO dto = new PedidoDetalleDTO();

        // Mapea el ID del Item asociado (el campo se llama 'producto' pero es de tipo Item)
        if (pedidoDetalle.getProducto() != null) {
            dto.setItemId(pedidoDetalle.getProducto().getItemId());
        } else {
            // Considerar lanzar excepción si el item es null pero no debería serlo
            dto.setItemId(null);
        }

        dto.setCantidad(pedidoDetalle.getCantidad());
        dto.setPrecioUnitario(pedidoDetalle.getPrecioUnitario());

        // El ID del pedido generalmente no se incluye en el DTO del detalle,
        // ya que se obtiene del contexto (ej. al listar detalles de un pedido específico).

        return dto;
    }

    /**
     * Convierte un {@link PedidoDetalleDTO} a una entidad {@link PedidoDetalle} básica.
     * <strong>Importante:</strong> No asigna la entidad Pedido asociada ni el ID compuesto.
     * Requiere la entidad {@link Item} correspondiente ya cargada.
     * La asignación del Pedido y la gestión del ID compuesto deben hacerse en el servicio.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @param item La entidad {@link Item} asociada (ya cargada). No puede ser null.
     * @return Una entidad PedidoDetalle parcialmente poblada (sin Pedido ni ID compuesto),
     * o null si el DTO fue null.
     * @throws NullPointerException si el Item proporcionado es null.
     */
    public PedidoDetalle toEntity(PedidoDetalleDTO dto, Item item) {
        if (dto == null) return null;
        if (item == null) {
            throw new NullPointerException("El Item no puede ser null al mapear PedidoDetalleDTO a Entidad.");
        }

        PedidoDetalle pedidoDetalle = new PedidoDetalle();

        // Asignar el Item (campo llamado 'producto' en la entidad)
        pedidoDetalle.setProducto(item);
        pedidoDetalle.setCantidad(dto.getCantidad());
        pedidoDetalle.setPrecioUnitario(dto.getPrecioUnitario());

        // El ID compuesto (pedidoId, itemId) y la relación con Pedido se establecen en el servicio
        // al agregar el detalle al pedido y guardarlo.

        return pedidoDetalle;
    }

    /**
     * Actualiza los campos de una entidad {@link PedidoDetalle} existente desde un {@link PedidoDetalleDTO}.
     * <strong>Importante:</strong> No actualiza la relación con Pedido ni con Item.
     * Se usa típicamente para modificar cantidad o precio de un detalle existente.
     *
     * @param dto El DTO {@link PedidoDetalleDTO} con los datos actualizados (cantidad, precioUnitario).
     * @param pedidoDetalle La entidad {@link PedidoDetalle} a actualizar.
     * @param item La entidad {@link Item} asociada (generalmente no se usa para actualizar, pero se incluye por consistencia si fuera necesario).
     * No realiza ninguna acción si dto o pedidoDetalle son null.
     */
    public void updateEntityFromDTO(PedidoDetalleDTO dto, PedidoDetalle pedidoDetalle, Item item) {
        if (dto == null || pedidoDetalle == null) return;

        // No se actualiza el item asociado (item) ni el pedido aquí normalmente.
        // El ID compuesto tampoco se actualiza.

        // Actualiza cantidad si se proporciona en el DTO
        if (dto.getCantidad() != null) {
            pedidoDetalle.setCantidad(dto.getCantidad());
        }
        // Actualiza precio unitario si se proporciona en el DTO
        if (dto.getPrecioUnitario() != null) {
            pedidoDetalle.setPrecioUnitario(dto.getPrecioUnitario());
        }
    }
}