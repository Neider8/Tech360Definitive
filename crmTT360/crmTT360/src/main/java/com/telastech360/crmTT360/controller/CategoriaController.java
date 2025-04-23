package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.CategoriaDTO;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.mapper.CategoriaMapper;
import com.telastech360.crmTT360.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de Categorías de ítems en el sistema") // Anotación Tag para el controlador
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper; // Inyectar el mapper si se usa directamente

    @Autowired
    public CategoriaController(CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper; // Inyectar el mapper
    }

    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos (ADMIN, GERENTE, OPERARIO, CAJERO) listar categorías
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Lista todas las categorías", description = "Obtiene una lista de todas las categorías de ítems registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Categoria>> listarTodasLasCategorias() {
        List<Categoria> categorias = categoriaService.listarTodasLasCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener detalles de una categoría
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Obtiene una categoría por ID", description = "Recupera los detalles de una categoría específica usando su ID.")
    @Parameter(name = "id", description = "ID de la categoría a obtener", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id); // Asumo que el servicio lanza 404
        return new ResponseEntity<>(categoria, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Permitir a ADMIN y GERENTE crear nuevas categorías
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crea una nueva categoría", description = "Registra una nueva categoría de ítem en el sistema.")
    @RequestBody(description = "Datos de la categoría a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Categoria> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoriaMapper.toEntity(categoriaDTO)); // Usar mapper
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE actualizar categorías
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualiza una categoría existente", description = "Modifica los detalles de una categoría usando su ID.")
    @Parameter(name = "id", description = "ID de la categoría a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados de la categoría", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Categoria> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO
    ) {
        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaMapper.toEntity(categoriaDTO)); // Usar mapper
        return new ResponseEntity<>(categoriaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar categorías
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina una categoría", description = "Elimina una categoría del sistema usando su ID.")
    @Parameter(name = "id", description = "ID de la categoría a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Aquí podrías agregar más endpoints adicionales si es necesario,
    // y aplicarles la anotación @Operation, @Parameter, @ApiResponse correspondiente.
}