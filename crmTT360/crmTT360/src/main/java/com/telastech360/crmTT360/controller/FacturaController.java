package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;

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
import io.swagger.v3.oas.annotations.media.ArraySchema; // Importa ArraySchema


@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Gestión de Facturas en el sistema") // Anotación Tag
public class FacturaController {

    private final FacturaService facturaService;

    @Autowired
    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO listar facturas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Lista todas las facturas", description = "Obtiene una lista de todas las facturas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<FacturaDTO>> listarTodasLasFacturas() {
        List<FacturaDTO> facturas = facturaService.listarTodasLasFacturas();
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO obtener una factura por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Obtiene una factura por ID", description = "Recupera los detalles de una factura específica usando su ID.")
    @Parameter(name = "id", description = "ID de la factura a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Factura encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Factura no encontrada") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<FacturaDTO> obtenerFacturaPorId(@PathVariable Long id) {
        FacturaDTO factura = facturaService.obtenerFacturaPorId(id);
        return new ResponseEntity<>(factura, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO crear facturas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Crea una nueva factura", description = "Registra una nueva factura en el sistema.")
    @RequestBody(description = "Datos de la factura a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class))) // Describe cuerpo solicitud
    @ApiResponse(responseCode = "201", description = "Factura creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<FacturaDTO> crearFactura(@Valid @RequestBody FacturaDTO facturaDTO) {
        FacturaDTO nuevaFactura = facturaService.crearFactura(facturaDTO);
        return new ResponseEntity<>(nuevaFactura, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO actualizar facturas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Actualiza una factura existente", description = "Modifica los detalles de una factura usando su ID.")
    @Parameter(name = "id", description = "ID de la factura a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados de la factura", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Factura actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FacturaDTO.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<FacturaDTO> actualizarFactura(
            @PathVariable Long id,
            @Valid @RequestBody FacturaDTO facturaDTO
    ) {
        FacturaDTO facturaActualizada = facturaService.actualizarFactura(id, facturaDTO);
        return new ResponseEntity<>(facturaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE eliminar facturas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Elimina una factura", description = "Elimina una factura del sistema usando su ID.")
    @Parameter(name = "id", description = "ID de la factura a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Factura eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES ========== //

    @GetMapping("/tipo-movimiento/{tipoMovimiento}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar por tipo de movimiento
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca facturas por tipo de movimiento", description = "Obtiene una lista de facturas filtradas por su tipo de movimiento (INGRESO, EGRESO).")
    @Parameter(name = "tipoMovimiento", description = "Tipo de movimiento de la factura (enum)", required = true, example = "INGRESO",
            schema = @Schema(implementation = Factura.TipoMovimiento.class)) // Documenta el enum como parámetro
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Factura.class))) // Asumo que el servicio devuelve Factura, no FacturaDTO aquí
    @ApiResponse(responseCode = "400", description = "Tipo de movimiento inválido")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Factura>> buscarPorTipoMovimiento(@PathVariable Factura.TipoMovimiento tipoMovimiento) {
        return new ResponseEntity<>(facturaService.buscarPorTipoMovimiento(tipoMovimiento), HttpStatus.OK);
    }

    @GetMapping("/rango-fechas")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar por rango de fechas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca facturas por rango de fechas", description = "Obtiene facturas dentro de un rango de fechas específico.")
    @Parameter(name = "inicio", description = "Fecha y hora de inicio (formato Timestamp)", required = true, example = "2023-01-01T00:00:00Z") // Ejemplo de formato Timestamp ISO 8601
    @Parameter(name = "fin", description = "Fecha y hora de fin (formato Timestamp)", required = true, example = "2023-12-31T23:59:59Z")
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Factura.class))) // Asumo Factura no FacturaDTO
    @ApiResponse(responseCode = "400", description = "Formato de fecha inválido")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Factura>> buscarPorRangoFechas(
            @RequestParam Timestamp inicio,
            @RequestParam Timestamp fin) {
        return new ResponseEntity<>(facturaService.buscarPorRangoFechas(inicio, fin), HttpStatus.OK);
    }

    @GetMapping("/pendientes-pago")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar facturas pendientes de pago
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca facturas pendientes de pago", description = "Obtiene facturas cuyo estado indica que están pendientes de pago.")
    @ApiResponse(responseCode = "200", description = "Facturas pendientes encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Factura.class))) // Asumo Factura no FacturaDTO
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Factura>> buscarFacturasPendientesPago() {
        return new ResponseEntity<>(facturaService.buscarFacturasPendientesPago(), HttpStatus.OK);
    }

    @GetMapping("/total-facturado-por-dia")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO calcular total facturado por día
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Calcula el total facturado por día", description = "Calcula y devuelve el total facturado agrupado por día dentro de un rango de fechas.")
    @Parameter(name = "inicio", description = "Fecha y hora de inicio (formato Timestamp)", required = true, example = "2023-01-01T00:00:00Z")
    @Parameter(name = "fin", description = "Fecha y hora de fin (formato Timestamp)", required = true, example = "2023-12-31T23:59:59Z")
    @ApiResponse( // Inicia @ApiResponse
            responseCode = "200",
            description = "Total facturado por día calculado",
            content = @Content( // Inicia @Content
                    mediaType = "application/json",
                    array = @ArraySchema( // Inicia @ArraySchema (indica que la respuesta es un array principal)
                            schema = @Schema( // Inicia @Schema (describe CADA elemento del array principal, que es un Object[])
                                    // REMOVE 'items =' here. Apply oneOf directly to THIS schema
                                    oneOf = { // Inicia oneOf (los elementos pueden ser String o BigDecimal)
                                            String.class,
                                            BigDecimal.class
                                    } // Cierra oneOf
                            ) // Cierra el @Schema que describe cada Object[]
                    ) // Cierra el @ArraySchema
            ) // Cierra el @Content
    ) // Cierra @ApiResponse
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Object[]>> calcularTotalFacturadoPorDia(
            @RequestParam Timestamp inicio,
            @RequestParam Timestamp fin) {
        return new ResponseEntity<>(facturaService.calcularTotalFacturadoPorDia(inicio, fin), HttpStatus.OK);
    }

    @GetMapping("/total-mayor-a/{montoMinimo}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar facturas con total mayor a un monto
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca facturas con total mayor a un monto", description = "Obtiene facturas cuyo total es mayor que un monto especificado.")
    @Parameter(name = "montoMinimo", description = "Monto mínimo para filtrar facturas", required = true, example = "500.00")
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Factura.class))) // Asumo Factura no FacturaDTO
    @ApiResponse(responseCode = "400", description = "Formato de monto inválido")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Factura>> buscarFacturasConTotalMayorA(@PathVariable BigDecimal montoMinimo) {
        return new ResponseEntity<>(facturaService.buscarFacturasConTotalMayorA(montoMinimo), HttpStatus.OK);
    }
}