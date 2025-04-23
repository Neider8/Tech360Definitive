package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PedidoDetalleDTO;
import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class PedidoDetalleMapper {

    public PedidoDetalleDTO toDTO(PedidoDetalle pedidoDetalle) {
        PedidoDetalleDTO dto = new PedidoDetalleDTO();
        if (pedidoDetalle.getProducto() != null) {
            dto.setItemId(pedidoDetalle.getProducto().getItemId());
        }
        dto.setCantidad(pedidoDetalle.getCantidad());
        dto.setPrecioUnitario(pedidoDetalle.getPrecioUnitario());
        return dto;
    }

    public PedidoDetalle toEntity(PedidoDetalleDTO dto, Producto producto) {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setProducto(producto);
        pedidoDetalle.setCantidad(dto.getCantidad());
        pedidoDetalle.setPrecioUnitario(dto.getPrecioUnitario());
        return pedidoDetalle;
    }

    public void updateEntityFromDTO(PedidoDetalleDTO dto, PedidoDetalle pedidoDetalle, Producto producto) {
        pedidoDetalle.setCantidad(dto.getCantidad());
        pedidoDetalle.setPrecioUnitario(dto.getPrecioUnitario());
        // Nota: normalmente no se cambia el producto en un pedido ya registrado.
    }
}
