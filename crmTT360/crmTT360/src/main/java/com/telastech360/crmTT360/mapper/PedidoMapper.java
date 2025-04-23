package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PedidoDTO;
import com.telastech360.crmTT360.dto.PedidoDetalleDTO; // Importar PedidoDetalleDTO
import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.PedidoDetalle; // Importar PedidoDetalle
import org.springframework.beans.factory.annotation.Autowired; // Importar Autowired
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List; // Importar List
import java.util.ArrayList; // Importar ArrayList

// Mapper para convertir entre la entidad Pedido y el DTO PedidoDTO.
@Component
public class PedidoMapper {

    private final PedidoDetalleMapper pedidoDetalleMapper; // Inyectar el mapper de detalles

    @Autowired // Inyectar el mapper de detalles
    public PedidoMapper(PedidoDetalleMapper pedidoDetalleMapper) {
        this.pedidoDetalleMapper = pedidoDetalleMapper;
    }

    // Método para convertir una entidad Pedido a un PedidoDTO
    public PedidoDTO toDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setPedidoId(pedido.getPedidoId());
        if (pedido.getCliente() != null) {
            // Corregido: Usar getClienteId() en lugar de getClienteld()
            dto.setClienteId(pedido.getCliente().getClienteId());
        }
        if (pedido.getEstado() != null) {
            dto.setEstadoId(pedido.getEstado().getEstadoId());
        }
        // La fecha de pedido no se incluye en el DTO de respuesta si no es necesaria para el cliente

        // Mapear la lista de PedidoDetalle a lista de PedidoDetalleDTO
        if (pedido.getDetalles() != null) {
            List<PedidoDetalleDTO> detallesDTO = pedido.getDetalles().stream()
                    .map(pedidoDetalleMapper::toDTO) // Usar el mapper de detalles
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        } else {
            // Corregido: ArrayList ahora está importado
            dto.setDetalles(new ArrayList<>()); // Retornar lista vacía si no hay detalles
        }


        return dto;
    }

    // Método para convertir un PedidoDTO a una entidad Pedido.
    // Requiere las entidades ClienteInterno y Estado ya cargadas.
    // NOTA: Este método NO mapea los detalles del pedido. Los detalles
    // se manejan por separado en el servicio.
    public Pedido toEntity(PedidoDTO dto, ClienteInterno cliente, Estado estado) {
        Pedido pedido = new Pedido();
        // El pedidoId no se setea aquí (se genera automáticamente)
        pedido.setCliente(cliente);
        pedido.setEstado(estado);
        // La fecha de pedido se establece automáticamente en la entidad
        // Los detalles se añaden en el servicio
        return pedido;
    }

    // Método para actualizar una entidad Pedido existente a partir de un PedidoDTO.
    // Requiere las entidades ClienteInterno y Estado ya cargadas.
    // NOTA: Este método NO actualiza los detalles del pedido. Los detalles
    // se manejan por separado en el servicio (agregar, eliminar, actualizar).
    public void updateEntityFromDTO(PedidoDTO dto, Pedido pedido, ClienteInterno cliente, Estado estado) {
        // El pedidoId no se actualiza
        pedido.setCliente(cliente);
        pedido.setEstado(estado);
    }
}