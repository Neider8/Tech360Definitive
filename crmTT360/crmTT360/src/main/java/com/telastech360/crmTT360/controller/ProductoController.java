// src/main/java/com/telastech360/crmTT360/controller/ProductoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ProductoDTO;
import com.telastech360.crmTT360.entity.Producto;
import com.telastech360.crmTT360.mapper.ProductoMapper;
import com.telastech360.crmTT360.service.ProductoService;
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
 * Controlador REST para gestionar las operaciones CRUD y consultas específicas de Productos Terminados.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos Terminados", description = "Gestión de Productos Terminados (Prendas)")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;

    @Autowired
    public ProductoController(ProductoService productoService, ProductoMapper productoMapper) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_PRODUCTOS')") // Modificado
    @Operation(summary = "Lista todos los productos terminados", description = "Obtiene una lista completa de todos los productos terminados disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProductoDTO>> listarTodosLosProductos() {
        log.info("GET /api/productos - Solicitud para listar todos los productos");
        List<Producto> productos = productoService.listarTodosLosProductos();
        List<ProductoDTO> dtos = productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/productos - Devolviendo {} productos", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_PRODUCTOS')") // Modificado
    @Operation(summary = "Obtiene un producto por ID", description = "Recupera los detalles de un producto terminado específico usando su ID de ítem.")
    @Parameter(name = "id", description = "ID único del producto (ID del ítem)", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        log.info("GET /api/productos/{} - Solicitud para obtener producto por ID", id);
        Producto producto = productoService.obtenerProductoPorId(id);
        ProductoDTO dto = productoMapper.toDTO(producto);
        log.info("GET /api/productos/{} - Producto encontrado: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_PRODUCTO')") // Modificado (o GESTIONAR_PRODUCTOS)
    @Operation(summary = "Crea un nuevo producto terminado", description = "Registra una nueva prenda (producto terminado) en el inventario.")
    @RequestBody(description = "Datos del producto a crear. Incluye campos comunes de ítem y específicos como tipo de prenda, talla, color.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (DTO, tipo/talla inválido)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Alguna relación (Bodega, Cat, etc.) no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDto) {
        log.info("POST /api/productos - Solicitud para crear producto: {}", productoDto.getCodigo());
        Producto nuevoProducto = productoService.crearProducto(productoDto);
        ProductoDTO responseDto = productoMapper.toDTO(nuevoProducto);
        log.info("POST /api/productos - Producto '{}' creado con ID: {}", responseDto.getNombre(), responseDto.getItemId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_PRODUCTO')") // Modificado (o GESTIONAR_PRODUCTOS)
    @Operation(summary = "Actualiza un producto terminado existente", description = "Modifica los detalles de un producto terminado.")
    @Parameter(name = "id", description = "ID del producto (ítem) a actualizar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados del producto", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Producto o relación no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código de ítem ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDto
    ) {
        log.info("PUT /api/productos/{} - Solicitud para actualizar producto", id);
        Producto productoGuardado = productoService.actualizarProducto(id, productoDto);
        ProductoDTO responseDto = productoMapper.toDTO(productoGuardado);
        log.info("PUT /api/productos/{} - Producto actualizado", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_PRODUCTO')") // Modificado
    @Operation(summary = "Elimina un producto terminado", description = "Elimina un producto terminado del sistema. Falla si está en pedidos activos.")
    @Parameter(name = "id", description = "ID del producto (ítem) a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El producto está en uso en pedidos activos", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("DELETE /api/productos/{} - Solicitud para eliminar producto", id);
        productoService.eliminarProducto(id);
        log.info("DELETE /api/productos/{} - Producto eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales (usar 'BUSCAR_PRODUCTOS' o 'LEER_PRODUCTOS') ---

    @GetMapping("/tipo-prenda/{tipoPrenda}")
    @PreAuthorize("hasAuthority('BUSCAR_PRODUCTOS')") // Modificado (o LEER_PRODUCTOS)
    @Operation(summary = "Busca productos por tipo de prenda", description = "Obtiene una lista de productos terminados filtrados por su tipo de prenda.")
    @Parameter(name = "tipoPrenda", description = "Tipo de prenda (CAMISA, PANTALON, VESTIDO, CHAQUETA, OTROS)", required = true, example = "CAMISA", schema = @Schema(implementation = Producto.TipoPrenda.class))
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo de prenda inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProductoDTO>> buscarPorTipoPrenda(@PathVariable Producto.TipoPrenda tipoPrenda) {
        log.info("GET /api/productos/tipo-prenda/{} - Buscando por tipo de prenda", tipoPrenda);
        List<Producto> productos = productoService.buscarPorTipoPrenda(tipoPrenda);
        List<ProductoDTO> dtos = productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/productos/tipo-prenda/{} - Encontrados {} productos", tipoPrenda, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/talla/{talla}")
    @PreAuthorize("hasAuthority('BUSCAR_PRODUCTOS')") // Modificado (o LEER_PRODUCTOS)
    @Operation(summary = "Busca productos por talla", description = "Obtiene una lista de productos terminados filtrados por su talla.")
    @Parameter(name = "talla", description = "Talla del producto (XS, S, M, L, XL, XXL, UNICA)", required = true, example = "M", schema = @Schema(implementation = Producto.Talla.class))
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    @ApiResponse(responseCode = "400", description = "Talla inválida", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProductoDTO>> buscarPorTalla(@PathVariable Producto.Talla talla) {
        log.info("GET /api/productos/talla/{} - Buscando por talla", talla);
        List<Producto> productos = productoService.buscarPorTalla(talla);
        List<ProductoDTO> dtos = productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/productos/talla/{} - Encontrados {} productos", talla, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/color/{color}")
    @PreAuthorize("hasAuthority('BUSCAR_PRODUCTOS')") // Modificado (o LEER_PRODUCTOS)
    @Operation(summary = "Busca productos por color", description = "Obtiene una lista de productos terminados cuyo color contiene el texto de búsqueda (case-insensitive).")
    @Parameter(name = "color", description = "Texto a buscar en el color del producto", required = true, example = "Rojo")
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ProductoDTO>> buscarPorColor(@PathVariable String color) {
        log.info("GET /api/productos/color/{} - Buscando por color", color);
        List<Producto> productos = productoService.buscarPorColor(color);
        List<ProductoDTO> dtos = productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/productos/color/{} - Encontrados {} productos", color, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}