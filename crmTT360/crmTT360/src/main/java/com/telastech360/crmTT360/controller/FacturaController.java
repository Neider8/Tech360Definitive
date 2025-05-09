// src/main/java/com/telastech360/crmTT360/controller/FacturaController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.mapper.FacturaMapper;
import com.telastech360.crmTT360.service.FacturaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegurar importación
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
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con las Facturas.
 */
@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Gestión de Facturas de Venta y Compra")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService facturaService;
    private final FacturaMapper facturaMapper;

    @Autowired
    public FacturaController(FacturaService facturaService, FacturaMapper facturaMapper) {
        this.facturaService = facturaService;
        this.facturaMapper = facturaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_FACTURAS')") // Modificado
    @Operation(summary = "Lista todas las facturas", description = "Obtiene una lista completa de todas las facturas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FacturaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<FacturaDTO>> listarTodasLasFacturas() {
        log.info("GET /api/facturas - Solicitud para listar todas las facturas");
        List<FacturaDTO> facturas = facturaService.listarTodasLasFacturas();
        log.info("GET /api/facturas - Devolviendo {} facturas", facturas.size());
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_FACTURAS')") // Modificado
    @Operation(summary = "Obtiene una factura por ID", description = "Recupera los detalles de una factura específica usando su ID único.")
    @Parameter(name = "id", description = "ID único de la factura", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Factura encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<FacturaDTO> obtenerFacturaPorId(@PathVariable Long id) {
        log.info("GET /api/facturas/{} - Solicitud para obtener factura por ID", id);
        FacturaDTO factura = facturaService.obtenerFacturaPorId(id);
        log.info("GET /api/facturas/{} - Factura encontrada", id);
        return new ResponseEntity<>(factura, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_FACTURA')") // Modificado
    @Operation(summary = "Crea una nueva factura", description = "Registra una nueva factura asociada a un pedido existente.")
    @RequestBody(description = "Datos de la factura a crear. Se debe incluir ID del pedido, tipo, total y fecha.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "201", description = "Factura creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. falta pedidoId, fallo de validación)", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Pedido asociado no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<FacturaDTO> crearFactura(@Valid @RequestBody FacturaDTO facturaDTO) {
        log.info("POST /api/facturas - Solicitud para crear factura para pedido ID {}", facturaDTO.getPedidoId());
        FacturaDTO nuevaFactura = facturaService.crearFactura(facturaDTO);
        log.info("POST /api/facturas - Factura creada con ID: {}", nuevaFactura.getFacturaId());
        return new ResponseEntity<>(nuevaFactura, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_FACTURA')") // Modificado
    @Operation(summary = "Actualiza una factura existente", description = "Modifica los detalles de una factura (tipo, total, fecha, estado de pago). No cambia el pedido asociado.")
    @Parameter(name = "id", description = "ID de la factura a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @RequestBody(description = "Datos actualizados de la factura", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Factura actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<FacturaDTO> actualizarFactura(
            @PathVariable Long id,
            @Valid @RequestBody FacturaDTO facturaDTO
    ) {
        log.info("PUT /api/facturas/{} - Solicitud para actualizar factura", id);
        FacturaDTO facturaActualizada = facturaService.actualizarFactura(id, facturaDTO);
        log.info("PUT /api/facturas/{} - Factura actualizada", id);
        return new ResponseEntity<>(facturaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_FACTURA')") // Modificado
    @Operation(summary = "Elimina una factura", description = "Elimina una factura del sistema permanentemente.")
    @Parameter(name = "id", description = "ID de la factura a eliminar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Factura eliminada exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        log.info("DELETE /api/facturas/{} - Solicitud para eliminar factura", id);
        facturaService.eliminarFactura(id);
        log.info("DELETE /api/facturas/{} - Factura eliminada", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales ---

    @GetMapping("/tipo-movimiento/{tipoMovimiento}")
    @PreAuthorize("hasAuthority('BUSCAR_FACTURAS')") // Modificado (o LEER_FACTURAS)
    @Operation(summary = "Busca facturas por tipo de movimiento", description = "Obtiene una lista de facturas filtradas por su tipo (VENTA o COMPRA).")
    @Parameter(name = "tipoMovimiento", description = "Tipo de movimiento (VENTA, COMPRA)", required = true, example = "VENTA", schema = @Schema(implementation = Factura.TipoMovimiento.class))
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FacturaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo de movimiento inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<FacturaDTO>> buscarPorTipoMovimiento(@PathVariable Factura.TipoMovimiento tipoMovimiento) {
        log.info("GET /api/facturas/tipo-movimiento/{} - Buscando por tipo", tipoMovimiento);
        List<Factura> facturas = facturaService.buscarPorTipoMovimiento(tipoMovimiento);
        List<FacturaDTO> dtos = facturas.stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/facturas/tipo-movimiento/{} - Encontradas {} facturas", tipoMovimiento, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/rango-fechas")
    @PreAuthorize("hasAuthority('BUSCAR_FACTURAS')") // Modificado (o LEER_FACTURAS)
    @Operation(summary = "Busca facturas por rango de fechas", description = "Obtiene facturas cuya fecha de creación se encuentra dentro del rango especificado.")
    @Parameter(name = "inicio", description = "Fecha/hora de inicio (Timestamp ISO 8601)", required = true, example = "2023-01-01T00:00:00.000Z")
    @Parameter(name = "fin", description = "Fecha/hora de fin (Timestamp ISO 8601)", required = true, example = "2023-12-31T23:59:59.999Z")
    @ApiResponse(responseCode = "200", description = "Facturas encontradas en el rango",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FacturaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Formato de fecha inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<FacturaDTO>> buscarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp fin) {
        log.info("GET /api/facturas/rango-fechas?inicio={}&fin={} - Buscando por rango", inicio, fin);
        List<Factura> facturas = facturaService.buscarPorRangoFechas(inicio, fin);
        List<FacturaDTO> dtos = facturas.stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/facturas/rango-fechas - Encontradas {} facturas", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/pendientes-pago")
    @PreAuthorize("hasAuthority('BUSCAR_FACTURAS_PENDIENTES')") // Modificado (permiso más específico)
    @Operation(summary = "Busca facturas pendientes de pago", description = "Obtiene una lista de facturas que aún no han sido marcadas como pagadas.")
    @ApiResponse(responseCode = "200", description = "Facturas pendientes encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FacturaDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<FacturaDTO>> buscarFacturasPendientesPago() {
        log.info("GET /api/facturas/pendientes-pago - Buscando facturas pendientes");
        List<Factura> facturas = facturaService.buscarFacturasPendientesPago();
        List<FacturaDTO> dtos = facturas.stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/facturas/pendientes-pago - Encontradas {} facturas pendientes", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/total-facturado-por-dia")
    @PreAuthorize("hasAuthority('VER_REPORTES_FACTURACION')") // Modificado (permiso de reportes)
    @Operation(summary = "Calcula el total facturado por día", description = "Agrupa el monto total facturado por día dentro de un rango de fechas.")
    @Parameter(name = "inicio", description = "Fecha/hora de inicio (Timestamp ISO 8601)", required = true, example = "2023-01-01T00:00:00.000Z")
    @Parameter(name = "fin", description = "Fecha/hora de fin (Timestamp ISO 8601)", required = true, example = "2023-12-31T23:59:59.999Z")
    @ApiResponse(responseCode = "200", description = "Total facturado por día calculado exitosamente.",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(type = "array", example = "[\"2023-10-26\", 1500.75]"))))
    @ApiResponse(responseCode = "400", description = "Formato de fecha inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<Object[]>> calcularTotalFacturadoPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp fin) {
        log.info("GET /api/facturas/total-facturado-por-dia?inicio={}&fin={} - Calculando total por día", inicio, fin);
        List<Object[]> resultado = facturaService.calcularTotalFacturadoPorDia(inicio, fin);
        log.info("GET /api/facturas/total-facturado-por-dia - Cálculo completado, {} resultados.", resultado.size());
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping("/total-mayor-a/{montoMinimo}")
    @PreAuthorize("hasAuthority('BUSCAR_FACTURAS')") // Modificado (o LEER_FACTURAS)
    @Operation(summary = "Busca facturas con total mayor a un monto", description = "Obtiene una lista de facturas cuyo monto total supera el valor especificado.")
    @Parameter(name = "montoMinimo", description = "Monto total mínimo a buscar", required = true, example = "1000.00", schema = @Schema(type="number", format="double"))
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FacturaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Monto mínimo inválido (ej. no numérico)", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<FacturaDTO>> buscarFacturasConTotalMayorA(@PathVariable BigDecimal montoMinimo) {
        log.info("GET /api/facturas/total-mayor-a/{} - Buscando facturas", montoMinimo);
        List<Factura> facturas = facturaService.buscarFacturasConTotalMayorA(montoMinimo);
        List<FacturaDTO> dtos = facturas.stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/facturas/total-mayor-a/{} - Encontradas {} facturas", montoMinimo, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}