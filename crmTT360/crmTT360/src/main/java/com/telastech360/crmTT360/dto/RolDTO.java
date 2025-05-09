// src/main/java/com/telastech360/crmTT360/dto/RolDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para representar la información de un Rol de usuario.
 * Se utiliza para crear o actualizar roles en el sistema.
 */
public class RolDTO {

    // El ID generalmente no se incluye en el DTO de creación/actualización
    // ya que se maneja en la URL o es generado por la DB.
    // private Long rolId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre; // Nombre único del rol (ej. "ADMIN", "GERENTE")

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion; // Descripción opcional del rol

    // Getters y Setters

    /**
     * Obtiene el nombre del rol.
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del rol.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del rol.
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del rol.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}