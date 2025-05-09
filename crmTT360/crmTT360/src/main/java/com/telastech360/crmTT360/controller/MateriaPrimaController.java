// src/main/java/com/telastech360/crmTT360/controller/MateriaPrimaController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.MateriaPrimaDTO;
import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.mapper.MateriaPrimaMapper;
import com.telastech360.crmTT360.service.MateriaPrimaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar las operaciones CRUD y consultas específicas de Materias Primas.
 */
@RestController
@RequestMapping("/api/materias-primas")
@Tag(name = "Materia Prima", description = "Gestión de Materia Prima (Telas, Hilos, Botones, etc.)")
public class MateriaPrimaController {

    private static final Logger log = LoggerFactory.getLogger(MateriaPrimaController.class);

    private final MateriaPrimaService materiaPrimaService;
    private final MateriaPrimaMapper materiaPrimaMapper;

    @Autowired
    public MateriaPrimaController(MateriaPrimaService materiaPrimaService, MateriaPrimaMapper materiaPrimaMapper) {
        this.materiaPrimaService = materiaPrimaService;
        this.materiaPrimaMapper = materiaPrimaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_MATERIAS_PRIMAS')") // Modificado
    @Operation(summary = "Lista todas las materias primas", description = "Obtiene una lista completa de todas las materias primas disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de materias primas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MateriaPrimaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<MateriaPrimaDTO>> listarTodasLasMateriasPrimas() {
        log.info("GET /api/materias-primas - Solicitud para listar todas las materias primas");
        List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodasLasMateriasPrimas();
        List<MateriaPrimaDTO> dtos = materiasPrimas.stream()
                .map(materiaPrimaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/materias-primas - Devolviendo {} materias primas", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_MATERIAS_PRIMAS')") // Modificado
    @Operation(summary = "Obtiene una materia prima por ID", description = "Recupera los detalles de una materia prima específica usando su ID de ítem.")
    @Parameter(name = "id", description = "ID único de la materia prima (ID del ítem)", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Materia prima encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrimaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Materia prima no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<MateriaPrimaDTO> obtenerMateriaPrimaPorId(@PathVariable Long id) {
        log.info("GET /api/materias-primas/{} - Solicitud para obtener materia prima por ID", id);
        MateriaPrima materiaPrima = materiaPrimaService.obtenerMateriaPrimaPorId(id);
        MateriaPrimaDTO dto = materiaPrimaMapper.toDTO(materiaPrima);
        log.info("GET /api/materias-primas/{} - Materia prima encontrada: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_MATERIA_PRIMA')") // Modificado (o GESTIONAR_MATERIAS_PRIMAS)
    @Operation(summary = "Crea una nueva materia prima", description = "Registra una nueva materia prima (tela, hilo, botón, etc.) en el inventario.")
    @RequestBody(description = "Datos de la materia prima a crear. Incluye campos comunes de ítem y específicos como tipo de material.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrimaDTO.class)))
    @ApiResponse(responseCode = "201", description = "Materia prima creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrimaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo DTO, tipo material inválido)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Alguna relación (Bodega, Cat, Estado, Prov, Usuario, ProvTela) no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<MateriaPrimaDTO> crearMateriaPrima(@Valid @RequestBody MateriaPrimaDTO materiaPrimaDto) {
        log.info("POST /api/materias-primas - Solicitud para crear materia prima: {}", materiaPrimaDto.getCodigo());
        MateriaPrima nuevaMateriaPrima = materiaPrimaService.crearMateriaPrima(materiaPrimaDto);
        MateriaPrimaDTO responseDto = materiaPrimaMapper.toDTO(nuevaMateriaPrima);
        log.info("POST /api/materias-primas - Materia prima '{}' creada con ID: {}", responseDto.getNombre(), responseDto.getItemId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_MATERIA_PRIMA')") // Modificado (o GESTIONAR_MATERIAS_PRIMAS)
    @Operation(summary = "Actualiza una materia prima existente", description = "Modifica los detalles de una materia prima existente.")
    @Parameter(name = "id", description = "ID de la materia prima (ítem) a actualizar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados de la materia prima", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrimaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Materia prima actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrimaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Materia prima o relación no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<MateriaPrimaDTO> actualizarMateriaPrima(
            @PathVariable Long id,
            @Valid @RequestBody MateriaPrimaDTO materiaPrimaDto
    ) {
        log.info("PUT /api/materias-primas/{} - Solicitud para actualizar materia prima", id);
        MateriaPrima materiaPrimaGuardada = materiaPrimaService.actualizarMateriaPrima(id, materiaPrimaDto);
        MateriaPrimaDTO responseDto = materiaPrimaMapper.toDTO(materiaPrimaGuardada);
        log.info("PUT /api/materias-primas/{} - Materia prima actualizada", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_MATERIA_PRIMA')") // Modificado
    @Operation(summary = "Elimina una materia prima", description = "Elimina una materia prima del sistema. Falla si está en pedidos activos.")
    @Parameter(name = "id", description = "ID de la materia prima (ítem) a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Materia prima eliminada exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Materia prima no encontrada", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - La materia prima está en uso en pedidos activos", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarMateriaPrima(@PathVariable Long id) {
        log.info("DELETE /api/materias-primas/{} - Solicitud para eliminar materia prima", id);
        materiaPrimaService.eliminarMateriaPrima(id);
        log.info("DELETE /api/materias-primas/{} - Materia prima eliminada", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales ---

    @GetMapping("/tipo-material/{tipo}")
    @PreAuthorize("hasAuthority('BUSCAR_MATERIAS_PRIMAS')") // Modificado (o LEER_MATERIAS_PRIMAS)
    @Operation(summary = "Busca materias primas por tipo", description = "Obtiene una lista de materias primas filtradas por su tipo específico.")
    @Parameter(name = "tipo", description = "Tipo de material (TELA, HILO, BOTON, CIERRE, ETIQUETA, OTROS)", required = true, example = "TELA", schema = @Schema(implementation = MateriaPrima.TipoMaterial.class))
    @ApiResponse(responseCode = "200", description = "Materias primas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MateriaPrimaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<MateriaPrimaDTO>> buscarPorTipoMaterial(@PathVariable MateriaPrima.TipoMaterial tipo) {
        log.info("GET /api/materias-primas/tipo-material/{} - Buscando por tipo", tipo);
        List<MateriaPrima> materiasPrimas = materiaPrimaService.buscarPorTipoMaterial(tipo);
        List<MateriaPrimaDTO> dtos = materiasPrimas.stream()
                .map(materiaPrimaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/materias-primas/tipo-material/{} - Encontradas {} materias primas", tipo, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/proveedor/{proveedorId}")
    @PreAuthorize("hasAuthority('BUSCAR_MATERIAS_PRIMAS')") // Modificado (o LEER_MATERIAS_PRIMAS)
    @Operation(summary = "Busca materias primas por proveedor", description = "Obtiene materias primas asociadas a un proveedor (ya sea general o específico de tela).")
    @Parameter(name = "proveedorId", description = "ID del proveedor", required = true, example = "5", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Materias primas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MateriaPrimaDTO.class))))
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<MateriaPrimaDTO>> buscarPorProveedor(@PathVariable Long proveedorId) {
        log.info("GET /api/materias-primas/proveedor/{} - Buscando por proveedor", proveedorId);
        List<MateriaPrima> materiasPrimas = materiaPrimaService.buscarPorProveedor(proveedorId);
        List<MateriaPrimaDTO> dtos = materiasPrimas.stream()
                .map(materiaPrimaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/materias-primas/proveedor/{} - Encontradas {} materias primas", proveedorId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}