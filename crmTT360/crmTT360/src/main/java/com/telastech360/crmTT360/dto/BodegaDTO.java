// src/main/java/com/telastech360/crmTT360/dto/BodegaDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para representar la información de una Bodega.
 * Utilizado para transferir datos entre la capa de controlador y la capa de servicio,
 * validando la entrada y exponiendo solo los campos necesarios.
 */
public class BodegaDTO {

    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de bodega es obligatorio")
    private String tipoBodega; // Representa los valores del enum Bodega.TipoBodega como String

    @NotNull(message = "La capacidad máxima es obligatoria")
    private Integer capacidadMaxima;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede exceder los 200 caracteres")
    private String ubicacion;

    @NotNull(message = "El estado es obligatorio")
    private Long estadoId; // ID del Estado asociado

    // Getters y Setters

    /**
     * Obtiene el nombre de la bodega.
     * @return El nombre de la bodega.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la bodega.
     * @param nombre El nuevo nombre de la bodega.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de bodega como String (ej. "MATERIA_PRIMA", "PRODUCTO_TERMINADO").
     * @return El tipo de bodega.
     */
    public String getTipoBodega() {
        return tipoBodega;
    }

    /**
     * Establece el tipo de bodega.
     * @param tipoBodega El nuevo tipo de bodega (debe coincidir con los valores del enum {@link com.telastech360.crmTT360.entity.Bodega.TipoBodega}).
     */
    public void setTipoBodega(String tipoBodega) {
        this.tipoBodega = tipoBodega;
    }

    /**
     * Obtiene la capacidad máxima de la bodega.
     * @return La capacidad máxima.
     */
    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Establece la capacidad máxima de la bodega.
     * @param capacidadMaxima La nueva capacidad máxima.
     */
    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * Obtiene la ubicación física de la bodega.
     * @return La ubicación de la bodega.
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Establece la ubicación física de la bodega.
     * @param ubicacion La nueva ubicación.
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene el ID del estado asociado a la bodega.
     * @return El ID del estado.
     */
    public Long getEstadoId() {
        return estadoId;
    }

    /**
     * Establece el ID del estado asociado a la bodega.
     * @param estadoId El nuevo ID del estado.
     */
    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }
}