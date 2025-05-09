// src/main/java/com/telastech360/crmTT360/controller/CategoriaController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.CategoriaDTO;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.mapper.CategoriaMapper;
import com.telastech360.crmTT360.service.CategoriaService;
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
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con las Categorías de ítems.
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de Categorías de ítems")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    @Autowired
    public CategoriaController(CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_CATEGORIAS')") // Modificado
    @Operation(summary = "Lista todas las categorías", description = "Obtiene una lista de todas las categorías de ítems disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoriaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<CategoriaDTO>> listarTodasLasCategorias() {
        log.info("GET /api/categorias - Solicitud para listar todas las categorías");
        List<Categoria> categorias = categoriaService.listarTodasLasCategorias();
        List<CategoriaDTO> dtos = categorias.stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/categorias - Devolviendo {} categorías", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_CATEGORIAS')") // Modificado
    @Operation(summary = "Obtiene una categoría por ID", description = "Recupera los detalles de una categoría específica usando su ID.")
    @Parameter(name = "id", description = "ID único de la categoría", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Categoría encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        log.info("GET /api/categorias/{} - Solicitud para obtener categoría por ID", id);
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        CategoriaDTO dto = categoriaMapper.toDTO(categoria);
        log.info("GET /api/categorias/{} - Categoría encontrada: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_CATEGORIA')") // Modificado (o GESTIONAR_CATEGORIAS)
    @Operation(summary = "Crea una nueva categoría", description = "Registra una nueva categoría para clasificar ítems.")
    @RequestBody(description = "Datos de la categoría a crear (nombre es obligatorio)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. nombre vacío)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de categoría ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDto) {
        log.info("POST /api/categorias - Solicitud para crear categoría: {}", categoriaDto.getNombre());
        Categoria categoriaACrear = categoriaMapper.toEntity(categoriaDto);
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoriaACrear);
        CategoriaDTO responseDto = categoriaMapper.toDTO(nuevaCategoria);
        log.info("POST /api/categorias - Categoría '{}' creada con ID: {}", responseDto.getNombre(), nuevaCategoria.getCategoriaId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_CATEGORIA')") // Modificado (o GESTIONAR_CATEGORIAS)
    @Operation(summary = "Actualiza una categoría existente", description = "Modifica el nombre y/o la descripción de una categoría.")
    @Parameter(name = "id", description = "ID de la categoría a actualizar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados de la categoría", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Nombre de categoría ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<CategoriaDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDto
    ) {
        log.info("PUT /api/categorias/{} - Solicitud para actualizar categoría", id);
        Categoria categoriaActualizada = categoriaMapper.toEntity(categoriaDto);
        Categoria categoriaGuardada = categoriaService.actualizarCategoria(id, categoriaActualizada);
        CategoriaDTO responseDto = categoriaMapper.toDTO(categoriaGuardada);
        log.info("PUT /api/categorias/{} - Categoría actualizada", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_CATEGORIA')") // Modificado (o GESTIONAR_CATEGORIAS)
    @Operation(summary = "Elimina una categoría", description = "Elimina una categoría del sistema. Falla si tiene ítems asociados.")
    @Parameter(name = "id", description = "ID de la categoría a eliminar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - La categoría tiene ítems asociados y no puede ser eliminada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        log.info("DELETE /api/categorias/{} - Solicitud para eliminar categoría", id);
        categoriaService.eliminarCategoria(id);
        log.info("DELETE /api/categorias/{} - Categoría eliminada", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}