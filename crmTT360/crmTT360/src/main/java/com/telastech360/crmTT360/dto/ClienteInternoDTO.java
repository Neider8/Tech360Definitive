// src/main/java/com/telastech360/crmTT360/dto/ClienteInternoDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para representar la información de un Cliente Interno.
 * Se utiliza para crear o actualizar clientes internos en el sistema.
 */
public class ClienteInternoDTO {

    @NotBlank(message = "El código interno es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoInterno;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private String tipo; // Representa los valores del enum ClienteInterno.TipoCliente como String ("INTERNO", "EXTERNO")

    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String ubicacion;

    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    private BigDecimal presupuestoAnual;

    @NotNull(message = "El ID del responsable es obligatorio")
    private Long responsableId; // ID del Usuario responsable de este cliente

    // Getters y Setters

    /**
     * Obtiene el código interno único del cliente.
     * @return El código interno.
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Establece el código interno único del cliente.
     * @param codigoInterno El nuevo código interno.
     */
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Obtiene el nombre del cliente.
     * @return El nombre del cliente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de cliente como String.
     * @return El tipo ("INTERNO" o "EXTERNO").
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de cliente.
     * @param tipo El nuevo tipo (debe ser "INTERNO" o "EXTERNO").
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la ubicación del cliente.
     * @return La ubicación.
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Establece la ubicación del cliente.
     * @param ubicacion La nueva ubicación.
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene el presupuesto anual asignado al cliente.
     * @return El presupuesto anual.
     */
    public BigDecimal getPresupuestoAnual() {
        return presupuestoAnual;
    }

    /**
     * Establece el presupuesto anual asignado al cliente.
     * @param presupuestoAnual El nuevo presupuesto anual.
     */
    public void setPresupuestoAnual(BigDecimal presupuestoAnual) {
        this.presupuestoAnual = presupuestoAnual;
    }

    /**
     * Obtiene el ID del usuario responsable del cliente.
     * @return El ID del usuario responsable.
     */
    public Long getResponsableId() {
        return responsableId;
    }

    /**
     * Establece el ID del usuario responsable del cliente.
     * @param responsableId El nuevo ID del usuario responsable.
     */
    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }
}