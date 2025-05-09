// src/main/java/com/telastech360/crmTT360/mapper/FacturaMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.entity.Pedido; // Necesario si mapeas pedidoId
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Factura}
 * y sus correspondientes DTOs ({@link FacturaDTO}).
 */
@Component
public class FacturaMapper {

    /**
     * Convierte una entidad {@link Factura} a un {@link FacturaDTO}.
     * Mapea el ID de la factura y el ID del pedido.
     *
     * @param factura La entidad Factura a convertir. Si es null, retorna null.
     * @return El DTO {@link FacturaDTO} poblado, o null si la entrada fue null.
     */
    public FacturaDTO toDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();

        // --- CAMBIO: Descomentado/Añadido mapeo de IDs ---
        dto.setFacturaId(factura.getFacturaId()); // Mapea el ID de la factura

        if (factura.getPedido() != null) {
            dto.setPedidoId(factura.getPedido().getPedidoId()); // Mapea el ID del pedido
        }
        // ----------------------------------------------

        dto.setTipoMovimiento(factura.getTipoMovimiento());
        dto.setTotal(factura.getTotal());
        dto.setFechaCreacion(factura.getFechaCreacion());
        dto.setEstadoPago(factura.isEstadoPago()); // Mapea el estado de pago

        return dto;
    }

    /**
     * Convierte un {@link FacturaDTO} a una entidad {@link Factura} básica.
     * <strong>Importante:</strong> No asigna la entidad Pedido.
     * La lógica de buscar el Pedido por el pedidoId del DTO
     * debe realizarse en la capa de servicio.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad Factura parcialmente poblada (sin la relación Pedido),
     * o null si la entrada fue null.
     */
    public Factura toEntity(FacturaDTO dto) {
        if (dto == null) return null;
        Factura factura = new Factura();
        // El ID de la factura no se mapea (autogenerado)

        // La relación con Pedido se establece en el servicio buscando por dto.getPedidoId()
        // factura.setPedido(pedidoEncontrado);

        factura.setTipoMovimiento(dto.getTipoMovimiento());
        factura.setTotal(dto.getTotal());
        factura.setFechaCreacion(dto.getFechaCreacion());
        factura.setEstadoPago(dto.isEstadoPago()); // Mapea el estado de pago

        return factura;
    }

    /**
     * Actualiza los campos de una entidad {@link Factura} existente desde un {@link FacturaDTO}.
     * <strong>Importante:</strong> No actualiza la relación con el Pedido.
     *
     * @param dto El DTO {@link FacturaDTO} con los datos actualizados.
     * @param factura La entidad {@link Factura} a actualizar. No realiza ninguna acción si alguno es null.
     */
    public void updateEntityFromDTO(FacturaDTO dto, Factura factura) {
        if (dto == null || factura == null) return;

        // No se actualiza el ID de la factura ni el pedido aquí
        factura.setTipoMovimiento(dto.getTipoMovimiento());
        factura.setTotal(dto.getTotal());
        factura.setFechaCreacion(dto.getFechaCreacion());
        factura.setEstadoPago(dto.isEstadoPago()); // Actualiza el estado de pago
    }
}