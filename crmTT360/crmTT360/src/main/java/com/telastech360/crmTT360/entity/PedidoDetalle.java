package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pedido_detalle")
public class PedidoDetalle {

    @EmbeddedId
    private PedidoDetalleId id = new PedidoDetalleId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Item producto; // Cambiado de Producto a Item

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @DecimalMin(value = "0.0001", message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, columnDefinition = "DECIMAL(12,4)")
    private BigDecimal precioUnitario;

    // Constructores
    public PedidoDetalle() {}

    public PedidoDetalle(Pedido pedido, Item producto, Integer cantidad, BigDecimal precioUnitario) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.id = new PedidoDetalleId(pedido.getPedidoId(), producto.getItemId());
    }

    // Getters y Setters
    public PedidoDetalleId getId() {
        return id;
    }

    public void setId(PedidoDetalleId id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
        if (pedido != null && id != null) {
            id.setPedidoId(pedido.getPedidoId());
        }
    }

    public Item getProducto() {
        return producto;
    }

    public void setProducto(Item producto) {
        this.producto = producto;
        if (producto != null && id != null) {
            id.setItemId(producto.getItemId());
        }
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

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoDetalle that = (PedidoDetalle) o;
        return Objects.equals(pedido, that.pedido) &&
                Objects.equals(producto, that.producto) &&
                Objects.equals(cantidad, that.cantidad) &&
                Objects.equals(precioUnitario, that.precioUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, producto, cantidad, precioUnitario);
    }

    @Override
    public String toString() {
        return "PedidoDetalle{" +
                "pedidoId=" + (pedido != null ? pedido.getPedidoId() : null) +
                ", productoId=" + (producto != null ? producto.getItemId() : null) +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                '}';
    }

    // ===================== ID EMBEDDABLE ====================== //
    @Embeddable
    public static class PedidoDetalleId implements java.io.Serializable {
        @Column(name = "pedido_id")
        private Long pedidoId;

        @Column(name = "item_id")
        private Long itemId;

        public PedidoDetalleId() {}

        public PedidoDetalleId(Long pedidoId, Long itemId) {
            this.pedidoId = pedidoId;
            this.itemId = itemId;
        }

        public Long getPedidoId() {
            return pedidoId;
        }

        public void setPedidoId(Long pedidoId) {
            this.pedidoId = pedidoId;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PedidoDetalleId that = (PedidoDetalleId) o;
            return Objects.equals(pedidoId, that.pedidoId) &&
                    Objects.equals(itemId, that.itemId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pedidoId, itemId);
        }
    }
}