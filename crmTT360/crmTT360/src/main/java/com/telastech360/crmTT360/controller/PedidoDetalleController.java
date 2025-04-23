package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.service.PedidoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
@RequestMapping("/api/pedidos/detalles")
@Tag(name = "Detalles de Pedidos", description = "Gestión de los detalles de los ítems dentro de un Pedido") // Anotación Tag
public class PedidoDetalleController {

    private final PedidoDetalleService pedidoDetalleService;

    @Autowired
    public PedidoDetalleController(PedidoDetalleService pedidoDetalleService) {
        this.pedidoDetalleService = pedidoDetalleService;
    }

    // ========== ENDPOINTS CRUD ========== //

    /**
     * Listar todos los detalles de pedidos.
     *
     * @return Lista de detalles de pedidos.
     */
    @GetMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO listar todos los detalles de pedidos (quizás para auditoría)
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Lista todos los detalles de pedidos", description = "Obtiene una lista de todos los detalles de ítems para todos los pedidos.")
    @ApiResponse(responseCode = "200", description = "Lista de detalles de pedidos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<PedidoDetalle>> listarTodosLosDetalles() {
        List<PedidoDetalle> detalles = pedidoDetalleService.listarTodosLosDetalles();
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }


    @GetMapping("/{pedidoId}/{itemId}")
    // Ejemplo: Permitir a todos los roles operativos obtener un detalle específico
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un detalle de pedido por ID compuesto", description = "Recupera un detalle de ítem específico usando el ID del pedido y el ID del ítem.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1") // Describe parámetro
    @Parameter(name = "itemId", description = "ID del ítem", required = true, example = "101") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Detalle de pedido encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<PedidoDetalle> obtenerDetallePorId(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        PedidoDetalle detalle = pedidoDetalleService.obtenerDetallePorId(pedidoId, itemId);
        return new ResponseEntity<>(detalle, HttpStatus.OK);
    }


    @PostMapping("/{pedidoId}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO crear un detalle para un pedido
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Agrega un detalle a un pedido", description = "Crea un nuevo detalle (ítem con cantidad, precio, etc.) y lo asocia a un pedido existente.")
    @Parameter(name = "pedidoId", description = "ID del pedido al que se agregará el detalle", required = true, example = "1")
    @RequestBody(description = "Datos del detalle de pedido a agregar", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "201", description = "Detalle de pedido creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Pedido o ítem no encontrado (si aplica en tu servicio)") // Si el pedido o item no existen
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<PedidoDetalle> crearDetalle(
            @PathVariable Long pedidoId,
            @RequestBody PedidoDetalle detalle) {
        PedidoDetalle nuevoDetalle = pedidoDetalleService.agregarDetalle(pedidoId, detalle);
        return new ResponseEntity<>(nuevoDetalle, HttpStatus.CREATED);
    }

    @PutMapping("/{pedidoId}/{itemId}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO actualizar un detalle existente
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Actualiza un detalle de pedido", description = "Modifica los detalles de un ítem específico dentro de un pedido.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1")
    @Parameter(name = "itemId", description = "ID del ítem dentro del pedido", required = true, example = "101")
    @RequestBody(description = "Datos actualizados del detalle de pedido", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Detalle de pedido actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<PedidoDetalle> actualizarDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody PedidoDetalle detalleActualizado) {
        PedidoDetalle detalle = pedidoDetalleService.actualizarDetalle(pedidoId, itemId, detalleActualizado);
        return new ResponseEntity<>(detalle, HttpStatus.OK);
    }


    @DeleteMapping("/{pedidoId}/{itemId}")
    // Ejemplo: Permitir a ADMIN y GERENTE eliminar un detalle (acción más restrictiva)
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Elimina un detalle de pedido", description = "Elimina un ítem específico de un pedido usando el ID del pedido y el ID del ítem.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1")
    @Parameter(name = "itemId", description = "ID del ítem dentro del pedido", required = true, example = "101")
    @ApiResponse(responseCode = "204", description = "Detalle de pedido eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        pedidoDetalleService.eliminarDetalle(pedidoId, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //


    @GetMapping("/pedido/{pedidoId}")
    // Ejemplo: Permitir a todos los roles operativos listar detalles por pedido
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista detalles de pedido por ID de pedido", description = "Obtiene una lista de todos los detalles de ítems asociados a un pedido específico.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Detalles de pedido encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalle.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado (si aplica en tu servicio)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<PedidoDetalle>> listarDetallesPorPedido(@PathVariable Long pedidoId) {
        List<PedidoDetalle> detalles = pedidoDetalleService.listarDetallesPorPedido(pedidoId);
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }


    @GetMapping("/{pedidoId}/{itemId}/subtotal")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO calcular el subtotal
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Calcula subtotal de un detalle de pedido", description = "Calcula el subtotal (cantidad * precio) de un detalle de ítem dentro de un pedido.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1")
    @Parameter(name = "itemId", description = "ID del ítem", required = true, example = "101")
    @ApiResponse(responseCode = "200", description = "Subtotal calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class))) // Describe la respuesta (BigDecimal)
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<BigDecimal> calcularSubtotalDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        BigDecimal subtotal = pedidoDetalleService.calcularSubtotalDetalle(pedidoId, itemId);
        return new ResponseEntity<>(subtotal, HttpStatus.OK);
    }

    @GetMapping("/pedido/{pedidoId}/total")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO calcular el total del pedido
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Calcula total de un pedido", description = "Calcula el monto total de un pedido sumando los subtotales de todos sus detalles.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Total del pedido calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class))) // Describe la respuesta (BigDecimal)
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<BigDecimal> calcularTotalPedido(@PathVariable Long pedidoId) {
        BigDecimal total = pedidoDetalleService.calcularTotalPedido(pedidoId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}