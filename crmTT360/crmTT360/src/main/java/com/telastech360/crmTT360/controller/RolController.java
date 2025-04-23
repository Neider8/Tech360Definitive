package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.RolDTO;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.mapper.RolMapper;
import com.telastech360.crmTT360.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Gestión de Roles de usuario en el sistema") // Anotación Tag
public class RolController {

    private final RolService rolService;
    private final RolMapper rolMapper; // Inyectar el mapper si se usa directamente

    @Autowired
    public RolController(RolService rolService, RolMapper rolMapper) {
        this.rolService = rolService;
        this.rolMapper = rolMapper; // Inyectar mapper
    }

    @GetMapping
    // Ejemplo: Solo ADMIN puede listar todos los roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos los roles", description = "Obtiene una lista de todos los roles de usuario registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Rol>> listarTodosLosRoles() {
        List<Rol> roles = rolService.listarTodosLosRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Solo ADMIN puede obtener un rol por ID
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtiene un rol por ID", description = "Recupera los detalles de un rol de usuario específico usando su ID.")
    @Parameter(name = "id", description = "ID del rol a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Rol no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Long id) {
        Rol rol = rolService.obtenerRolPorId(id);
        return new ResponseEntity<>(rol, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Solo ADMIN puede crear roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuevo rol", description = "Registra un nuevo rol de usuario en el sistema.")
    @RequestBody(description = "Datos del rol a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RolDTO.class))) // Describe cuerpo solicitud (usando el DTO)
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class))) // Describe respuesta (asumo que el servicio devuelve la entidad guardada)
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto (ej: nombre de rol duplicado si aplica)") // O tu excepción
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Rol> crearRol(@Valid @RequestBody RolDTO rolDTO) {
        Rol nuevoRol = rolService.crearRol(rolMapper.toEntity(rolDTO)); // Usar mapper
        return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Solo ADMIN puede actualizar roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualiza un rol existente", description = "Modifica los detalles de un rol de usuario usando su ID.")
    @Parameter(name = "id", description = "ID del rol a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del rol", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RolDTO.class))) // Describe cuerpo solicitud (usando el DTO)
    @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Rol.class))) // Describe respuesta (asumo que el servicio devuelve la entidad actualizada)
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Rol> actualizarRol(
            @PathVariable Long id,
            @Valid @RequestBody RolDTO rolDTO
    ) {
        Rol rolActualizado = rolService.actualizarRol(id, rolMapper.toEntity(rolDTO)); // Usar mapper
        return new ResponseEntity<>(rolActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar roles
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un rol", description = "Elimina un rol de usuario del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del rol a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.eliminarRol(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Aquí podrías agregar más endpoints adicionales si es necesario,
    // y aplicarles la anotación @Operation, @Parameter, @ApiResponse correspondiente.
}