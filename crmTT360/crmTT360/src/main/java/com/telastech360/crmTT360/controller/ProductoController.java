package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Producto;
import com.telastech360.crmTT360.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de Productos Terminados en el sistema") // Anotación Tag
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ========== ENDPOINTS CRUD ========== //

    /**
     * Listar todos los productos.
     *
     * @return Lista de productos.
     */
    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar productos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los productos", description = "Obtiene una lista de todos los productos terminados registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Producto>> listarTodosLosProductos() {
        List<Producto> productos = productoService.listarTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener un producto por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un producto por ID", description = "Recupera los detalles de un producto específico usando su ID.")
    @Parameter(name = "id", description = "ID del producto a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Producto no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    /**
     * Crear un nuevo producto.
     *
     * @param producto Datos del producto a crear.
     * @return Producto creado.
     */
    @PostMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y OPERARIO crear productos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Crea un nuevo producto", description = "Registra un nuevo producto terminado en el sistema.")
    @RequestBody(description = "Datos del producto a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN, GERENTE y OPERARIO actualizar productos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Actualiza un producto existente", description = "Modifica los detalles de un producto usando su ID.")
    @Parameter(name = "id", description = "ID del producto a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del producto", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        Producto producto = productoService.actualizarProducto(id, productoActualizado);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar productos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un producto", description = "Elimina un producto del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del producto a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //

    /**
     * Buscar productos por tipo de prenda.
     *
     * @param tipoPrenda Tipo de prenda.
     * @return Lista de productos filtrados.
     */
    @GetMapping("/tipo-prenda/{tipoPrenda}")
    // Ejemplo: Permitir a todos los roles operativos buscar por tipo de prenda
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca productos por tipo de prenda", description = "Obtiene una lista de productos filtrados por su tipo de prenda.")
    @Parameter(name = "tipoPrenda", description = "Tipo de prenda (enum)", required = true, example = "CAMISA",
            schema = @Schema(implementation = Producto.TipoPrenda.class)) // Documenta el enum
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe la respuesta
    @ApiResponse(responseCode = "400", description = "Tipo de prenda inválido")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Producto>> buscarPorTipoPrenda(@PathVariable Producto.TipoPrenda tipoPrenda) {
        List<Producto> productos = productoService.buscarPorTipoPrenda(tipoPrenda);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Buscar productos por talla.
     *
     * @param talla Talla del producto.
     * @return Lista de productos filtrados.
     */
    @GetMapping("/talla/{talla}")
    // Ejemplo: Permitir a todos los roles operativos buscar por talla
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca productos por talla", description = "Obtiene una lista de productos filtrados por su talla.")
    @Parameter(name = "talla", description = "Talla del producto (enum)", required = true, example = "M",
            schema = @Schema(implementation = Producto.Talla.class)) // Documenta el enum
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe la respuesta
    @ApiResponse(responseCode = "400", description = "Talla inválida")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Producto>> buscarPorTalla(@PathVariable Producto.Talla talla) {
        List<Producto> productos = productoService.buscarPorTalla(talla);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Buscar productos por color.
     *
     * @param color Color del producto.
     * @return Lista de productos filtrados.
     */
    @GetMapping("/color/{color}")
    // Ejemplo: Permitir a todos los roles operativos buscar por color
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca productos por color", description = "Obtiene una lista de productos filtrados por su color.")
    @Parameter(name = "color", description = "Color del producto", required = true, example = "Rojo")
    @ApiResponse(responseCode = "200", description = "Productos encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Producto>> buscarPorColor(@PathVariable String color) {
        List<Producto> productos = productoService.buscarPorColor(color);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
}