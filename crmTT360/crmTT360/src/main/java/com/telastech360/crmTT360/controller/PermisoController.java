package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.service.PermisoService;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Importa RequestBody de swagger


@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Gestión de Permisos en el sistema") // Anotación Tag
public class PermisoController {

    private final PermisoService permisoService;

    @Autowired
    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    // ========== ENDPOINTS CRUD ========== //

    @GetMapping
    // Ejemplo: Solo ADMIN puede listar todos los permisos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos los permisos", description = "Obtiene una lista de todos los permisos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Permiso>> listarTodosLosPermisos() {
        List<Permiso> permisos = permisoService.listarTodosLosPermisos();
        return new ResponseEntity<>(permisos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Solo ADMIN puede obtener un permiso por ID
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtiene un permiso por ID", description = "Recupera los detalles de un permiso específico usando su ID.")
    @Parameter(name = "id", description = "ID del permiso a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Permiso encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Permiso> obtenerPermisoPorId(@PathVariable Long id) {
        Permiso permiso = permisoService.obtenerPermisoPorId(id);
        return new ResponseEntity<>(permiso, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Solo ADMIN puede crear permisos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuevo permiso", description = "Registra un nuevo permiso en el sistema.")
    @RequestBody(description = "Datos del permiso a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Permiso> crearPermiso(@RequestBody Permiso permiso) {
        Permiso nuevoPermiso = permisoService.crearPermiso(permiso);
        return new ResponseEntity<>(nuevoPermiso, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Solo ADMIN puede actualizar permisos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualiza un permiso existente", description = "Modifica los detalles de un permiso usando su ID.")
    @Parameter(name = "id", description = "ID del permiso a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del permiso", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Permiso> actualizarPermiso(
            @PathVariable Long id,
            @RequestBody Permiso permisoActualizado
    ) {
        Permiso permiso = permisoService.actualizarPermiso(id, permisoActualizado);
        return new ResponseEntity<>(permiso, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar permisos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un permiso", description = "Elimina un permiso del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del permiso a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Permiso eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        permisoService.eliminarPermiso(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Aquí podrías agregar más endpoints adicionales si es necesario,
    // y aplicarles la anotación @Operation, @Parameter, @ApiResponse correspondiente.
}