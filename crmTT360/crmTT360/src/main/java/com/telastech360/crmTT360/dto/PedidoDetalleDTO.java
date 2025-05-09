// src/main/java/com/telastech360/crmTT360/dto/PedidoDetalleDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para representar un ítem individual dentro de un Pedido.
 * Contiene la información del ítem, la cantidad pedida y el precio unitario en el momento del pedido.
 */
public class PedidoDetalleDTO {

    @NotNull(message = "El ID del ítem es obligatorio en el detalle del pedido")
    private Long itemId; // ID del Item (Producto o MateriaPrima)

    @NotNull(message = "La cantidad es obligatoria en el detalle del pedido")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad; // Cantidad pedida del ítem

    @NotNull(message = "El precio unitario es obligatorio en el detalle del pedido")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario; // Precio del ítem al momento de crear/actualizar el detalle

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto.
     */
    public PedidoDetalleDTO() {
    }

    /**
     * Constructor con parámetros.
     * @param itemId ID del ítem.
     * @param cantidad Cantidad pedida.
     * @param precioUnitario Precio unitario del ítem.
     */
    public PedidoDetalleDTO(Long itemId, Integer cantidad, BigDecimal precioUnitario) {
        this.itemId = itemId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el ID del ítem asociado a este detalle.
     * @return El ID del ítem.
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Establece el ID del ítem asociado a este detalle.
     * @param itemId El nuevo ID del ítem.
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Obtiene la cantidad pedida de este ítem.
     * @return La cantidad.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad pedida de este ítem.
     * @param cantidad La nueva cantidad.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del ítem en este detalle de pedido.
     * @return El precio unitario.
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del ítem en este detalle de pedido.
     * @param precioUnitario El nuevo precio unitario.
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}