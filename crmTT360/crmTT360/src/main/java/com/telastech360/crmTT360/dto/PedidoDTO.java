package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.media.Schema; // Importar Schema

// DTO para la entidad Pedido.
public class PedidoDTO {

    private Long pedidoId; // Incluir ID para operaciones de actualización/respuesta

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID del estado del pedido es obligatorio")
    private Long estadoId;

    // La fecha de pedido se genera en el backend, no se recibe del cliente en la creación

    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Schema(description = "Lista de detalles del pedido", implementation = PedidoDetalleDTO.class) // <--- ANOTACIÓN AGREGADA AQUÍ
    private List<PedidoDetalleDTO> detalles = new ArrayList<>();

    // No incluimos FacturaDTO aquí, ya que las facturas se generan a partir del pedido.

    // --- Constructores ---
    public PedidoDTO() {
    }

    public PedidoDTO(Long pedidoId, Long clienteId, Long estadoId, List<PedidoDetalleDTO> detalles) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.estadoId = estadoId;
        this.detalles = detalles;
    }

    // --- Getters y Setters ---
    // (Se generan automáticamente o se añaden aquí)

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public List<PedidoDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDetalleDTO> detalles) {
        this.detalles = detalles;
    }
}