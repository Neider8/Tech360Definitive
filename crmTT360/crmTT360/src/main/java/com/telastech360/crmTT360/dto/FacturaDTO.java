// src/main/java/com/telastech360/crmTT360/dto/FacturaDTO.java
package com.telastech360.crmTT360.dto;

import com.telastech360.crmTT360.entity.Factura.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO (Data Transfer Object) para representar la información de una Factura.
 * Se utiliza para crear o actualizar facturas asociadas a pedidos.
 */
public class FacturaDTO {

    // --- CAMBIO: Descomentado/Añadido facturaId ---
    private Long facturaId; // ID de la factura (útil en respuestas)

    // Añadir pedidoId si se necesita para crear la factura a partir del DTO
    @NotNull(message = "El ID del pedido es obligatorio") // Asegúrate de incluir esto si creas desde DTO
    private Long pedidoId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento; // VENTA o COMPRA

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser positivo")
    private BigDecimal total; // Monto total de la factura

    @NotNull(message = "La fecha de la factura es obligatoria")
    private Timestamp fechaCreacion; // Fecha y hora de creación

    // Añadir estadoPago si se maneja desde el DTO
    private boolean estadoPago; // Asumiendo que lo quieres manejar

    /**
     * Constructor por defecto.
     */
    public FacturaDTO() {
    }

    /**
     * Constructor con parámetros principales.
     * @param tipoMovimiento El tipo de movimiento (VENTA/COMPRA).
     * @param total El monto total.
     * @param fechaCreacion La fecha de creación.
     */
    public FacturaDTO(TipoMovimiento tipoMovimiento, BigDecimal total, Timestamp fechaCreacion) {
        this.tipoMovimiento = tipoMovimiento;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    // --- CAMBIO: Añadido Getters y Setters para facturaId y pedidoId ---

    /**
     * Obtiene el ID de la factura.
     * @return El ID de la factura.
     */
    public Long getFacturaId() {
        return facturaId;
    }

    /**
     * Establece el ID de la factura.
     * @param facturaId El nuevo ID de la factura.
     */
    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    /**
     * Obtiene el ID del pedido asociado.
     * @return El ID del pedido.
     */
    public Long getPedidoId() {
        return pedidoId;
    }

    /**
     * Establece el ID del pedido asociado.
     * @param pedidoId El ID del pedido.
     */
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    // --- Getters y Setters existentes ---

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Obtiene el estado de pago.
     * @return true si está pagada, false si está pendiente.
     */
    public boolean isEstadoPago() {
        return estadoPago;
    }

    /**
     * Establece el estado de pago.
     * @param estadoPago true si está pagada, false si está pendiente.
     */
    public void setEstadoPago(boolean estadoPago) {
        this.estadoPago = estadoPago;
    }
}