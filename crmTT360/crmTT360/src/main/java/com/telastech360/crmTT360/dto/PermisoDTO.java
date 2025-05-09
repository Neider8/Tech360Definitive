// src/main/java/com/telastech360/crmTT360/dto/PermisoDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para representar la información de un Permiso del sistema.
 * Define una acción o capacidad específica que puede ser asignada a Roles.
 */
public class PermisoDTO {

    private Long permisoId; // ID del permiso, útil para respuestas o actualizaciones

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre; // Nombre único del permiso (ej. "LEER_USUARIOS", "CREAR_PEDIDO")

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion; // Descripción opcional del permiso

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto.
     */
    public PermisoDTO() {
    }

    /**
     * Constructor con parámetros.
     * @param permisoId ID del permiso.
     * @param nombre Nombre del permiso.
     * @param descripcion Descripción del permiso.
     */
    public PermisoDTO(Long permisoId, String nombre, String descripcion) {
        this.permisoId = permisoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el ID del permiso.
     * @return El ID del permiso.
     */
    public Long getPermisoId() {
        return permisoId;
    }

    /**
     * Establece el ID del permiso.
     * @param permisoId El nuevo ID del permiso.
     */
    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    /**
     * Obtiene el nombre del permiso.
     * @return El nombre del permiso.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del permiso.
     * @param nombre El nuevo nombre del permiso.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del permiso.
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del permiso.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}