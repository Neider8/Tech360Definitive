// src/main/java/com/telastech360/crmTT360/controller/ItemController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ItemDTO;
import com.telastech360.crmTT360.service.ItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
 * Controlador REST para gestionar operaciones sobre la entidad base Item.
 */
@RestController
@RequestMapping("/api/items")
@Tag(name = "Ítems (Genérico)", description = "Gestión genérica de Ítems (base para Productos y Materias Primas)")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_ITEMS')") // Modificado (Permiso genérico para items)
    @Operation(summary = "Lista todos los ítems", description = "Obtiene una lista de todos los ítems registrados (tanto productos como materias primas).")
    @ApiResponse(responseCode = "200", description = "Lista de ítems obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ItemDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ItemDTO>> listarTodosLosItems() {
        log.info("GET /api/items - Solicitud para listar todos los ítems");
        List<ItemDTO> items = itemService.listarTodosLosItems();
        log.info("GET /api/items - Devolviendo {} ítems", items.size());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_ITEMS')") // Modificado
    @Operation(summary = "Obtiene un ítem por ID", description = "Recupera los detalles de un ítem específico (Producto o Materia Prima) usando su ID.")
    @Parameter(name = "id", description = "ID único del ítem", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Ítem encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Ítem no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ItemDTO> obtenerItemPorId(@PathVariable Long id) {
        log.info("GET /api/items/{} - Solicitud para obtener ítem por ID", id);
        ItemDTO item = itemService.obtenerItemPorId(id);
        log.info("GET /api/items/{} - Ítem encontrado: {}", id, item.getNombre());
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_ITEM')") // Modificado (O permisos específicos de producto/mp)
    @Operation(summary = "Crea un nuevo ítem (Genérico)", description = "Registra un nuevo ítem base. Preferir usar endpoints de /productos o /materias-primas.")
    @RequestBody(description = "Datos del ítem a crear. Incluir tipo ('PRODUCTO_TERMINADO' o 'MATERIA_PRIMA') y IDs de relaciones.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "201", description = "Ítem creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo de validación DTO o tipo inválido)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Alguna relación (Bodega, Categoría, etc.) no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ItemDTO> crearItem(@Valid @RequestBody ItemDTO itemDTO) {
        log.info("POST /api/items - Solicitud para crear ítem genérico: {}", itemDTO.getCodigo());
        ItemDTO nuevoItem = itemService.crearItem(itemDTO);
        log.info("POST /api/items - Ítem '{}' creado con ID: {}", nuevoItem.getNombre(), nuevoItem.getItemId());
        return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_ITEM')") // Modificado (O permisos específicos de producto/mp)
    @Operation(summary = "Actualiza un ítem existente (Genérico)", description = "Modifica los detalles comunes de un ítem existente. Preferir usar endpoints de /productos o /materias-primas para actualizaciones completas.")
    @Parameter(name = "id", description = "ID del ítem a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos comunes actualizados del ítem", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "200", description = "Ítem actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Ítem o alguna relación no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ItemDTO> actualizarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO itemDTO
    ) {
        log.info("PUT /api/items/{} - Solicitud para actualizar ítem genérico", id);
        ItemDTO itemActualizado = itemService.actualizarItem(id, itemDTO);
        log.info("PUT /api/items/{} - Ítem actualizado", id);
        return new ResponseEntity<>(itemActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_ITEM')") // Modificado (O permisos específicos de producto/mp)
    @Operation(summary = "Elimina un ítem", description = "Elimina un ítem (Producto o Materia Prima) del sistema. Falla si está en pedidos activos.")
    @Parameter(name = "id", description = "ID del ítem a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Ítem eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ítem no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El ítem está en uso en pedidos activos", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        log.info("DELETE /api/items/{} - Solicitud para eliminar ítem", id);
        itemService.eliminarItem(id);
        log.info("DELETE /api/items/{} - Ítem eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}