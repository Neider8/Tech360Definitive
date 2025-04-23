package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO para la entidad Proveedor
public class ProveedorDTO {

    private Long proveedorId; // Incluir ID para respuestas y posibles actualizaciones

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    @NotBlank(message = "La dirección del proveedor es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono del proveedor es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;

    @NotBlank(message = "El email del proveedor es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    // Constructores, Getters y Setters

    public ProveedorDTO() {
    }

    public ProveedorDTO(Long proveedorId, String nombre, String direccion, String telefono, String email) {
        this.proveedorId = proveedorId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}