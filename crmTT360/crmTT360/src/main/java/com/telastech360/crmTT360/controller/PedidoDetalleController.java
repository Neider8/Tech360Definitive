// src/main/java/com/telastech360/crmTT360/controller/PedidoDetalleController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.PedidoDetalleDTO;
import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.entity.PedidoDetalle;
import com.telastech360.crmTT360.mapper.PedidoDetalleMapper;
import com.telastech360.crmTT360.service.PedidoDetalleService;
import com.telastech360.crmTT360.service.ItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
 * Controlador REST para gestionar los detalles (ítems) dentro de un Pedido.
 */
@RestController
@RequestMapping("/api/pedidos/detalles")
@Tag(name = "Detalles de Pedidos", description = "Gestión de ítems dentro de un Pedido")
public class PedidoDetalleController {

    private static final Logger log = LoggerFactory.getLogger(PedidoDetalleController.class);

    private final PedidoDetalleService pedidoDetalleService;
    private final PedidoDetalleMapper pedidoDetalleMapper;
    private final ItemService itemService;

    @Autowired
    public PedidoDetalleController(PedidoDetalleService pedidoDetalleService,
                                   PedidoDetalleMapper pedidoDetalleMapper,
                                   ItemService itemService) {
        this.pedidoDetalleService = pedidoDetalleService;
        this.pedidoDetalleMapper = pedidoDetalleMapper;
        this.itemService = itemService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_DETALLES_PEDIDO_TODOS')") // Modificado (permiso muy específico)
    @Operation(summary = "Lista todos los detalles de todos los pedidos", description = "Obtiene una lista completa de todos los ítems de todos los pedidos. Usar con precaución, puede devolver muchos datos.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PedidoDetalleDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PedidoDetalleDTO>> listarTodosLosDetalles() {
        log.info("GET /api/pedidos/detalles - Solicitud para listar todos los detalles");
        List<PedidoDetalle> detalles = pedidoDetalleService.listarTodosLosDetalles();
        List<PedidoDetalleDTO> dtos = detalles.stream()
                .map(pedidoDetalleMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/pedidos/detalles - Devolviendo {} detalles en total", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{pedidoId}/{itemId}")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')") // Modificado
    @Operation(summary = "Obtiene un detalle específico de un pedido", description = "Recupera un ítem específico (cantidad, precio) dentro de un pedido usando el ID del pedido y el ID del ítem.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "itemId", description = "ID del ítem dentro del pedido", required = true, example = "101", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Detalle de pedido encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalleDTO.class)))
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado (o el pedido/ítem no existe)", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDetalleDTO> obtenerDetallePorId(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        log.info("GET /api/pedidos/detalles/{}/{} - Solicitud para obtener detalle", pedidoId, itemId);
        PedidoDetalle detalle = pedidoDetalleService.obtenerDetallePorId(pedidoId, itemId);
        PedidoDetalleDTO dto = pedidoDetalleMapper.toDTO(detalle);
        log.info("GET /api/pedidos/detalles/{}/{} - Detalle encontrado", pedidoId, itemId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/{pedidoId}")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDO')") // Modificado (o AGREGAR_DETALLE_PEDIDO)
    @Operation(summary = "Agrega un ítem a un pedido", description = "Crea y asocia un nuevo ítem (detalle) a un pedido existente.")
    @Parameter(name = "pedidoId", description = "ID del pedido al que se agregará el ítem", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos del detalle a agregar (itemId, cantidad, precioUnitario)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalleDTO.class)))
    @ApiResponse(responseCode = "201", description = "Ítem agregado al pedido exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalleDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (cantidad <= 0, precio <= 0)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Pedido o Ítem no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDetalleDTO> crearDetalle(
            @PathVariable Long pedidoId,
            @Valid @RequestBody PedidoDetalleDTO detalleDto) {
        log.info("POST /api/pedidos/detalles/{} - Solicitud para agregar ítem ID {}", pedidoId, detalleDto.getItemId());
        Item item = itemService.findItemEntityById(detalleDto.getItemId());
        log.debug("Item ID {} encontrado para agregar al pedido {}", detalleDto.getItemId(), pedidoId);
        PedidoDetalle detalleEntidad = pedidoDetalleMapper.toEntity(detalleDto, item);
        PedidoDetalle nuevoDetalle = pedidoDetalleService.agregarDetalle(pedidoId, detalleEntidad);
        PedidoDetalleDTO responseDto = pedidoDetalleMapper.toDTO(nuevoDetalle);
        log.info("POST /api/pedidos/detalles/{} - Ítem ID {} agregado exitosamente", pedidoId, detalleDto.getItemId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{pedidoId}/{itemId}")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDO')") // Modificado (o EDITAR_DETALLE_PEDIDO)
    @Operation(summary = "Actualiza un detalle de pedido", description = "Modifica la cantidad y/o el precio unitario de un ítem existente dentro de un pedido.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "itemId", description = "ID del ítem a actualizar", required = true, example = "101", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados del detalle (cantidad y/o precioUnitario)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalleDTO.class)))
    @ApiResponse(responseCode = "200", description = "Detalle de pedido actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDetalleDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (cantidad <= 0, precio <= 0)", content = @Content)
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDetalleDTO> actualizarDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @Valid @RequestBody PedidoDetalleDTO detalleDto) {
        log.info("PUT /api/pedidos/detalles/{}/{} - Solicitud para actualizar detalle", pedidoId, itemId);
        // El servicio necesita la entidad temporal para extraer campos. Podría optimizarse pasando campos directamente.
        Item tempItem = new Item(); // Crear instancia base
        tempItem.setItemId(itemId); // Establecer ID para referencia si es necesario en toEntity
        PedidoDetalle detalleTemporal = pedidoDetalleMapper.toEntity(detalleDto, tempItem);
        PedidoDetalle detalleActualizado = pedidoDetalleService.actualizarDetalle(pedidoId, itemId, detalleTemporal);

        PedidoDetalleDTO responseDto = pedidoDetalleMapper.toDTO(detalleActualizado);
        log.info("PUT /api/pedidos/detalles/{}/{} - Detalle actualizado", pedidoId, itemId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @DeleteMapping("/{pedidoId}/{itemId}")
    @PreAuthorize("hasAuthority('ELIMINAR_DETALLE_PEDIDO')") // Modificado (o EDITAR_PEDIDO)
    @Operation(summary = "Elimina un ítem de un pedido", description = "Elimina un detalle (ítem) específico de un pedido.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "itemId", description = "ID del ítem a eliminar del pedido", required = true, example = "101", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Detalle de pedido eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        log.info("DELETE /api/pedidos/detalles/{}/{} - Solicitud para eliminar detalle", pedidoId, itemId);
        pedidoDetalleService.eliminarDetalle(pedidoId, itemId);
        log.info("DELETE /api/pedidos/detalles/{}/{} - Detalle eliminado", pedidoId, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')") // Modificado
    @Operation(summary = "Lista detalles de un pedido específico", description = "Obtiene la lista de todos los ítems (detalles) asociados a un pedido particular.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Detalles del pedido encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PedidoDetalleDTO.class))))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PedidoDetalleDTO>> listarDetallesPorPedido(@PathVariable Long pedidoId) {
        log.info("GET /api/pedidos/detalles/pedido/{} - Solicitud para listar detalles", pedidoId);
        List<PedidoDetalle> detalles = pedidoDetalleService.listarDetallesPorPedido(pedidoId);
        List<PedidoDetalleDTO> dtos = detalles.stream()
                .map(pedidoDetalleMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/pedidos/detalles/pedido/{} - Encontrados {} detalles", pedidoId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{pedidoId}/{itemId}/subtotal")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')") // Modificado
    @Operation(summary = "Calcula subtotal de un detalle", description = "Calcula el subtotal (cantidad * precio unitario) para un ítem específico dentro de un pedido.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @Parameter(name = "itemId", description = "ID del ítem", required = true, example = "101", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Subtotal calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class)))
    @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BigDecimal> calcularSubtotalDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        log.info("GET /api/pedidos/detalles/{}/{}/subtotal - Calculando subtotal", pedidoId, itemId);
        BigDecimal subtotal = pedidoDetalleService.calcularSubtotalDetalle(pedidoId, itemId);
        log.info("GET /api/pedidos/detalles/{}/{}/subtotal - Subtotal: {}", pedidoId, itemId, subtotal);
        return new ResponseEntity<>(subtotal, HttpStatus.OK);
    }

    @GetMapping("/pedido/{pedidoId}/total")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')") // Modificado
    @Operation(summary = "Calcula total de un pedido", description = "Calcula y devuelve el monto total de un pedido sumando los subtotales de todos sus ítems.")
    @Parameter(name = "pedidoId", description = "ID del pedido", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Total del pedido calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BigDecimal> calcularTotalPedido(@PathVariable Long pedidoId) {
        log.info("GET /api/pedidos/detalles/pedido/{}/total - Calculando total del pedido", pedidoId);
        BigDecimal total = pedidoDetalleService.calcularTotalPedido(pedidoId);
        log.info("GET /api/pedidos/detalles/pedido/{}/total - Total calculado: {}", pedidoId, total);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}