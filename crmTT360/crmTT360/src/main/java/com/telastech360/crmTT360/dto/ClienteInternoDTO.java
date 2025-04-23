package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ClienteInternoDTO {

    @NotBlank(message = "El código interno es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoInterno;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private String tipo; // Puedes usar un enum si lo prefieres

    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String ubicacion;

    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    private BigDecimal presupuestoAnual;

    @NotNull(message = "El ID del responsable es obligatorio")
    private Long responsableId;

    // Getters y Setters
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }
}