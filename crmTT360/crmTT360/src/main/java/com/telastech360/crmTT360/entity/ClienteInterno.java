package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "cliente_interno", uniqueConstraints = @UniqueConstraint(columnNames = "codigo_interno"))
public class ClienteInterno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    @NotBlank(message = "El código interno es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoInterno;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoCliente tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String ubicacion;

    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    @Column(columnDefinition = "DECIMAL(12,2)")
    private BigDecimal presupuestoAnual;

    @Column(nullable = false, updatable = false)
    private Timestamp fechaRegistro = new Timestamp(System.currentTimeMillis());

    // Enum interno
    public enum TipoCliente {
        INTERNO, EXTERNO
    }

    // Getters y Setters
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public BigDecimal getPresupuestoAnual() {
        return presupuestoAnual;
    }

    public void setPresupuestoAnual(BigDecimal presupuestoAnual) {
        this.presupuestoAnual = presupuestoAnual;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}