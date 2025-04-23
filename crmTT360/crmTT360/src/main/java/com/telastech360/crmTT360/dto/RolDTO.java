package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RolDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion;

    // Getters y Setters
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