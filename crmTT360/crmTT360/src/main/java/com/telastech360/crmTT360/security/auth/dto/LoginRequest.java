package com.telastech360.crmTT360.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El email es obligatorio para iniciar sesión")
    @Email(message = "El formato del email no es válido")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria para iniciar sesión")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;

    // Constructores, Getters y Setters

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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
}