// src/main/java/com/telastech360/crmTT360/controller/RolController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.RolDTO;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.mapper.RolMapper;
import com.telastech360.crmTT360.service.RolService;
// Importaciones para Validación Manual y manejo de errores Y @Valid
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid; // <--- IMPORTANTE: Asegúrate que esta línea exista
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Swagger RequestBody
import io.swagger.v3.oas.annotations.media.ArraySchema;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Gestión de Roles de usuario")
public class RolController {

    private static final Logger log = LoggerFactory.getLogger(RolController.class);

    private final RolService rolService;
    private final RolMapper rolMapper;
    @Autowired
    private Validator validator;

    @Autowired
    public RolController(RolService rolService, RolMapper rolMapper) {
        this.rolService = rolService;
        this.rolMapper = rolMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_ROLES')")
    @Operation(summary = "Lista todos los roles", description = "Obtiene una lista de todos los roles definidos en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RolDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<RolDTO>> listarTodosLosRoles() {
        log.info("GET /api/roles - Solicitud para listar todos los roles");
        List<Rol> roles = rolService.listarTodosLosRoles();
        List<RolDTO> dtos = roles.stream()
                .map(rolMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/roles - Devolviendo {} roles", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_ROLES')")
    @Operation(summary = "Obtiene un rol por ID", description = "Recupera los detalles de un rol específico usando su ID.")
    @Parameter(name = "id", description = "ID único del rol", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Rol encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolDTO.class)))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable Long id) {
        log.info("GET /api/roles/{} - Solicitud para obtener rol por ID", id);
        Rol rol = rolService.obtenerRolPorId(id);
        RolDTO dto = rolMapper.toDTO(rol);
        log.info("GET /api/roles/{} - Rol encontrado: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREAR_ROL')")
    @Operation(summary = "Crea un nuevo rol", description = "Registra un nuevo rol de usuario en el sistema. El nombre debe ser único.")
    @RequestBody(description = "Datos del rol a crear (nombre es obligatorio)", required = true, content = @Content(schema = @Schema(implementation = RolDTO.class)))
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo de validación manual)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de rol ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<?> crearRol(@org.springframework.web.bind.annotation.RequestBody RolDTO rolDTO) {

        log.debug(">>> [DEBUG INICIO crearRol] RolDTO recibido: {}", rolDTO);
        if (rolDTO != null) {
            log.debug(">>> [DEBUG crearRol] rolDTO.getNombre() = '{}'", rolDTO.getNombre());
            log.debug(">>> [DEBUG crearRol] rolDTO.getDescripcion() = '{}'", rolDTO.getDescripcion());
        } else {
            log.warn(">>> [DEBUG crearRol] ¡El objeto rolDTO recibido es NULL!");
            return ResponseEntity.badRequest().body("El cuerpo de la solicitud está vacío o no es JSON.");
        }

        Set<ConstraintViolation<RolDTO>> violations = validator.validate(rolDTO);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toList());
            log.warn("Validación manual falló para RolDTO: {}", errorMessages);
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("error", "Bad Request");
            errorBody.put("message", "Errores de validación manual");
            errorBody.put("path", "/api/roles");
            errorBody.put("errors", errorMessages);
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
        log.debug(">>> [DEBUG crearRol] Validación manual superada.");

        log.info("POST /api/roles - Solicitud para crear rol (después de debug y validación manual): {}", rolDTO.getNombre());
        try {
            Rol rolACrear = rolMapper.toEntity(rolDTO);
            Rol nuevoRol = rolService.crearRol(rolACrear);
            RolDTO responseDto = rolMapper.toDTO(nuevoRol);
            log.info("POST /api/roles - Rol '{}' creado con ID: {}", responseDto.getNombre(), nuevoRol.getRolId());
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (DuplicateResourceException e) {
            log.warn("Conflicto al crear rol: {}", e.getMessage());
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.CONFLICT.value());
            errorBody.put("error", "Conflict");
            errorBody.put("message", e.getMessage());
            errorBody.put("path", "/api/roles");
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error inesperado al crear rol: {}", e.getMessage(), e);
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorBody.put("error", "Internal Server Error");
            errorBody.put("message", "Error interno del servidor al crear el rol.");
            errorBody.put("path", "/api/roles");
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Método PUT con @Valid (asegurar que el import esté presente) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('EDITAR_ROL')")
    @Operation(summary = "Actualiza un rol existente", description = "Modifica el nombre y/o descripción de un rol.")
    @Parameter(name = "id", description = "ID del rol a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos actualizados del rol", required = true, content = @Content(schema = @Schema(implementation = RolDTO.class)))
    @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de rol ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<RolDTO> actualizarRol(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody RolDTO rolDTO // @Valid se usa aquí
    ) {
        log.debug(">>> [DEBUG INICIO actualizarRol] ID: {}, RolDTO recibido: {}", id, rolDTO);
        if (rolDTO != null) {
            log.debug(">>> [DEBUG actualizarRol] rolDTO.getNombre() = '{}'", rolDTO.getNombre());
        }
        log.info("PUT /api/roles/{} - Solicitud para actualizar rol", id);
        Rol rolActualizado = rolMapper.toEntity(rolDTO);
        Rol rolGuardado = rolService.actualizarRol(id, rolActualizado);
        RolDTO responseDto = rolMapper.toDTO(rolGuardado);
        log.info("PUT /api/roles/{} - Rol actualizado", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // --- Método DELETE (sin cambios) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ELIMINAR_ROL')")
    @Operation(summary = "Elimina un rol", description = "Elimina un rol del sistema. Falla si está asignado a usuarios.")
    @Parameter(name = "id", description = "ID del rol a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El rol está asignado a usuarios", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        log.info("DELETE /api/roles/{} - Solicitud para eliminar rol", id);
        rolService.eliminarRol(id);
        log.info("DELETE /api/roles/{} - Rol eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}