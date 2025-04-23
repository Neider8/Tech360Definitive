package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO para la entidad Permiso
public class PermisoDTO {

    private Long permisoId; // Incluir ID para respuestas y posibles actualizaciones

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion;

    // Constructores, Getters y Setters

    public PermisoDTO() {
    }

    public PermisoDTO(Long permisoId, String nombre, String descripcion) {
        this.permisoId = permisoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}