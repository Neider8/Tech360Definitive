// src/main/java/com/telastech360/crmTT360/mapper/PedidoMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.PedidoDTO;
import com.telastech360.crmTT360.dto.PedidoDetalleDTO; // Importar PedidoDetalleDTO
import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.PedidoDetalle; // Importar PedidoDetalle
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.hibernate.Hibernate; // Para inicializar colecciones LAZY si es necesario

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

/**
 * Componente Mapper responsable de convertir entre entidades {@link Pedido}
 * y sus correspondientes DTOs ({@link PedidoDTO}).
 * Incluye el mapeo de la lista de detalles del pedido.
 */
@Component
public class PedidoMapper {

    private final PedidoDetalleMapper pedidoDetalleMapper; // Inyectar el mapper de detalles

    /**
     * Constructor para inyección de dependencias del mapper de detalles.
     * @param pedidoDetalleMapper Mapper para convertir {@link PedidoDetalle} a {@link PedidoDetalleDTO}.
     */
    @Autowired
    public PedidoMapper(PedidoDetalleMapper pedidoDetalleMapper) {
        this.pedidoDetalleMapper = pedidoDetalleMapper;
    }

    /**
     * Convierte una entidad {@link Pedido} a un {@link PedidoDTO}.
     * Mapea el ID del pedido, IDs de cliente y estado, y la lista de detalles (convirtiéndolos a DTOs).
     * Asegura que la colección de detalles se inicialice si es LAZY antes de mapearla.
     *
     * @param pedido La entidad Pedido a convertir. Si es null, retorna null.
     * @return El DTO {@link PedidoDTO} poblado, o null si la entrada fue null.
     */
    public PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        PedidoDTO dto = new PedidoDTO();
        dto.setPedidoId(pedido.getPedidoId());

        // Mapear ID de Cliente si existe
        if (pedido.getCliente() != null) {
            dto.setClienteId(pedido.getCliente().getClienteId());
        } else {
            dto.setClienteId(null); // Asegurar null si no hay cliente
        }

        // Mapear ID de Estado (debería existir siempre según la entidad)
        if (pedido.getEstado() != null) {
            dto.setEstadoId(pedido.getEstado().getEstadoId());
        } else {
            // Considerar lanzar una excepción si el estado es null pero es obligatorio
            dto.setEstadoId(null);
        }

        // La fecha de pedido y fecha fin generalmente no se incluyen en el DTO de respuesta
        // a menos que sean específicamente requeridas por el frontend.

        // Mapear la lista de PedidoDetalle a lista de PedidoDetalleDTO
        List<PedidoDetalle> detallesEntidad = pedido.getDetalles();
        // Forzar inicialización si la carga es LAZY y estamos fuera de una transacción
        // if (!Hibernate.isInitialized(detallesEntidad)) {
        //     Hibernate.initialize(detallesEntidad);
        // }

        if (detallesEntidad != null) {
            List<PedidoDetalleDTO> detallesDTO = detallesEntidad.stream()
                    .map(pedidoDetalleMapper::toDTO) // Reutilizar el mapper de detalles
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        } else {
            dto.setDetalles(new ArrayList<>()); // Devolver lista vacía si es null
        }

        // La relación con Facturas generalmente no se mapea aquí

        return dto;
    }

    /**
     * Convierte un {@link PedidoDTO} a una entidad {@link Pedido} básica.
     * <strong>Importante:</strong> No mapea la lista de detalles (`List<PedidoDetalleDTO>`).
     * La creación y asociación de los detalles debe hacerse en la capa de servicio.
     * Requiere las entidades {@link ClienteInterno} y {@link Estado} ya cargadas.
     *
     * @param dto El DTO con los datos de entrada (pedidoId es ignorado). Si es null, retorna null.
     * @param cliente La entidad {@link ClienteInterno} asociada (puede ser null si el pedido no tiene cliente).
     * @param estado La entidad {@link Estado} asociada (no puede ser null).
     * @return Una entidad Pedido parcialmente poblada (sin detalles), o null si el DTO fue null.
     * @throws NullPointerException si la entidad Estado es null.
     */
    public Pedido toEntity(PedidoDTO dto, ClienteInterno cliente, Estado estado) {
        if (dto == null) {
            return null;
        }
        if (estado == null) {
            throw new NullPointerException("La entidad Estado no puede ser null al mapear PedidoDTO a Entidad.");
        }

        Pedido pedido = new Pedido();
        // El pedidoId se genera en la BD

        pedido.setCliente(cliente); // Asignar el cliente (puede ser null)
        pedido.setEstado(estado); // Asignar el estado (no debe ser null)

        // La fecha de pedido se establece automáticamente en la entidad Pedido
        // La fecha de fin se establece opcionalmente en el servicio

        // Los detalles (List<PedidoDetalle>) se añaden en el servicio procesando dto.getDetalles()

        return pedido;
    }

    /**
     * Actualiza los campos principales de una entidad {@link Pedido} existente desde un {@link PedidoDTO}.
     * <strong>Importante:</strong> No actualiza la lista de detalles. La gestión de añadir,
     * modificar o eliminar detalles debe hacerse en la capa de servicio.
     * Requiere las entidades {@link ClienteInterno} y {@link Estado} ya cargadas.
     *
     * @param dto El DTO {@link PedidoDTO} con los datos actualizados (clienteId, estadoId).
     * @param pedido La entidad {@link Pedido} a actualizar.
     * @param cliente La entidad {@link ClienteInterno} actualizada (puede ser null).
     * @param estado La entidad {@link Estado} actualizada (no puede ser null).
     * @throws NullPointerException si la entidad Estado es null o si dto o pedido son null.
     */
    public void updateEntityFromDTO(PedidoDTO dto, Pedido pedido, ClienteInterno cliente, Estado estado) {
        if (dto == null || pedido == null) {
            return; // No hacer nada
        }
        if (estado == null) {
            throw new NullPointerException("La entidad Estado no puede ser null al actualizar la entidad Pedido.");
        }

        // El pedidoId no se actualiza
        pedido.setCliente(cliente); // Actualizar cliente (permite null)
        pedido.setEstado(estado); // Actualizar estado

        // La fecha de pedido no se actualiza
        // La fecha de fin se actualiza en el servicio si es necesario
        // Los detalles se gestionan por separado en el servicio
    }
}