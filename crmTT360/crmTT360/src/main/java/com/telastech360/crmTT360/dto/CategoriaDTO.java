// src/main/java/com/telastech360/crmTT360/dto/CategoriaDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para representar la información de una Categoría de ítems.
 * Utilizado para crear o actualizar categorías.
 */
public class CategoriaDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion;

    // Getters y Setters

    /**
     * Obtiene el nombre de la categoría.
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la categoría.
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}