package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

// DTO para los detalles de un pedido.
public class PedidoDetalleDTO {

    @NotNull(message = "El ID del ítem es obligatorio en el detalle del pedido")
    private Long itemId;

    @NotNull(message = "La cantidad es obligatoria en el detalle del pedido")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio en el detalle del pedido")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario;

    // Constructores, Getters y Setters

    public PedidoDetalleDTO() {
    }

    public PedidoDetalleDTO(Long itemId, Integer cantidad, BigDecimal precioUnitario) {
        this.itemId = itemId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}