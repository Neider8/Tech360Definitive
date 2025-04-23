package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private Timestamp fechaPedido = new Timestamp(System.currentTimeMillis());

    // ===>>> CAMPO AÑADIDO: fechaFin <<<===
    @Column(name = "fecha_fin") // Asegúrate que el nombre de la columna en tu BD sea 'fecha_fin'
    private Timestamp fechaFin;
    // ====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteInterno cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    @NotNull(message = "El estado del pedido es obligatorio")
    private Estado estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Factura> facturas = new ArrayList<>();

    public List<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    public Pedido() {}

    public Pedido(Estado estado) {
        this.estado = estado;
    }

    public Pedido(ClienteInterno cliente, Estado estado) {
        this.cliente = cliente;
        this.estado = estado;
    }

    public void agregarDetalle(PedidoDetalle detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removerDetalle(PedidoDetalle detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }

    // Getters y setters existentes...
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public Timestamp getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(Timestamp fechaPedido) { this.fechaPedido = fechaPedido; }

    // ===>>> GETTER Y SETTER AÑADIDOS PARA fechaFin <<<===
    public Timestamp getFechaFin() { return fechaFin; }
    public void setFechaFin(Timestamp fechaFin) { this.fechaFin = fechaFin; }
    // ====================================================

    public ClienteInterno getCliente() { return cliente; }
    public void setCliente(ClienteInterno cliente) { this.cliente = cliente; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public List<PedidoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<PedidoDetalle> detalles) { this.detalles = detalles; }


    @Override
    public String toString() {
        return "Pedido{" +
                "pedidoId=" + pedidoId +
                ", fechaPedido=" + fechaPedido +
                ", fechaFin=" + fechaFin + // Incluir fechaFin si deseas en el String
                ", estado=" + (estado != null ? estado.getValor() : "null") +
                '}';
    }
}