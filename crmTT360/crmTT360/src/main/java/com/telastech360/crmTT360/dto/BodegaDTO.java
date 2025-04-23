package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BodegaDTO {

    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de bodega es obligatorio")
    private String tipoBodega; // Puedes usar un enum si lo prefieres

    @NotNull(message = "La capacidad máxima es obligatoria")
    private Integer capacidadMaxima;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede exceder los 200 caracteres")
    private String ubicacion;

    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoBodega() {
        return tipoBodega;
    }

    public void setTipoBodega(String tipoBodega) {
        this.tipoBodega = tipoBodega;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }
}