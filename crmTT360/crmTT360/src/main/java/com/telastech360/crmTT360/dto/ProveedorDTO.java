// src/main/java/com/telastech360/crmTT360/dto/ProveedorDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para representar la información de un Proveedor.
 * Utilizado para transferir datos de proveedores entre capas y validar la entrada.
 */
public class ProveedorDTO {

    private Long proveedorId; // ID del proveedor, útil para respuestas o actualizaciones

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    @NotBlank(message = "La dirección del proveedor es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono del proveedor es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    // Considerar añadir @Pattern para validar formato de teléfono si es necesario
    private String telefono;

    @NotBlank(message = "El email del proveedor es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto.
     */
    public ProveedorDTO() {
    }

    /**
     * Constructor con parámetros.
     * @param proveedorId ID del proveedor.
     * @param nombre Nombre del proveedor.
     * @param direccion Dirección del proveedor.
     * @param telefono Teléfono de contacto.
     * @param email Email de contacto.
     */
    public ProveedorDTO(Long proveedorId, String nombre, String direccion, String telefono, String email) {
        this.proveedorId = proveedorId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    /**
     * Obtiene el ID del proveedor.
     * @return El ID del proveedor.
     */
    public Long getProveedorId() {
        return proveedorId;
    }

    /**
     * Establece el ID del proveedor.
     * @param proveedorId El nuevo ID.
     */
    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    /**
     * Obtiene el nombre del proveedor.
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del proveedor.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección del proveedor.
     * @return La dirección.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del proveedor.
     * @param direccion La nueva dirección.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene el teléfono de contacto del proveedor.
     * @return El teléfono.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono de contacto del proveedor.
     * @param telefono El nuevo teléfono.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el email de contacto del proveedor.
     * @return El email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email de contacto del proveedor.
     * @param email El nuevo email.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}