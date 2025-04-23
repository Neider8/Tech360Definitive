package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO para solicitudes de creación o actualización de Usuario
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres")
    private String email;

    // La contraseña solo es necesaria para la creación de usuario,
    // por lo que se marca como no obligatoria aquí para permitir actualizaciones sin cambiarla.
    // Para el endpoint de creación específico, se puede manejar la validación de contraseña.
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    // El estado del usuario (activo/inactivo) podría no ser editable directamente
    // en esta solicitud general, sino a través de un endpoint específico.
    // Incluirlo o no depende de la lógica de negocio deseada.

    // Constructores, Getters y Setters

    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String nombre, String email, String password, Long rolId) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}