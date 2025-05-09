// src/main/java/com/telastech360/crmTT360/security/auth/dto/LoginRequest.java
package com.telastech360.crmTT360.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para encapsular las credenciales de inicio de sesión (email y contraseña)
 * enviadas en el cuerpo de la solicitud POST a /api/auth/login.
 */
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio para iniciar sesión")
    @Email(message = "El formato del email no es válido")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria para iniciar sesión")
    // La validación de longitud aquí debe coincidir con la validación usada en el registro/actualización
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto.
     */
    public LoginRequest() {
    }

    /**
     * Constructor con parámetros.
     * @param email Email del usuario.
     * @param password Contraseña del usuario.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Obtiene el email proporcionado para el inicio de sesión.
     * @return El email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email para el inicio de sesión.
     * @param email El nuevo email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña proporcionada para el inicio de sesión.
     * @return La contraseña.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña para el inicio de sesión.
     * @param password La nueva contraseña.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}