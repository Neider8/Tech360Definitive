// src/main/java/com/telastech360/crmTT360/controller/PedidoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.PedidoDTO;
import com.telastech360.crmTT360.entity.ClienteInterno; // <-- Importación añadida
import com.telastech360.crmTT360.entity.Estado;       // <-- Importación añadida
import com.telastech360.crmTT360.entity.Pedido;
import com.telastech360.crmTT360.mapper.PedidoMapper;
import com.telastech360.crmTT360.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con los Pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de Pedidos de Clientes")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    @Autowired
    public PedidoController(PedidoService pedidoService, PedidoMapper pedidoMapper) {
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

    // --- listarTodosLosPedidos, obtenerPedidoPorId, crearPedido (sin cambios respecto a la versión anterior) ---
    @GetMapping
    @PreAuthorize("hasAuthority('LEER_PEDIDO')")
    @Operation(summary = "Lista todos los pedidos", description = "Obtiene una lista completa de todos los pedidos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PedidoDTO>> listarTodosLosPedidos() {
        log.info("GET /api/pedidos - Solicitud para listar todos los pedidos");
        List<Pedido> pedidos = pedidoService.listarTodosLosPedidos();
        List<PedidoDTO> dtos = pedidos.stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/pedidos - Devolviendo {} pedidos", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')")
    @Operation(summary = "Obtiene un pedido por ID", description = "Recupera los detalles completos de un pedido específico, incluyendo sus ítems.")
    @Parameter(name = "id", description = "ID único del pedido", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Pedido encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Solicitud para obtener pedido por ID", id);
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        PedidoDTO dto = pedidoMapper.toDTO(pedido);
        log.info("GET /api/pedidos/{} - Pedido encontrado. Cliente ID: {}, Estado ID: {}, #Detalles: {}", id, dto.getClienteId(), dto.getEstadoId(), dto.getDetalles().size());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_PEDIDO')")
    @Operation(summary = "Crea un nuevo pedido", description = "Registra un nuevo pedido con su cliente, estado inicial y la lista de ítems solicitados (detalles).")
    @RequestBody(description = "Datos del pedido a crear. Debe incluir clienteId, estadoId y una lista no vacía de detalles (con itemId, cantidad y precioUnitario).", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (DTO, lista detalles vacía, cantidad/precio inválido)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Cliente, Estado o algún Ítem no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoDTO pedidoDto) {
        log.info("POST /api/pedidos - Solicitud para crear pedido para cliente ID {}", pedidoDto.getClienteId());
        Pedido pedidoCreado = pedidoService.crearPedidoConDetalles(pedidoDto);
        PedidoDTO responseDto = pedidoMapper.toDTO(pedidoCreado);
        log.info("POST /api/pedidos - Pedido creado con ID: {}", responseDto.getPedidoId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDO')")
    @Operation(summary = "Actualiza información principal de un pedido", description = "Modifica el cliente, estado y/o fecha de fin de un pedido. NO modifica los ítems (detalles).")
    @Parameter(name = "id", description = "ID del pedido a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos actualizados del pedido (solo se consideran clienteId, estadoId; la lista de detalles es ignorada aquí)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDTO.class))) // Reutiliza PedidoDTO
    @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Pedido, Cliente o Estado no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<PedidoDTO> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoDTO pedidoDto // La validación @NotEmpty de detalles no aplica aquí
    ) {
        log.info("PUT /api/pedidos/{} - Solicitud para actualizar información principal del pedido", id);

        // Llamar al servicio pasando el DTO directamente
        Pedido pedidoActualizado = pedidoService.actualizarPedido(id, pedidoDto);

        PedidoDTO responseDto = pedidoMapper.toDTO(pedidoActualizado);
        log.info("PUT /api/pedidos/{} - Información principal del pedido actualizada", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_PEDIDO')")
    @Operation(summary = "Elimina un pedido", description = "Elimina un pedido y todos sus detalles asociados. Falla si el pedido tiene facturas.")
    @Parameter(name = "id", description = "ID del pedido a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El pedido tiene facturas asociadas", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Solicitud para eliminar pedido", id);
        pedidoService.eliminarPedido(id);
        log.info("DELETE /api/pedidos/{} - Pedido eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales (sin cambios respecto a la versión anterior) ---
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')")
    @Operation(summary = "Busca pedidos por cliente", description = "Obtiene una lista de todos los pedidos realizados por un cliente específico.")
    @Parameter(name = "clienteId", description = "ID del cliente", required = true, example = "10", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados para el cliente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class))))
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PedidoDTO>> buscarPedidosPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/pedidos/cliente/{} - Buscando pedidos por cliente", clienteId);
        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        List<PedidoDTO> dtos = pedidos.stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/pedidos/cliente/{} - Encontrados {} pedidos", clienteId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/estado/{estadoValor}")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')")
    @Operation(summary = "Busca pedidos por estado", description = "Obtiene una lista de pedidos que se encuentran en un estado específico (usando el valor del estado).")
    @Parameter(name = "estadoValor", description = "Valor del estado a buscar (ej: Pendiente, Completado, Cancelado). Case-sensitive.", required = true, example = "Pendiente")
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados con el estado especificado",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<PedidoDTO>> buscarPedidosPorEstado(@PathVariable String estadoValor) {
        log.info("GET /api/pedidos/estado/{} - Buscando pedidos por estado", estadoValor);
        List<Pedido> pedidos = pedidoService.buscarPedidosPorEstado(estadoValor);
        List<PedidoDTO> dtos = pedidos.stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/pedidos/estado/{} - Encontrados {} pedidos", estadoValor, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}/total")
    @PreAuthorize("hasAuthority('LEER_PEDIDO')")
    @Operation(summary = "Calcula el total de un pedido", description = "Calcula y devuelve el monto total de un pedido sumando los subtotales de todos sus detalles.")
    @Parameter(name = "id", description = "ID del pedido", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Total calculado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<BigDecimal> calcularTotalPedido(@PathVariable Long id) {
        log.info("GET /api/pedidos/{}/total - Calculando total del pedido", id);
        BigDecimal total = pedidoService.calcularTotalPedido(id);
        log.info("GET /api/pedidos/{}/total - Total calculado: {}", id, total);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}