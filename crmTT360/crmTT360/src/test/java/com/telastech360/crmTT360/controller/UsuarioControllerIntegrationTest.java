// src/test/java/com/telastech360/crmTT360/controller/UsuarioControllerIntegrationTest.java
package com.telastech360.crmTT360.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

// Importar el post-processor para user()
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Asegura rollback después de cada test
class UsuarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RolRepository rolRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long idRolOperario;
    private Long idRolAdmin;
    private Rol rolOperario;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        // Asegurar que los roles necesarios existan
        rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> rolRepository.saveAndFlush(crearRol("ADMIN", "Admin Test")));
        rolOperario = rolRepository.findByNombre("OPERARIO")
                .orElseGet(() -> rolRepository.saveAndFlush(crearRol("OPERARIO", "Operario Test")));

        idRolAdmin = rolAdmin.getRolId();
        idRolOperario = rolOperario.getRolId();
        assertNotNull(idRolOperario, "El ID del rol Operario no debe ser nulo después del setup");
        assertNotNull(idRolAdmin, "El ID del rol Admin no debe ser nulo después del setup");

        // Limpiar usuarios antes de cada test para evitar conflictos de email único
        usuarioRepository.deleteAll();
        usuarioRepository.flush(); // Asegurar que la eliminación se ejecute antes del siguiente test
    }

    // Método helper para crear roles si no existen
    private Rol crearRol(String nombre, String descripcion) {
        Rol r = new Rol();
        r.setNombre(nombre.toUpperCase()); // Guardar en mayúsculas
        r.setDescripcion(descripcion);
        return r;
    }

    // Método helper para crear usuarios directamente en la BD para tests específicos
    private Usuario crearUsuario(String nombre, String email, String password, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRol(rol);
        u.setEstado("ACTIVO");
        return usuarioRepository.saveAndFlush(u);
    }

    // --- Tests Corregidos ---

    @Test
    @DisplayName("POST /api/usuarios - Creación Exitosa")
    @WithMockUser(authorities = {"CREAR_USUARIO"}) // Simula usuario con permiso necesario
    void crearUsuario_Exito() throws Exception {
        // --- DTO COMPLETAMENTE VÁLIDO ---
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setNombre("Nuevo Operario Valido");
        nuevoUsuario.setEmail("valido.operario@example.com");
        nuevoUsuario.setPassword("passwordValida123");
        nuevoUsuario.setRolId(idRolOperario); // ID de un rol existente
        // ---------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andDo(print())
                .andExpect(status().isCreated()) // Esperar 201 CREATED
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(nuevoUsuario.getEmail()))
                .andExpect(jsonPath("$.nombre").value(nuevoUsuario.getNombre()))
                .andExpect(jsonPath("$.rolId").value(idRolOperario))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.usuarioId").exists());
    }

    @Test
    @DisplayName("POST /api/usuarios - Email Duplicado")
    @WithMockUser(authorities = {"CREAR_USUARIO"})
    void crearUsuario_EmailDuplicado() throws Exception {
        // Crear usuario base directamente para asegurar existencia
        crearUsuario("Usuario Existente", "existente@example.com", "passwordBase123", rolOperario);

        // --- DTO COMPLETO CON EMAIL DUPLICADO ---
        UsuarioRequestDTO usuarioDuplicado = new UsuarioRequestDTO();
        usuarioDuplicado.setNombre("Usuario Duplicado Intento");
        usuarioDuplicado.setEmail("existente@example.com"); // Email que ya existe
        usuarioDuplicado.setPassword("otraPasswordValida123");
        usuarioDuplicado.setRolId(idRolOperario);
        // ---------------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDuplicado)))
                .andDo(print())
                .andExpect(status().isConflict()); // Esperar 409 Conflict
    }

    @Test
    @DisplayName("POST /api/usuarios - Datos Inválidos (Email incorrecto)")
    @WithMockUser(authorities = {"CREAR_USUARIO"})
    void crearUsuario_DatosInvalidosEmail() throws Exception {
        // --- DTO CON EMAIL INVÁLIDO, PERO OTROS CAMPOS VÁLIDOS ---
        UsuarioRequestDTO usuarioInvalido = new UsuarioRequestDTO();
        usuarioInvalido.setNombre("Usuario Inválido Email"); // Válido
        usuarioInvalido.setEmail("email-no-valido");         // Inválido
        usuarioInvalido.setPassword("passwordValida123");      // Válido
        usuarioInvalido.setRolId(idRolOperario);             // Válido
        // --------------------------------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Esperar 400 Bad Request
                .andExpect(jsonPath("$.errors[?(@ =~ /.*email.*/)]").exists()); // Verificar mensaje error email
    }

    @Test
    @DisplayName("POST /api/usuarios - Datos Inválidos (Contraseña corta)")
    @WithMockUser(authorities = {"CREAR_USUARIO"})
    void crearUsuario_DatosInvalidosPassword() throws Exception {
        // --- DTO CON PASSWORD INVÁLIDA, PERO OTROS CAMPOS VÁLIDOS ---
        UsuarioRequestDTO usuarioInvalido = new UsuarioRequestDTO();
        usuarioInvalido.setNombre("Usuario Inválido Pass");   // Válido
        usuarioInvalido.setEmail("invalido.pass@example.com"); // Válido
        usuarioInvalido.setPassword("corta");                 // Inválido (corta)
        usuarioInvalido.setRolId(idRolOperario);             // Válido
        // -----------------------------------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Esperar 400 Bad Request
                .andExpect(jsonPath("$.errors[?(@ =~ /.*contraseña.*/)]").exists()); // Verificar mensaje error password
    }

    @Test
    @DisplayName("POST /api/usuarios - Datos Inválidos (Rol ID nulo)")
    @WithMockUser(authorities = {"CREAR_USUARIO"})
    void crearUsuario_DatosInvalidosRolIdNulo() throws Exception {
        // --- DTO CON ROL ID NULO, PERO OTROS CAMPOS VÁLIDOS ---
        UsuarioRequestDTO usuarioInvalido = new UsuarioRequestDTO();
        usuarioInvalido.setNombre("Usuario Sin Rol");          // Válido
        usuarioInvalido.setEmail("sin.rol@example.com");       // Válido
        usuarioInvalido.setPassword("passwordValida123");      // Válido
        usuarioInvalido.setRolId(null);                       // Inválido (@NotNull)
        // -------------------------------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Esperar 400 Bad Request
                .andExpect(jsonPath("$.errors[?(@ =~ /.*rolId.*/)]").exists()); // Verificar mensaje error rolId
    }

    @Test
    @DisplayName("POST /api/usuarios - Datos Inválidos (Nombre vacío)")
    @WithMockUser(authorities = {"CREAR_USUARIO"})
    void crearUsuario_DatosInvalidosNombreVacio() throws Exception {
        // --- DTO CON NOMBRE VACÍO, PERO OTROS CAMPOS VÁLIDOS ---
        UsuarioRequestDTO usuarioInvalido = new UsuarioRequestDTO();
        usuarioInvalido.setNombre("");                         // Inválido (@NotBlank)
        usuarioInvalido.setEmail("nombre.vacio@example.com");  // Válido
        usuarioInvalido.setPassword("passwordValida123");      // Válido
        usuarioInvalido.setRolId(idRolOperario);             // Válido
        // ------------------------------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Esperar 400 Bad Request
                .andExpect(jsonPath("$.errors[?(@ =~ /.*nombre.*/)]").exists()); // Verificar mensaje error nombre
    }

    @Test
    @DisplayName("POST /api/usuarios - No Autorizado (Sin permiso)")
    @WithMockUser(username = "user.sinpermiso@test.com", authorities = {"LEER_ITEMS"}) // Sin permiso CREAR_USUARIO
    void crearUsuario_NoAutorizado_PermisoIncorrecto() throws Exception {
        // --- DTO COMPLETAMENTE VÁLIDO ---
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setNombre("Intento No Autorizado OK");
        nuevoUsuario.setEmail("no.auth.ok@example.com");
        nuevoUsuario.setPassword("passwordValida123");
        nuevoUsuario.setRolId(idRolOperario);
        // ---------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        // No es necesario .with(user(...)) si ya usas @WithMockUser
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Ahora espera 403 Forbidden (gracias al GlobalExceptionHandler corregido)
    }

    @Test
    @DisplayName("POST /api/usuarios - No Autorizado (Sin autenticación)")
    void crearUsuario_NoAutorizado_SinAutenticar() throws Exception {
        // --- DTO COMPLETAMENTE VÁLIDO ---
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setNombre("Intento Anonimo OK");
        nuevoUsuario.setEmail("anon.ok@example.com");
        nuevoUsuario.setPassword("passwordValida123");
        nuevoUsuario.setRolId(idRolOperario);
        // ---------------------------------

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios")
                        // SIN .with(...) o @WithMockUser
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // Esperar 401 Unauthorized
    }

    // --- Añade aquí más tests para PUT, DELETE, GET si es necesario, ---
    // --- asegurando que los DTOs en PUT también sean válidos ---
    // --- y simulando permisos/autenticación correctamente ---

}