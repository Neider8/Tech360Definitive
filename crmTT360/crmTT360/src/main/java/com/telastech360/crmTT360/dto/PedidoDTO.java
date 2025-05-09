// src/main/java/com/telastech360/crmTT360/dto/PedidoDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para representar la información de un Pedido.
 * Contiene la información principal del pedido (cliente, estado) y una lista
 * de sus detalles ({@link PedidoDetalleDTO}).
 */
public class PedidoDTO {

    private Long pedidoId; // ID del pedido, útil para respuestas o actualizaciones

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId; // ID del ClienteInterno asociado

    @NotNull(message = "El ID del estado del pedido es obligatorio")
    private Long estadoId; // ID del Estado del pedido

    // La fecha de pedido se genera en el backend, no se recibe del cliente en la creación
    // private Timestamp fechaPedido;

    // La fecha de fin podría incluirse si es relevante para la API
    // private Timestamp fechaFin;

    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Schema(description = "Lista de detalles (ítems) incluidos en el pedido.")
    private List<PedidoDetalleDTO> detalles = new ArrayList<>(); // Lista de detalles del pedido

    // No incluimos FacturaDTO aquí, ya que las facturas se generan a partir del pedido.

    // --- Constructores ---
    /**
     * Constructor por defecto.
     */
    public PedidoDTO() {
    }

    /**
     * Constructor con parámetros.
     * @param pedidoId ID del pedido.
     * @param clienteId ID del cliente asociado.
     * @param estadoId ID del estado del pedido.
     * @param detalles Lista de detalles del pedido.
     */
    public PedidoDTO(Long pedidoId, Long clienteId, Long estadoId, List<PedidoDetalleDTO> detalles) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.estadoId = estadoId;
        this.detalles = detalles;
    }

    // --- Getters y Setters ---

    /**
     * Obtiene el ID del pedido.
     * @return El ID del pedido.
     */
    public Long getPedidoId() {
        return pedidoId;
    }

    /**
     * Establece el ID del pedido.
     * @param pedidoId El nuevo ID del pedido.
     */
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    /**
     * Obtiene el ID del cliente asociado al pedido.
     * @return El ID del cliente.
     */
    public Long getClienteId() {
        return clienteId;
    }

    /**
     * Establece el ID del cliente asociado al pedido.
     * @param clienteId El nuevo ID del cliente.
     */
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    /**
     * Obtiene el ID del estado actual del pedido.
     * @return El ID del estado.
     */
    public Long getEstadoId() {
        return estadoId;
    }

    /**
     * Establece el ID del estado actual del pedido.
     * @param estadoId El nuevo ID del estado.
     */
    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    /**
     * Obtiene la lista de detalles (ítems) del pedido.
     * @return Una lista de {@link PedidoDetalleDTO}.
     */
    public List<PedidoDetalleDTO> getDetalles() {
        return detalles;
    }

    /**
     * Establece la lista de detalles (ítems) del pedido.
     * @param detalles La nueva lista de detalles.
     */
    public void setDetalles(List<PedidoDetalleDTO> detalles) {
        this.detalles = detalles;
    }
}