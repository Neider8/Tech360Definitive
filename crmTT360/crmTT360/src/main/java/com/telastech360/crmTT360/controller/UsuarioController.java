package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Importa la anotación PreAuthorize (ya estaba)
import org.springframework.security.access.prepost.PreAuthorize;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

// Importaciones para logging manual
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de Usuarios del sistema") // Anotación Tag
// Elimina @Slf4j si estaba aquí
public class UsuarioController {

    // Inicialización manual del logger
    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ========== ENDPOINTS CRUD ========== //

    @GetMapping
    // Ejemplo: Permitir a ADMIN y GERENTE listar todos los usuarios
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Lista todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Usuario>> listarTodosLosUsuarios() {
        log.info("Recibida solicitud GET /api/usuarios para listar todos los usuarios.");
        try {
            List<Usuario> usuarios = usuarioService.listarTodosLosUsuarios();
            log.debug("Se recuperaron {} usuarios del servicio.", usuarios.size());
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al listar todos los usuarios: {}", e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje
        }
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE obtener un usuario por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtiene un usuario por ID", description = "Recupera los detalles de un usuario específico usando su ID.")
    @Parameter(name = "id", description = "ID del usuario a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Recibida solicitud GET /api/usuarios/{} para obtener usuario por ID.", id);
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            log.debug("Usuario con ID {} encontrado.", id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al obtener usuario con ID {}: {}", id, e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje (incluido ResourceNotFoundException)
        }
    }

    @PostMapping
    // Ejemplo: Solo ADMIN puede crear usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuevo usuario", description = "Registra un nuevo usuario en el sistema.")
    @RequestBody(description = "Datos del usuario a crear", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto (ej: email duplicado si aplica)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        log.info("Recibida solicitud POST /api/usuarios para crear nuevo usuario con email: {}", usuario.getEmail());
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            log.info("Usuario creado exitosamente con ID: {}", nuevoUsuario.getUsuarioId()); // <- Corrección
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear usuario con email {}: {}", usuario.getEmail(), e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje (incluido DuplicateResourceException)
        }
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE actualizar usuarios
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualiza un usuario existente", description = "Modifica los detalles de un usuario usando su ID.")
    @Parameter(name = "id", description = "ID del usuario a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado
    ) {
        log.info("Recibida solicitud PUT /api/usuarios/{} para actualizar usuario.", id);
        try {
            Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
            log.info("Usuario con ID {} actualizado exitosamente.", id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al actualizar usuario con ID {}: {}", id, e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje
        }
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un usuario", description = "Elimina un usuario del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del usuario a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("Recibida solicitud DELETE /api/usuarios/{} para eliminar usuario.", id);
        try {
            usuarioService.eliminarUsuario(id);
            log.info("Usuario con ID {} eliminado exitosamente.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error al eliminar usuario con ID {}: {}", id, e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje (incluido IllegalOperationException)
        }
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //

    @GetMapping("/buscar")
    // Ejemplo: Permitir a ADMIN y GERENTE buscar usuarios por nombre o email
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Busca usuarios por nombre o email", description = "Obtiene una lista de usuarios cuyo nombre o email coincide con un término de búsqueda.")
    @Parameter(name = "termino", description = "Texto para buscar en nombre o email del usuario", required = true, example = "admin")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Usuario>> buscarPorNombreOEmail(@RequestParam String termino) {
        log.info("Recibida solicitud GET /api/usuarios/buscar con término: {}", termino);
        try {
            List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombreOEmail(termino);
            log.debug("Se encontraron {} usuarios para el término '{}'.", usuarios.size(), termino);
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al buscar usuarios por término '{}': {}", termino, e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje
        }
    }

    @GetMapping("/rol/{rolNombre}")
    // Ejemplo: Permitir a ADMIN y GERENTE listar usuarios por rol
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Lista usuarios por rol", description = "Obtiene una lista de usuarios que tienen un rol específico.")
    @Parameter(name = "rolNombre", description = "Nombre del rol", required = true, example = "ADMIN")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado (si aplica en tu servicio)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Usuario>> listarUsuariosPorRol(@PathVariable String rolNombre) {
        log.info("Recibida solicitud GET /api/usuarios/rol/{} para listar usuarios por rol.", rolNombre);
        try {
            List<Usuario> usuarios = usuarioService.listarUsuariosPorRol(rolNombre);
            log.debug("Se encontraron {} usuarios con el rol '{}'.", usuarios.size(), rolNombre);
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al listar usuarios por rol '{}': {}", rolNombre, e.getMessage(), e);
            throw e; // Permite que el GlobalExceptionHandler lo maneje
        }
    }
}