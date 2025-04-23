package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Importación necesaria para trabajar con BigDecimal
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
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de Pedidos en el sistema") // Anotación Tag
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ========== ENDPOINTS CRUD ========== //

    /**
     * Listar todos los pedidos.
     *
     * @return Lista de pedidos.
     */
    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar pedidos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los pedidos", description = "Obtiene una lista de todos los pedidos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Pedido>> listarTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.listarTodosLosPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener un pedido por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un pedido por ID", description = "Recupera los detalles de un pedido específico usando su ID.")
    @Parameter(name = "id", description = "ID del pedido a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Pedido encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }

    /**
     * Crear un nuevo pedido.
     *
     * @param pedido Datos del pedido a crear.
     * @return Pedido creado.
     */
    @PostMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO crear pedidos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Crea un nuevo pedido", description = "Registra un nuevo pedido en el sistema.")
    @RequestBody(description = "Datos del pedido a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.crearPedido(pedido);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Actualiza un pedido existente", description = "Modifica los detalles de un pedido usando su ID.")
    @Parameter(name = "id", description = "ID del pedido a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del pedido", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Pedido> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoActualizado) {
        Pedido pedido = pedidoService.actualizarPedido(id, pedidoActualizado);
        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE eliminar pedidos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Elimina un pedido", description = "Elimina un pedido del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del pedido a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //

    /**
     * Buscar pedidos por cliente.
     *
     * @param clienteId ID del cliente.
     * @return Lista de pedidos filtrados.
     */
    @GetMapping("/cliente/{clienteId}")
    // Ejemplo: Permitir a todos los roles operativos buscar pedidos por cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca pedidos por cliente", description = "Obtiene una lista de pedidos asociados a un cliente específico.")
    @Parameter(name = "clienteId", description = "ID del cliente", required = true, example = "10")
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe la respuesta
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado (si aplica en tu servicio)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Pedido>> buscarPedidosPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    /**
     * Buscar pedidos por estado.
     *
     * @param estadoValor Valor del estado.
     * @return Lista de pedidos filtrados.
     */
    @GetMapping("/estado/{estadoValor}")
    // Ejemplo: Permitir a todos los roles operativos buscar pedidos por estado
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca pedidos por estado", description = "Obtiene una lista de pedidos filtrados por el valor de su estado.")
    @Parameter(name = "estadoValor", description = "Valor del estado (ej: 'Pendiente')", required = true, example = "Pendiente")
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class))) // Describe la respuesta
    @ApiResponse(responseCode = "400", description = "Valor de estado inválido (si tu servicio valida esto)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Pedido>> buscarPedidosPorEstado(@PathVariable String estadoValor) {
        List<Pedido> pedidos = pedidoService.buscarPedidosPorEstado(estadoValor);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }


    @GetMapping("/{id}/total")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO calcular el total de un pedido
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Calcula el total de un pedido", description = "Calcula el monto total de un pedido específico.")
    @Parameter(name = "id", description = "ID del pedido para calcular el total", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Total del pedido calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Double.class))) // Describe la respuesta (un Double)
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Double> calcularTotalPedido(@PathVariable Long id) {
        // Calcula el total del pedido usando BigDecimal para precisión
        BigDecimal total = pedidoService.calcularTotalPedido(id);

        // Convierte el resultado a double antes de devolverlo
        return new ResponseEntity<>(total.doubleValue(), HttpStatus.OK);
    }
}