// src/main/java/com/telastech360/crmTT360/controller/ProveedorController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ProveedorDTO;
import com.telastech360.crmTT360.mapper.ProveedorMapper;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.service.ProveedorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación

import java.util.List;
import java.util.stream.Collectors;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;

/**
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con los Proveedores.
 */
@RestController
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Gestión de Proveedores en el sistema")
public class ProveedorController {

    private static final Logger log = LoggerFactory.getLogger(ProveedorController.class);

    private final ProveedorService proveedorService;
    private final ProveedorMapper proveedorMapper;

    @Autowired
    public ProveedorController(ProveedorService proveedorService, ProveedorMapper proveedorMapper) {
        this.proveedorService = proveedorService;
        this.proveedorMapper = proveedorMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_PROVEEDORES')") // Modificado
    @Operation(summary = "Lista todos los proveedores", description = "Obtiene una lista de todos los proveedores registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProveedorDTO.class))))
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProveedorDTO>> listarTodosLosProveedores() {
        log.info("GET /api/proveedores - Solicitud para listar todos los proveedores");
        List<Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
        List<ProveedorDTO> dtos = proveedores.stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/proveedores - Devolviendo {} proveedores.", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_PROVEEDORES')") // Modificado
    @Operation(summary = "Obtiene un proveedor por ID", description = "Recupera los detalles de un proveedor específico.")
    @Parameter(name = "id", description = "ID único del proveedor", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProveedorDTO> obtenerProveedorPorId(@PathVariable Long id) {
        log.info("GET /api/proveedores/{} - Solicitud para obtener proveedor por ID", id);
        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
        ProveedorDTO dto = proveedorMapper.toDTO(proveedor);
        log.info("GET /api/proveedores/{} - Proveedor encontrado: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_PROVEEDOR')") // Modificado (o GESTIONAR_PROVEEDORES)
    @Operation(summary = "Crea un nuevo proveedor", description = "Registra un nuevo proveedor en el sistema.")
    @RequestBody(description = "Datos del proveedor a crear (nombre, dirección, teléfono, email son obligatorios)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Email o teléfono ya registrado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProveedorDTO> crearProveedor(
            @Valid @RequestBody ProveedorDTO proveedorDto
    ) {
        log.info("POST /api/proveedores - Solicitud para crear proveedor: {}", proveedorDto.getEmail());
        Proveedor proveedorACrear = proveedorMapper.toEntity(proveedorDto);
        Proveedor nuevoProveedor = proveedorService.crearProveedor(proveedorACrear);
        ProveedorDTO responseDto = proveedorMapper.toDTO(nuevoProveedor);
        log.info("POST /api/proveedores - Proveedor '{}' creado con ID: {}", responseDto.getNombre(), responseDto.getProveedorId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_PROVEEDOR')") // Modificado (o GESTIONAR_PROVEEDORES)
    @Operation(summary = "Actualiza un proveedor existente", description = "Modifica los detalles de un proveedor existente.")
    @Parameter(name = "id", description = "ID del proveedor a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos actualizados del proveedor", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Email o teléfono ya registrado por otro proveedor", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProveedorDTO> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO proveedorDto
    ) {
        log.info("PUT /api/proveedores/{} - Solicitud para actualizar proveedor", id);
        Proveedor proveedorActualizado = proveedorMapper.toEntity(proveedorDto);
        Proveedor proveedorGuardado = proveedorService.actualizarProveedor(id, proveedorActualizado);
        ProveedorDTO responseDto = proveedorMapper.toDTO(proveedorGuardado);
        log.info("PUT /api/proveedores/{} - Proveedor actualizado.", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_PROVEEDOR')") // Modificado
    @Operation(summary = "Elimina un proveedor", description = "Elimina un proveedor del sistema. Falla si tiene ítems asociados.")
    @Parameter(name = "id", description = "ID del proveedor a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El proveedor tiene ítems asociados", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        log.info("DELETE /api/proveedores/{} - Solicitud para eliminar proveedor", id);
        proveedorService.eliminarProveedor(id);
        log.info("DELETE /api/proveedores/{} - Proveedor eliminado.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('BUSCAR_PROVEEDORES')") // Modificado (o LEER_PROVEEDORES)
    @Operation(summary = "Busca proveedores por nombre y/o ubicación", description = "Obtiene proveedores filtrados opcionalmente por nombre y/o ubicación (dirección). Si no se envían parámetros, lista todos.")
    @Parameter(name = "nombre", description = "Texto a buscar en el nombre del proveedor (opcional)", required = false, example = "Textiles")
    @Parameter(name = "ubicacion", description = "Texto a buscar en la dirección/ubicación del proveedor (opcional)", required = false, example = "Bogotá")
    @ApiResponse(responseCode = "200", description = "Proveedores encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProveedorDTO.class))))
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProveedorDTO>> buscarProveedores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ubicacion
    ) {
        log.info("GET /api/proveedores/buscar - Buscando con nombre '{}' y ubicación '{}'", nombre, ubicacion);
        List<Proveedor> proveedores;
        if (nombre != null && ubicacion != null) {
            proveedores = proveedorService.buscarProveedoresPorNombreYUbicacion(nombre, ubicacion);
        } else if (nombre != null) {
            proveedores = proveedorService.buscarProveedoresPorNombre(nombre);
        } else if (ubicacion != null) {
            proveedores = proveedorService.buscarProveedoresPorUbicacion(ubicacion);
        } else {
            proveedores = proveedorService.listarTodosLosProveedores(); // Sin filtros, listar todos
        }
        List<ProveedorDTO> dtos = proveedores.stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/proveedores/buscar - Encontrados {} proveedores.", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('BUSCAR_PROVEEDORES')") // Modificado (o LEER_PROVEEDORES)
    @Operation(summary = "Busca un proveedor por email", description = "Obtiene un proveedor específico buscando por su dirección de email exacta.")
    @Parameter(name = "email", description = "Email exacto del proveedor", required = true, example = "contacto@proveedorA.com")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProveedorDTO.class)))
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado con ese email", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProveedorDTO> buscarPorEmail(@PathVariable String email) {
        log.info("GET /api/proveedores/email/{} - Solicitud para buscar proveedor por email", email);
        Proveedor proveedor = proveedorService.buscarPorEmail(email)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado con email: {}", email);
                    return new ResourceNotFoundException("Proveedor no encontrado con email: " + email);
                });
        ProveedorDTO dto = proveedorMapper.toDTO(proveedor);
        log.info("GET /api/proveedores/email/{} - Proveedor encontrado: ID {}", email, dto.getProveedorId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}