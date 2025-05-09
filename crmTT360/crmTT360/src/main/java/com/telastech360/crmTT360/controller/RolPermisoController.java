// src/main/java/com/telastech360/crmTT360/controller/RolPermisoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.PermisoDTO;
import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.mapper.PermisoMapper;
import com.telastech360.crmTT360.service.RolPermisoService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet; // <--- Asegurar esta importación
import java.util.List;    // <--- Asegurar esta importación
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

/**
 * Controlador REST para gestionar la asignación y desasignación de Permisos a Roles.
 */
@RestController
@RequestMapping("/api/roles-permisos")
@Tag(name = "Roles y Permisos", description = "Gestión de asignación de Permisos a Roles")
public class RolPermisoController {

    private static final Logger log = LoggerFactory.getLogger(RolPermisoController.class);

    private final RolPermisoService rolPermisoService;
    private final PermisoMapper permisoMapper;

    @Autowired
    public RolPermisoController(RolPermisoService rolPermisoService, PermisoMapper permisoMapper) {
        this.rolPermisoService = rolPermisoService;
        this.permisoMapper = permisoMapper;
    }

    @PostMapping("/{rolId}/permisos/{permisoId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MODIFICAR_PERMISOS_ROL')")
    @Operation(summary = "Asigna un permiso a un rol", description = "Crea una relación entre un rol y un permiso existentes.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "permisoId", description = "ID del permiso a asignar", required = true, example = "10", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "201", description = "Permiso asignado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Rol o Permiso no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - La asignación ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> asignarPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        log.info("POST /api/roles-permisos/{}/permisos/{} - Asignando permiso", rolId, permisoId);
        rolPermisoService.asignarPermiso(rolId, permisoId);
        log.info("POST /api/roles-permisos/{}/permisos/{} - Permiso asignado", rolId, permisoId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{rolId}/permisos/{permisoId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MODIFICAR_PERMISOS_ROL')")
    @Operation(summary = "Remueve un permiso de un rol", description = "Elimina la relación existente entre un rol y un permiso.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "permisoId", description = "ID del permiso a remover", required = true, example = "10", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Permiso removido exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Rol, Permiso o la asignación no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> removerPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        log.info("DELETE /api/roles-permisos/{}/permisos/{} - Removiendo permiso", rolId, permisoId);
        rolPermisoService.removerPermiso(rolId, permisoId);
        log.info("DELETE /api/roles-permisos/{}/permisos/{} - Permiso removido", rolId, permisoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{rolId}/permisos")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_PERMISOS_ROL')")
    @Operation(summary = "Lista IDs de permisos de un rol", description = "Obtiene únicamente los IDs numéricos de todos los permisos asignados a un rol específico.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Lista de IDs de permisos obtenida",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64"))))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<Long>> obtenerPermisosDeRol(@PathVariable Long rolId) {
        log.info("GET /api/roles-permisos/{}/permisos - Obteniendo IDs de permisos", rolId);
        List<Long> permisoIds = rolPermisoService.obtenerPermisosDeRol(rolId);
        log.info("GET /api/roles-permisos/{}/permisos - Encontrados {} IDs", rolId, permisoIds.size());
        return new ResponseEntity<>(permisoIds, HttpStatus.OK);
    }

    @GetMapping("/{rolId}/permisos-completos")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_PERMISOS_ROL')")
    @Operation(summary = "Lista permisos completos de un rol", description = "Obtiene los detalles completos (ID, nombre, descripción) de todos los permisos asignados a un rol específico.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Lista de permisos completos obtenida",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PermisoDTO.class))))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Set<PermisoDTO>> obtenerPermisosCompletosDeRol(@PathVariable Long rolId) {
        log.info("GET /api/roles-permisos/{}/permisos-completos - Obteniendo permisos completos", rolId);
        Set<Permiso> permisos = rolPermisoService.obtenerPermisosCompletosDeRol(rolId);
        Set<PermisoDTO> dtos = permisos.stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toSet());
        log.info("GET /api/roles-permisos/{}/permisos-completos - Encontrados {} permisos", rolId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // --- MÉTODO PUT CORREGIDO ---
    @PutMapping("/{rolId}/permisos")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MODIFICAR_PERMISOS_ROL')")
    @Operation(summary = "Actualiza (sobrescribe) permisos de un rol", description = "Establece el conjunto completo de permisos para un rol, reemplazando cualquier asignación anterior.")
    @Parameter(name = "rolId", description = "ID del rol cuyos permisos se actualizarán", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Conjunto de IDs de los permisos que el rol debe tener. Los permisos no incluidos serán desasignados.", required = true,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64", example="[10, 12, 15]"))))
    @ApiResponse(responseCode = "200", description = "Permisos actualizados exitosamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. lista vacía o formato incorrecto)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Rol o alguno de los Permisos no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> actualizarPermisosDeRol(
            @PathVariable Long rolId,
            // --- CAMBIO: Usar List<Long> en lugar de Set<Long> ---
            @org.springframework.web.bind.annotation.RequestBody List<Long> nuevosPermisoIds) {
        log.info("PUT /api/roles-permisos/{}/permisos - Actualizando permisos con IDs: {}", rolId, nuevosPermisoIds);
        // --- CAMBIO: Convertir List a Set antes de llamar al servicio ---
        rolPermisoService.actualizarPermisosDeRol(rolId, new HashSet<>(nuevosPermisoIds));
        log.info("PUT /api/roles-permisos/{}/permisos - Permisos actualizados", rolId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{rolId}/permisos/{permisoId}/existe")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LEER_PERMISOS_ROL')")
    @Operation(summary = "Verifica si un permiso está asignado a un rol", description = "Comprueba si existe una relación directa entre un rol y un permiso específicos.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "permisoId", description = "ID del permiso", required = true, example = "10", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Verificación realizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado (implícito si la relación no puede existir)", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Boolean> existeRelacionRolPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        log.debug("GET /api/roles-permisos/{}/permisos/{}/existe - Verificando relación", rolId, permisoId);
        boolean existe = rolPermisoService.existeRelacionRolPermiso(rolId, permisoId);
        log.debug("GET /api/roles-permisos/{}/permisos/{}/existe - Resultado: {}", rolId, permisoId, existe);
        return new ResponseEntity<>(existe, HttpStatus.OK);
    }
}