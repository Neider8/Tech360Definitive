package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.dto.UsuarioResponseDTO;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.mapper.UsuarioMapper;
import com.telastech360.crmTT360.service.UsuarioService;
import com.telastech360.crmTT360.exception.*; // Importar excepciones relevantes
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException; // <-- IMPORT YA PRESENTE
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// --- Importar RequestBody de Swagger ---
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;


/**
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con los Usuarios.
 * Proporciona endpoints para listar, obtener, crear, actualizar, eliminar y buscar usuarios.
 * Las operaciones están protegidas por roles/permisos usando {@link PreAuthorize}.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de Usuarios del sistema")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /**
     * Constructor para inyección de dependencias del servicio y mapper de Usuario.
     * @param usuarioService Servicio para la lógica de negocio de usuarios.
     * @param usuarioMapper Mapper para convertir entre Usuario y DTOs.
     */
    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema.
     * Requiere rol ADMIN o permiso LEER_USUARIOS.
     *
     * @return ResponseEntity con la lista de {@link UsuarioResponseDTO} y estado HTTP 200 (OK).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_USUARIOS')")
    @Operation(summary = "Lista todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosLosUsuarios() {
        log.info("GET /api/usuarios - Solicitud para listar todos los usuarios");
        List<Usuario> usuarios = usuarioService.listarTodosLosUsuarios();
        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("GET /api/usuarios - Devolviendo {} usuarios", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Recupera los detalles de un usuario específico usando su ID.
     * Requiere rol ADMIN o permiso LEER_USUARIOS.
     *
     * @param id El ID único del usuario a obtener.
     * @return ResponseEntity con el {@link UsuarioResponseDTO} encontrado y estado HTTP 200 (OK).
     * @throws ResourceNotFoundException Si el usuario con el ID dado no existe (manejado por GlobalExceptionHandler -> 404).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_USUARIOS')")
    @Operation(summary = "Obtiene un usuario por ID", description = "Recupera los detalles de un usuario específico usando su ID.")
    @Parameter(name = "id", description = "ID único del usuario", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("GET /api/usuarios/{} - Solicitud para obtener usuario por ID", id);
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        UsuarioResponseDTO dto = usuarioMapper.toResponseDTO(usuario);
        log.info("GET /api/usuarios/{} - Usuario encontrado: {}", id, dto.getEmail());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Requiere rol ADMIN o permiso CREAR_USUARIO.
     * Valida los datos de entrada y la unicidad del email.
     *
     * @param usuarioDto El DTO {@link UsuarioRequestDTO} con los datos del usuario a crear (nombre, email, password, rolId).
     * @return ResponseEntity con el {@link UsuarioResponseDTO} del usuario creado y estado HTTP 201 (Created).
     * @throws DuplicateResourceException Si el email ya está registrado (manejado por GlobalExceptionHandler -> 409).
     * @throws ResourceNotFoundException Si el rol especificado no existe (manejado por GlobalExceptionHandler -> 404).
     * @throws InvalidDataException Si la contraseña no cumple los requisitos o falta (manejado por GlobalExceptionHandler -> 400/422).
     * @throws MethodArgumentNotValidException Si los datos del DTO fallan la validación (manejado por GlobalExceptionHandler -> 400).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREAR_USUARIO')") // Maneja el 403 Forbidden
    @Operation(
            summary = "Crea un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema con su nombre, email, contraseña y rol.",
            // --- El RequestBody se define DENTRO de @Operation ---
            requestBody = @RequestBody(
                    description = "Datos del usuario a crear. Nombre, email, contraseña y rolId son obligatorios.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioRequestDTO.class))
            )
            // --- Fin de RequestBody ---
    )
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (DTO, contraseña inválida)", content = @Content) // Manejado por @Valid y GlobalExceptionHandler
    @ApiResponse(responseCode = "409", description = "Conflicto - Email ya registrado", content = @Content) // Manejado por GlobalExceptionHandler (DuplicateResourceException)
    @ApiResponse(responseCode = "404", description = "No encontrado - Rol especificado no existe", content = @Content) // Manejado por GlobalExceptionHandler (ResourceNotFoundException)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content) // Manejado por @PreAuthorize
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @org.springframework.web.bind.annotation.RequestBody UsuarioRequestDTO usuarioDto // @Valid activa la validación del DTO
    ) {
        log.info("POST /api/usuarios - Solicitud para crear usuario: {}", usuarioDto.getEmail());
        // Llama al servicio que contiene la lógica de negocio (incluida la verificación de email duplicado)
        Usuario nuevoUsuario = usuarioService.registrarNuevoUsuario(usuarioDto);
        UsuarioResponseDTO responseDto = usuarioMapper.toResponseDTO(nuevoUsuario);
        log.info("POST /api/usuarios - Usuario {} creado con ID: {}", responseDto.getEmail(), responseDto.getUsuarioId());
        // Devuelve 201 CREATED en caso de éxito
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    /**
     * Modifica los detalles de un usuario existente, incluyendo opcionalmente la contraseña y el rol.
     * Requiere rol ADMIN o permiso EDITAR_USUARIO.
     *
     * @param id El ID del usuario a actualizar.
     * @param usuarioDto El DTO {@link UsuarioRequestDTO} con los datos actualizados. La contraseña es opcional (si no se envía, no se cambia).
     * @return ResponseEntity con el {@link UsuarioResponseDTO} del usuario actualizado y estado HTTP 200 (OK).
     * @throws ResourceNotFoundException Si el usuario o el rol especificado no existen (manejado por GlobalExceptionHandler -> 404).
     * @throws DuplicateResourceException Si el nuevo email ya está en uso por otro usuario (manejado por GlobalExceptionHandler -> 409).
     * @throws InvalidDataException Si la nueva contraseña no cumple requisitos (manejado por GlobalExceptionHandler -> 400/422).
     * @throws IllegalOperationException Si se intenta modificar al último administrador (manejado por GlobalExceptionHandler -> 400).
     * @throws MethodArgumentNotValidException Si los datos del DTO fallan la validación (manejado por GlobalExceptionHandler -> 400).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('EDITAR_USUARIO')")
    @Operation(
            summary = "Actualiza un usuario existente",
            description = "Modifica los detalles de un usuario, incluyendo opcionalmente la contraseña y el rol.",
            // --- El RequestBody se define DENTRO de @Operation ---
            requestBody = @RequestBody(
                    description = "Datos actualizados del usuario. La contraseña es opcional (si no se envía, no se cambia).",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioRequestDTO.class))
            )
            // --- Fin de RequestBody ---
    )
    @Parameter(name = "id", description = "ID del usuario a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    // --- @RequestBody (Swagger) ELIMINADA DE AQUÍ ---
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (DTO, contraseña, operación ilegal)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Usuario o Rol no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Email ya registrado por otro usuario", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            // La anotación de Spring @RequestBody para el parámetro sigue siendo necesaria
            @Valid @org.springframework.web.bind.annotation.RequestBody UsuarioRequestDTO usuarioDto
    ) {
        log.info("PUT /api/usuarios/{} - Solicitud para actualizar usuario", id);
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDto);
        UsuarioResponseDTO responseDto = usuarioMapper.toResponseDTO(usuarioActualizado);
        log.info("PUT /api/usuarios/{} - Usuario actualizado", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Elimina un usuario del sistema.
     * Requiere rol ADMIN o permiso ELIMINAR_USUARIO.
     * No se permite eliminar al último administrador del sistema.
     *
     * @param id El ID del usuario a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content) si la eliminación es exitosa.
     * @throws ResourceNotFoundException Si el usuario no existe (manejado por GlobalExceptionHandler -> 404).
     * @throws IllegalOperationException Si se intenta eliminar al último administrador (manejado por GlobalExceptionHandler -> 400).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ELIMINAR_USUARIO')")
    @Operation(summary = "Elimina un usuario", description = "Elimina un usuario del sistema. No se puede eliminar al último administrador.")
    @Parameter(name = "id", description = "ID del usuario a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "400", description = "Operación ilegal (ej. eliminar último admin)", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - Solicitud para eliminar usuario", id);
        usuarioService.eliminarUsuario(id);
        log.info("DELETE /api/usuarios/{} - Usuario eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Busca usuarios cuyo nombre o email contengan el término proporcionado (case-insensitive).
     * Requiere rol ADMIN o permiso BUSCAR_USUARIOS.
     *
     * @param termino El texto a buscar en el nombre o email.
     * @return ResponseEntity con la lista de {@link UsuarioResponseDTO} encontrados y estado HTTP 200 (OK).
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BUSCAR_USUARIOS')")
    @Operation(summary = "Busca usuarios por nombre o email", description = "Obtiene una lista de usuarios cuyo nombre o email contienen el término de búsqueda (case-insensitive).")
    @Parameter(name = "termino", description = "Texto a buscar en nombre o email", required = true, example = "oper")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombreOEmail(@RequestParam String termino) {
        log.info("GET /api/usuarios/buscar?termino={} - Buscando usuarios", termino);
        List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombreOEmail(termino);
        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("GET /api/usuarios/buscar?termino={} - Encontrados {} usuarios", termino, dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene una lista de todos los usuarios asignados a un rol específico (por nombre de rol).
     * Requiere rol ADMIN o permiso LISTAR_USUARIOS_POR_ROL.
     *
     * @param rolNombre El nombre exacto del rol (ej. "OPERARIO", "ADMIN"). Case-sensitive.
     * @return ResponseEntity con la lista de {@link UsuarioResponseDTO} encontrados y estado HTTP 200 (OK).
     */
    @GetMapping("/rol/{rolNombre}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LISTAR_USUARIOS_POR_ROL')")
    @Operation(summary = "Lista usuarios por nombre de rol", description = "Obtiene una lista de todos los usuarios asignados a un rol específico.")
    @Parameter(name = "rolNombre", description = "Nombre exacto del rol (ej. OPERARIO, ADMIN). Case-sensitive.", required = true, example = "OPERARIO")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados para el rol especificado",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosPorRol(@PathVariable String rolNombre) {
        log.info("GET /api/usuarios/rol/{} - Listando usuarios por rol", rolNombre);
        List<Usuario> usuarios = usuarioService.listarUsuariosPorRol(rolNombre);
        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("GET /api/usuarios/rol/{} - Encontrados {} usuarios", rolNombre, dtos.size());
        return ResponseEntity.ok(dtos);
    }
}