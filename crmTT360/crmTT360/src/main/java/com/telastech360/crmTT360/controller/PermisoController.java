// src/main/java/com/telastech360/crmTT360/controller/PermisoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.PermisoDTO;
import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.exception.DuplicateResourceException; // Importar
import com.telastech360.crmTT360.mapper.PermisoMapper;
import com.telastech360.crmTT360.service.PermisoService;

// Importaciones para Validación Manual y manejo de errores Y @Valid
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid; // Mantener para actualizarPermiso
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Swagger RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para gestionar las operaciones CRUD de la entidad Permiso.
 * Los permisos definen acciones específicas que pueden ser asignadas a roles.
 * Todas las operaciones sobre permisos requieren el rol de ADMIN.
 */
@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Gestión de Permisos del sistema (solo ADMIN)")
public class PermisoController {

    private static final Logger log = LoggerFactory.getLogger(PermisoController.class);

    private final PermisoService permisoService;
    private final PermisoMapper permisoMapper;
    // Inyectar el Validator estándar de Jakarta/Spring
    @Autowired
    private Validator validator;

    @Autowired
    public PermisoController(PermisoService permisoService, PermisoMapper permisoMapper) {
        this.permisoService = permisoService;
        this.permisoMapper = permisoMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos los permisos", description = "Obtiene una lista de todos los permisos definidos en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermisoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado (Requiere rol ADMIN)", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PermisoDTO>> listarTodosLosPermisos() {
        log.info("GET /api/permisos - Solicitud para listar todos los permisos");
        List<Permiso> permisos = permisoService.listarTodosLosPermisos();
        List<PermisoDTO> permisosDTO = permisos.stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/permisos - Devolviendo {} permisos", permisosDTO.size());
        return new ResponseEntity<>(permisosDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtiene un permiso por ID", description = "Recupera los detalles de un permiso específico usando su ID.")
    @Parameter(name = "id", description = "ID único del permiso", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Permiso encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermisoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PermisoDTO> obtenerPermisoPorId(@PathVariable Long id) {
        log.info("GET /api/permisos/{} - Solicitud para obtener permiso por ID", id);
        Permiso permiso = permisoService.obtenerPermisoPorId(id);
        PermisoDTO permisoDTO = permisoMapper.toDTO(permiso);
        log.info("GET /api/permisos/{} - Permiso encontrado: {}", id, permisoDTO.getNombre());
        return new ResponseEntity<>(permisoDTO, HttpStatus.OK);
    }

    // --- MÉTODO POST MODIFICADO ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuevo permiso", description = "Registra un nuevo permiso en el sistema. El nombre debe ser único.")
    @RequestBody(description = "Datos del permiso a crear (nombre es obligatorio)", required = true, content = @Content(schema = @Schema(implementation = PermisoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermisoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. nombre vacío, fallo validación manual)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de permiso ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    // Se quitó @Valid y se cambió tipo de retorno
    public ResponseEntity<?> crearPermiso(@org.springframework.web.bind.annotation.RequestBody PermisoDTO permisoDTO) {

        // ----- Logs de Depuración -----
        log.debug(">>> [DEBUG INICIO crearPermiso] PermisoDTO recibido: {}", permisoDTO);
        if (permisoDTO != null) {
            log.debug(">>> [DEBUG crearPermiso] permisoDTO.getNombre() = '{}'", permisoDTO.getNombre());
            log.debug(">>> [DEBUG crearPermiso] permisoDTO.getDescripcion() = '{}'", permisoDTO.getDescripcion());
        } else {
            log.warn(">>> [DEBUG crearPermiso] ¡El objeto permisoDTO recibido es NULL!");
            return ResponseEntity.badRequest().body("El cuerpo de la solicitud está vacío o no es JSON.");
        }
        // ----- Fin Logs -----

        // ----- INICIO: Validación Manual -----
        Set<ConstraintViolation<PermisoDTO>> violations = validator.validate(permisoDTO);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toList());
            log.warn("Validación manual falló para PermisoDTO: {}", errorMessages);
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("error", "Bad Request");
            errorBody.put("message", "Errores de validación manual");
            errorBody.put("path", "/api/permisos");
            errorBody.put("errors", errorMessages);
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
        log.debug(">>> [DEBUG crearPermiso] Validación manual superada.");
        // ----- FIN: Validación Manual -----

        log.info("POST /api/permisos - Solicitud para crear permiso (después de validación): {}", permisoDTO.getNombre());
        try {
            Permiso permiso = permisoMapper.toEntity(permisoDTO);
            Permiso nuevoPermiso = permisoService.crearPermiso(permiso);
            PermisoDTO nuevoPermisoDTO = permisoMapper.toDTO(nuevoPermiso);
            log.info("POST /api/permisos - Permiso '{}' creado con ID: {}", nuevoPermisoDTO.getNombre(), nuevoPermiso.getPermisoId());
            return new ResponseEntity<>(nuevoPermisoDTO, HttpStatus.CREATED);
        } catch (DuplicateResourceException e) {
            log.warn("Conflicto al crear permiso: {}", e.getMessage());
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.CONFLICT.value());
            errorBody.put("error", "Conflict");
            errorBody.put("message", e.getMessage());
            errorBody.put("path", "/api/permisos");
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error inesperado al crear permiso: {}", e.getMessage(), e);
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorBody.put("error", "Internal Server Error");
            errorBody.put("message", "Error interno del servidor al crear el permiso.");
            errorBody.put("path", "/api/permisos");
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Método PUT (Mantenido con @Valid) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualiza un permiso existente", description = "Modifica el nombre y/o descripción de un permiso.")
    @Parameter(name = "id", description = "ID del permiso a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos actualizados del permiso", required = true, content = @Content(schema = @Schema(implementation = PermisoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermisoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de permiso ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PermisoDTO> actualizarPermiso(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody PermisoDTO permisoDTO // @Valid mantenido aquí
    ) {
        log.info("PUT /api/permisos/{} - Solicitud para actualizar permiso", id);
        Permiso permisoActualizadoDesdeDTO = permisoMapper.toEntity(permisoDTO);
        Permiso permisoActualizado = permisoService.actualizarPermiso(id, permisoActualizadoDesdeDTO);
        PermisoDTO permisoActualizadoRespuestaDTO = permisoMapper.toDTO(permisoActualizado);
        log.info("PUT /api/permisos/{} - Permiso actualizado", id);
        return new ResponseEntity<>(permisoActualizadoRespuestaDTO, HttpStatus.OK);
    }

    // --- Método DELETE (sin cambios) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un permiso", description = "Elimina un permiso del sistema. Falla si está asignado a algún rol.")
    @Parameter(name = "id", description = "ID del permiso a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Permiso eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El permiso está asignado a roles", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        log.info("DELETE /api/permisos/{} - Solicitud para eliminar permiso", id);
        permisoService.eliminarPermiso(id);
        log.info("DELETE /api/permisos/{} - Permiso eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}