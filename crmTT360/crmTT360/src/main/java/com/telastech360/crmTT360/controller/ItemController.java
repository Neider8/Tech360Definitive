package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ItemDTO; // Importar ItemDTO
import com.telastech360.crmTT360.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // Importar @Valid
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


// Controlador REST para la gestión de Ítems.
@RestController
@RequestMapping("/api/items")
@Tag(name = "Ítems", description = "Gestión de Ítems (Materia Prima y Productos) en el sistema") // Anotación Tag
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // ========== ENDPOINTS CRUD con DTOs ========== //

    /**
     * Endpoint para listar todos los ítems.
     * Responde a GET /api/items
     * @return Lista de ItemDTOs.
     */
    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar ítems
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los ítems", description = "Obtiene una lista de todos los ítems (materia prima y productos) registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de ítems obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ItemDTO>> listarTodosLosItems() {
        // El servicio ya devuelve simplemente retornarlos
        List<ItemDTO> items = itemService.listarTodosLosItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener un ítem por su ID.
     * Responde a GET /api/items/{id}
     * @param id ID del ítem a obtener.
     * @return ItemDTO correspondiente al ID.
     */
    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener un ítem por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un ítem por ID", description = "Recupera los detalles de un ítem específico usando su ID.")
    @Parameter(name = "id", description = "ID del ítem a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Ítem encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Ítem no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ItemDTO> obtenerItemPorId(@PathVariable Long id) {
        // El servicio ya devuelve un DTO, simplemente retornarlo
        ItemDTO item = itemService.obtenerItemPorId(id);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo ítem.
     * Responde a POST /api/items
     * @param itemDTO DTO con los datos del nuevo ítem.
     * @return ItemDTO del ítem creado.
     */
    @PostMapping
    // Ejemplo: Permitir a ADMIN y OPERARIO crear ítems (materias primas o productos)
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERARIO')")
    @Operation(summary = "Crea un nuevo ítem", description = "Registra un nuevo ítem (materia prima o producto) en el sistema.")
    @RequestBody(description = "Datos del ítem a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class))) // Describe cuerpo solicitud
    @ApiResponse(responseCode = "201", description = "Ítem creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ItemDTO> crearItem(@Valid @RequestBody ItemDTO itemDTO) {
        // Recibe un DTO, lo pasa al servicio y retorna el DTO resultante
        ItemDTO nuevoItem = itemService.crearItem(itemDTO);
        return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un ítem existente.
     * Responde a PUT /api/items/{id}
     * @param id ID del ítem a actualizar.
     * @param itemDTO DTO con los datos actualizados del ítem.
     * @return ItemDTO del ítem actualizado.
     */
    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y OPERARIO actualizar ítems
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERARIO')")
    @Operation(summary = "Actualiza un ítem existente", description = "Modifica los detalles de un ítem usando su ID.")
    @Parameter(name = "id", description = "ID del ítem a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del ítem", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "200", description = "Ítem actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ItemDTO.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ItemDTO> actualizarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO itemDTO
    ) {
        // Recibe un DTO, lo pasa al servicio junto con el ID y retorna el DTO resultante
        ItemDTO itemActualizado = itemService.actualizarItem(id, itemDTO);
        return new ResponseEntity<>(itemActualizado, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un ítem por su ID.
     * Responde a DELETE /api/items/{id}
     * @param id ID del ítem a eliminar.
     * @return Respuesta vacía con estado NO_CONTENT.
     */
    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar ítems
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un ítem", description = "Elimina un ítem del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del ítem a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Ítem eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        // Llama al servicio para eliminar y retorna una respuesta vacía
        itemService.eliminarItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Puedes agregar endpoints adicionales para los métodos especializados del servicio si es necesario
    // @GetMapping("/buscarPorNombre")
    // public ResponseEntity<List<Item>> buscarPorNombre(@RequestParam String nombre)
    //     List<Item> items = itemService.buscarItemsPorNombre(nombre);
    //     return new ResponseEntity<>(items, HttpStatus.OK);
    // }
    // ... otros endpoints especializados, aplicando @Operation, @Parameter, @ApiResponse
}