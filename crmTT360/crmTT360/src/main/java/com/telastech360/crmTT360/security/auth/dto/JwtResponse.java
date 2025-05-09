package com.telastech360.crmTT360.security.auth.dto;

import java.util.List;

public class JwtResponse {
    private String token; // El token JWT generado
    private String type = "Bearer";
    private Long id;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, Long id, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }

    // --- ¡ASEGÚRATE DE QUE ESTE GETTER EXISTE Y ES PÚBLICO! ---
    public String getAccessToken() {
        return token;
    }
    // -----------------------------------------------------------

    public void setAccessToken(String accessToken) { // Setter opcional
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) { // Setter opcional
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { // Setter opcional
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { // Setter opcional
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    // Setter para roles (generalmente no se usa)
    // public void setRoles(List<String> roles) {
    //    this.roles = roles;
    // }
}