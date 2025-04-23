package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.service.RolPermisoService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
@RequestMapping("/api/roles-permisos")
@Tag(name = "Roles y Permisos", description = "Gestión de asignación de Permisos a Roles de usuario") // Anotación Tag
public class RolPermisoController {

    private final RolPermisoService rolPermisoService;

    @Autowired
    public RolPermisoController(RolPermisoService rolPermisoService) {
        this.rolPermisoService = rolPermisoService;
    }

    // ========== ENDPOINTS CRUD ========== //

    @PostMapping("/{rolId}/permisos/{permisoId}")
    // Ejemplo: Solo ADMIN puede asignar permisos a roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asigna un permiso a un rol", description = "Crea una relación entre un rol y un permiso, otorgando el permiso al rol.")
    @Parameter(name = "rolId", description = "ID del rol al que se asignará el permiso", required = true, example = "1") // Describe parámetro
    @Parameter(name = "permisoId", description = "ID del permiso a asignar", required = true, example = "10") // Describe parámetro
    @ApiResponse(responseCode = "201", description = "Permiso asignado exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "409", description = "Conflicto (Si la asignación ya existe)") // Si aplica
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> asignarPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        rolPermisoService.asignarPermiso(rolId, permisoId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{rolId}/permisos/{permisoId}")
    // Ejemplo: Solo ADMIN puede remover permisos de roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remueve un permiso de un rol", description = "Elimina la relación entre un rol y un permiso.")
    @Parameter(name = "rolId", description = "ID del rol del que se removerá el permiso", required = true, example = "1")
    @Parameter(name = "permisoId", description = "ID del permiso a remover", required = true, example = "10")
    @ApiResponse(responseCode = "204", description = "Permiso removido exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado, o asignación no existe") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> removerPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        rolPermisoService.removerPermiso(rolId, permisoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{rolId}/permisos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista IDs de permisos de un rol", description = "Obtiene una lista de los IDs de todos los permisos asignados a un rol específico.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Lista de IDs de permisos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    // Usar @ArraySchema para describir la respuesta como un array
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64")))) // Describe los items del array (Long)
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Long>> obtenerPermisosDeRol(@PathVariable Long rolId) {
        List<Long> permisoIds = rolPermisoService.obtenerPermisosDeRol(rolId);
        return new ResponseEntity<>(permisoIds, HttpStatus.OK);
    }

    @GetMapping("/{rolId}/permisos-completos")
    // Ejemplo: Solo ADMIN puede obtener los detalles completos de permisos de un rol
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista permisos completos de un rol", description = "Obtiene la lista completa de entidades Permiso asignadas a un rol específico.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Permiso.class))) // Describe la respuesta (Set de entidad)
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Set<Permiso>> obtenerPermisosCompletosDeRol(@PathVariable Long rolId) {
        Set<Permiso> permisos = rolPermisoService.obtenerPermisosCompletosDeRol(rolId);
        return new ResponseEntity<>(permisos, HttpStatus.OK);
    }

    @PutMapping("/{rolId}/permisos")
// Ejemplo: Solo ADMIN puede actualizar el conjunto completo de permisos de un rol
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualiza permisos de un rol", description = "Establece el conjunto completo de permisos para un rol específico, reemplazando las asignaciones existentes.")
    @Parameter(name = "rolId", description = "ID del rol al que se actualizarán los permisos", required = true, example = "1")
    @RequestBody(description = "Conjunto de IDs de permisos a asignar al rol", required = true,
            content = @Content(mediaType = "application/json",
                    // Usar @ArraySchema para describir el cuerpo de solicitud como un array/set
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64")))) // Describe los items del array/set (Long)
    @ApiResponse(responseCode = "200", description = "Permisos del rol actualizados exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Rol o alguno de los Permisos no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> actualizarPermisosDeRol(
            @PathVariable Long rolId,
            @RequestBody Set<Long> nuevosPermisoIds) {
        rolPermisoService.actualizarPermisosDeRol(rolId, nuevosPermisoIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{rolId}/permisos/{permisoId}/existe")
    // Ejemplo: Solo ADMIN puede verificar si una relación rol-permiso existe
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verifica asignación de permiso", description = "Verifica si un permiso específico está asignado a un rol.")
    @Parameter(name = "rolId", description = "ID del rol", required = true, example = "1")
    @Parameter(name = "permisoId", description = "ID del permiso", required = true, example = "10")
    @ApiResponse(responseCode = "200", description = "Resultado de la verificación",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class))) // Describe la respuesta (Boolean)
    @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Boolean> existeRelacionRolPermiso(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        boolean existe = rolPermisoService.existeRelacionRolPermiso(rolId, permisoId);
        return new ResponseEntity<>(existe, HttpStatus.OK);
    }
}