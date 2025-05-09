// src/main/java/com/telastech360/crmTT360/controller/BodegaController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.BodegaDTO;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.mapper.BodegaMapper;
import com.telastech360.crmTT360.service.BodegaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;

/**
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con las Bodegas.
 */
@RestController
@RequestMapping("/api/bodegas")
@Tag(name = "Bodegas", description = "Gestión de Bodegas en el sistema")
public class BodegaController {

    private static final Logger log = LoggerFactory.getLogger(BodegaController.class);

    private final BodegaService bodegaService;
    private final BodegaMapper bodegaMapper;

    @Autowired
    public BodegaController(BodegaService bodegaService, BodegaMapper bodegaMapper) {
        this.bodegaService = bodegaService;
        this.bodegaMapper = bodegaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_BODEGAS')") // Modificado
    @Operation(summary = "Lista todas las bodegas", description = "Obtiene una lista de todas las bodegas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de bodegas obtenida",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BodegaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<BodegaDTO>> listarTodasLasBodegas() {
        log.info("GET /api/bodegas - Solicitud para listar todas las bodegas");
        List<Bodega> bodegas = bodegaService.listarTodasLasBodegas();
        List<BodegaDTO> dtos = bodegas.stream()
                .map(bodegaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/bodegas - Devolviendo {} bodegas", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_BODEGAS')") // Modificado
    @Operation(summary = "Obtiene una bodega por ID", description = "Recupera los detalles de una bodega específica.")
    @Parameter(name = "id", description = "ID único de la bodega", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Bodega encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BodegaDTO> obtenerBodegaPorId(@PathVariable Long id) {
        log.info("GET /api/bodegas/{} - Solicitud para obtener bodega por ID", id);
        Bodega bodega = bodegaService.obtenerBodegaPorId(id);
        BodegaDTO dto = bodegaMapper.toDTO(bodega);
        log.info("GET /api/bodegas/{} - Bodega encontrada: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_BODEGA')") // Modificado (o GESTIONAR_BODEGAS)
    @Operation(summary = "Crea una nueva bodega", description = "Registra una nueva bodega en el sistema.")
    @RequestBody(description = "Datos de la bodega a crear. Nombre, tipo, capacidad, ubicación y estado son obligatorios.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo de validación DTO o tipo de bodega inválido)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de bodega ya existe", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Estado o Responsable (si se incluye) no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado (Rol no permitido)", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BodegaDTO> crearBodega(@Valid @RequestBody BodegaDTO bodegaDto) {
        log.info("POST /api/bodegas - Solicitud para crear bodega: {}", bodegaDto.getNombre());
        Bodega bodegaACrear = bodegaMapper.toEntity(bodegaDto);
        Bodega nuevaBodega = bodegaService.crearBodega(bodegaACrear);
        BodegaDTO responseDto = bodegaMapper.toDTO(nuevaBodega);
        log.info("POST /api/bodegas - Bodega '{}' creada con ID: {}", responseDto.getNombre(), nuevaBodega.getBodegaId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_BODEGA')") // Modificado (o GESTIONAR_BODEGAS)
    @Operation(summary = "Actualiza una bodega existente", description = "Modifica los detalles de una bodega (nombre, tipo, capacidad, ubicación, estado, responsable).")
    @Parameter(name = "id", description = "ID de la bodega a actualizar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados de la bodega", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo de validación DTO o tipo de bodega inválido)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Bodega, Estado o Responsable no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de bodega ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BodegaDTO> actualizarBodega(
            @PathVariable Long id,
            @Valid @RequestBody BodegaDTO bodegaDto
    ) {
        log.info("PUT /api/bodegas/{} - Solicitud para actualizar bodega", id);
        Bodega bodegaActualizada = bodegaMapper.toEntity(bodegaDto);
        Bodega bodegaGuardada = bodegaService.actualizarBodega(id, bodegaActualizada);
        BodegaDTO responseDto = bodegaMapper.toDTO(bodegaGuardada);
        log.info("PUT /api/bodegas/{} - Bodega actualizada", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_BODEGA')") // Modificado (o GESTIONAR_BODEGAS)
    @Operation(summary = "Elimina una bodega", description = "Elimina una bodega del sistema. Falla si tiene items asociados.")
    @Parameter(name = "id", description = "ID de la bodega a eliminar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - La bodega contiene ítems y no puede ser eliminada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        log.info("DELETE /api/bodegas/{} - Solicitud para eliminar bodega", id);
        bodegaService.eliminarBodega(id);
        log.info("DELETE /api/bodegas/{} - Bodega eliminada", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales ---

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAuthority('BUSCAR_BODEGAS')") // Modificado (o LEER_BODEGAS)
    @Operation(summary = "Busca bodegas por tipo", description = "Obtiene una lista de bodegas filtradas por su tipo.")
    @Parameter(name = "tipo", description = "Tipo de bodega (MATERIA_PRIMA, PRODUCTO_TERMINADO, TEMPORAL)", required = true, example = "MATERIA_PRIMA", schema = @Schema(implementation = Bodega.TipoBodega.class))
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BodegaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo de bodega inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<BodegaDTO>> buscarPorTipo(@PathVariable Bodega.TipoBodega tipo) {
        log.info("GET /api/bodegas/tipo/{} - Solicitud para buscar bodegas por tipo", tipo);
        List<Bodega> bodegas = bodegaService.buscarBodegasPorTipo(tipo);
        List<BodegaDTO> dtos = bodegas.stream()
                .map(bodegaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/bodegas/tipo/{} - Encontradas {} bodegas", tipo, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/ubicacion")
    @PreAuthorize("hasAuthority('BUSCAR_BODEGAS')") // Modificado (o LEER_BODEGAS)
    @Operation(summary = "Busca bodegas por ubicación", description = "Obtiene bodegas cuya ubicación contiene el texto de búsqueda proporcionado.")
    @Parameter(name = "q", description = "Texto a buscar en la ubicación de la bodega", required = true, example = "Zona Franca")
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BodegaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<BodegaDTO>> buscarPorUbicacion(@RequestParam("q") String textoBusqueda) {
        log.info("GET /api/bodegas/ubicacion?q={} - Solicitud para buscar bodegas por ubicación", textoBusqueda);
        List<Bodega> bodegas = bodegaService.buscarBodegasPorUbicacion(textoBusqueda);
        List<BodegaDTO> dtos = bodegas.stream()
                .map(bodegaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/bodegas/ubicacion?q={} - Encontradas {} bodegas", textoBusqueda, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/capacidad-disponible")
    @PreAuthorize("hasAuthority('BUSCAR_BODEGAS_CAPACIDAD')") // Modificado (permiso específico)
    @Operation(summary = "Lista bodegas con capacidad disponible", description = "Obtiene bodegas que aún tienen espacio de almacenamiento (Capacidad Máxima > Stock Total Items).")
    @ApiResponse(responseCode = "200", description = "Bodegas con capacidad encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BodegaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<BodegaDTO>> buscarConCapacidadDisponible() {
        log.info("GET /api/bodegas/capacidad-disponible - Solicitud para buscar bodegas con capacidad");
        List<Bodega> bodegas = bodegaService.buscarBodegasConCapacidadDisponible();
        List<BodegaDTO> dtos = bodegas.stream()
                .map(bodegaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/bodegas/capacidad-disponible - Encontradas {} bodegas con capacidad", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/responsable/{responsableId}")
    @PreAuthorize("hasAuthority('BUSCAR_BODEGAS')") // Modificado (o LEER_BODEGAS)
    @Operation(summary = "Busca bodegas por responsable", description = "Obtiene una lista de bodegas asignadas a un usuario responsable específico.")
    @Parameter(name = "responsableId", description = "ID del usuario responsable", required = true, example = "10", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BodegaDTO.class))))
    @ApiResponse(responseCode = "404", description = "Usuario responsable no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<BodegaDTO>> buscarPorResponsable(@PathVariable Long responsableId) {
        log.info("GET /api/bodegas/responsable/{} - Solicitud para buscar bodegas por responsable", responsableId);
        List<Bodega> bodegas = bodegaService.buscarBodegasPorResponsable(responsableId);
        List<BodegaDTO> dtos = bodegas.stream()
                .map(bodegaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/bodegas/responsable/{} - Encontradas {} bodegas", responsableId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}