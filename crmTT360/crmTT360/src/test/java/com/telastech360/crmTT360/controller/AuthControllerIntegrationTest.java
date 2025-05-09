// src/test/java/com/telastech360/crmTT360/controller/AuthControllerIntegrationTest.java
package com.telastech360.crmTT360.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telastech360.crmTT360.security.auth.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

// <<<--- IMPORTAR HAMCREST MATCHERS --- >>>
import static org.hamcrest.Matchers.hasItem;
// <<<------------------------------------ >>>

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/login - Autenticación Exitosa (Admin)")
    void authenticateUser_ExitoAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@telastech360.com", "PasswordAdmin123.");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer")) // Verificar que el getter es correcto
                .andExpect(jsonPath("$.email").value(loginRequest.getEmail()))
                // <<<--- CORRECCIÓN DE ASERCIÓN --- >>>
                .andExpect(jsonPath("$.roles").isArray()) // Asegura que es un array
                .andExpect(jsonPath("$.roles").value(hasItem("ROLE_ADMIN"))); // Verifica que contenga el rol
        // <<<-------------------------------- >>>
    }

    @Test
    @DisplayName("POST /api/auth/login - Autenticación Fallida (Contraseña Incorrecta)")
    void authenticateUser_PasswordIncorrecta() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@telastech360.com", "incorrectaPasswordLarga");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Autenticación Fallida (Usuario No Existe)")
    void authenticateUser_UsuarioNoExiste() throws Exception {
        LoginRequest loginRequest = new LoginRequest("noexiste@example.com", "validPassword123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Datos Inválidos (Email)")
    void authenticateUser_EmailInvalido() throws Exception {
        LoginRequest loginRequest = new LoginRequest("email-invalido", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Datos Inválidos (Password corta)")
    void authenticateUser_PasswordCorta() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@telastech360.com", "corta");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}