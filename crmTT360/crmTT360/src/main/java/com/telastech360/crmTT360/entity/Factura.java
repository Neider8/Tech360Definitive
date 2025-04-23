package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "factura")
public class Factura {

    public enum TipoMovimiento {
        VENTA, COMPRA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    @Column(name = "total", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal total;

    @Column(name = "estado_pago", nullable = false)
    private boolean estadoPago = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Timestamp fechaCreacion = new Timestamp(System.currentTimeMillis());

    // Constructores
    public Factura() {}

    public Factura(Pedido pedido, TipoMovimiento tipoMovimiento, BigDecimal total) {
        this.pedido = pedido;
        this.tipoMovimiento = tipoMovimiento;
        this.total = total;
    }

    public Factura(Pedido pedido, TipoMovimiento tipoMovimiento, BigDecimal total, boolean estadoPago) {
        this.pedido = pedido;
        this.tipoMovimiento = tipoMovimiento;
        this.total = total;
        this.estadoPago = estadoPago;
    }

    // Getters y Setters
    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public boolean isEstadoPago() { return estadoPago; }
    public void setEstadoPago(boolean estadoPago) { this.estadoPago = estadoPago; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Métodos de negocio
    public boolean isVenta() {
        return TipoMovimiento.VENTA.equals(this.tipoMovimiento);
    }

    public boolean isCompra() {
        return TipoMovimiento.COMPRA.equals(this.tipoMovimiento);
    }

    @Override
    public String toString() {
        return "Factura{" +
                "facturaId=" + facturaId +
                ", pedidoId=" + (pedido != null ? pedido.getPedidoId() : null) +
                ", tipoMovimiento=" + tipoMovimiento +
                ", total=" + total +
                ", estadoPago=" + estadoPago +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}