package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "estado", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tipo_estado", "valor"}, name = "uk_estado_tipo_valor")
})
public class Estado {

    public enum TipoEstado {
        ACTIVO, INACTIVO, PENDIENTE, CANCELADO, PEDIDO, ITEM // <- Valores actualizados
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_id")
    private Long estadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_estado", nullable = false, length = 20)
    @NotNull(message = "El tipo de estado es obligatorio")
    private TipoEstado tipoEstado;

    @NotBlank(message = "El valor no puede estar vacío")
    @Column(name = "valor", nullable = false, length = 50)
    private String valor;

    public Estado() {
    }

    public Estado(TipoEstado tipoEstado, String valor) {
        this.tipoEstado = tipoEstado;
        this.valor = valor;
    }

    // Getters y Setters (sin cambios)
    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public TipoEstado getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(TipoEstado tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Estado{" +
                "estadoId=" + estadoId +
                ", tipoEstado=" + tipoEstado +
                ", valor='" + valor + '\'' +
                '}';
    }
}