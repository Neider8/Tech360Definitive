package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.BodegaDTO;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.mapper.BodegaMapper;
import com.telastech360.crmTT360.service.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
@RequestMapping("/api/bodegas")
@Tag(name = "Bodegas", description = "Gestión de Bodegas en el sistema") // Anotación Tag para el controlador
public class BodegaController {

    private final BodegaService bodegaService;
    private final BodegaMapper bodegaMapper;

    @Autowired
    public BodegaController(BodegaService bodegaService, BodegaMapper bodegaMapper) {
        this.bodegaService = bodegaService;
        this.bodegaMapper = bodegaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Lista todas las bodegas", description = "Obtiene una lista de todas las bodegas registradas en el sistema.") // Anotación Operation
    @ApiResponse(responseCode = "200", description = "Lista de bodegas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class))) // Describe la respuesta exitosa
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor") // Posible error global
    public ResponseEntity<List<Bodega>> listarTodasLasBodegas() {
        List<Bodega> bodegas = bodegaService.listarTodasLasBodegas();
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Obtiene una bodega por ID", description = "Recupera los detalles de una bodega específica usando su ID.")
    @Parameter(name = "id", description = "ID de la bodega a obtener", required = true, example = "1") // Describe un parámetro
    @ApiResponse(responseCode = "200", description = "Bodega encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Bodega> obtenerBodegaPorId(@PathVariable Long id) {
        Bodega bodega = bodegaService.obtenerBodegaPorId(id); // Asumo que el servicio lanza 404 si no existe
        return new ResponseEntity<>(bodega, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea una nueva bodega", description = "Registra una nueva bodega en el sistema.")
    @RequestBody(description = "Datos de la bodega a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class))) // Describe el cuerpo de la solicitud
    @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto (ej: nombre de bodega duplicado si aplica)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Bodega> crearBodega(@Valid @RequestBody BodegaDTO bodegaDTO) {
        Bodega nuevaBodega = bodegaService.crearBodega(bodegaMapper.toEntity(bodegaDTO)); // Usar BodegaMapper inyectado
        return new ResponseEntity<>(nuevaBodega, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualiza una bodega existente", description = "Modifica los detalles de una bodega usando su ID.")
    @Parameter(name = "id", description = "ID de la bodega a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados de la bodega", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BodegaDTO.class)))
    @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Bodega> actualizarBodega(
            @PathVariable Long id,
            @Valid @RequestBody BodegaDTO bodegaDTO
    ) {
        Bodega bodegaActualizada = bodegaService.actualizarBodega(id, bodegaMapper.toEntity(bodegaDTO));
        return new ResponseEntity<>(bodegaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina una bodega", description = "Elimina una bodega del sistema usando su ID.")
    @Parameter(name = "id", description = "ID de la bodega a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        bodegaService.eliminarBodega(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoints adicionales para buscar por tipo, ubicación, etc.
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Busca bodegas por tipo", description = "Obtiene una lista de bodegas filtradas por su tipo.")
    @Parameter(name = "tipo", description = "Tipo de bodega (enum)", required = true, example = "ALMACEN") // Ajusta el ejemplo según tu enum
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Bodega>> buscarPorTipo(@PathVariable Bodega.TipoBodega tipo) {
        List<Bodega> bodegas = bodegaService.buscarBodegasPorTipo(tipo);
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }

    @GetMapping("/ubicacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Busca bodegas por ubicación", description = "Obtiene una lista de bodegas cuya ubicación coincide con un texto de búsqueda.")
    @Parameter(name = "q", description = "Texto para buscar en la ubicación de la bodega", required = true, example = "Calle Falsa 123")
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Bodega>> buscarPorUbicacion(@RequestParam("q") String textoBusqueda) {
        List<Bodega> bodegas = bodegaService.buscarBodegasPorUbicacion(textoBusqueda);
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }

    @GetMapping("/capacidad-disponible")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Lista bodegas con capacidad disponible", description = "Obtiene bodegas que aún tienen capacidad de almacenamiento disponible.")
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Bodega>> buscarConCapacidadDisponible() {
        List<Bodega> bodegas = bodegaService.buscarBodegasConCapacidadDisponible();
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }

    @GetMapping("/responsable/{responsableId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Busca bodegas por responsable", description = "Obtiene una lista de bodegas asociadas a un responsable específico.")
    @Parameter(name = "responsableId", description = "ID del usuario responsable de la bodega", required = true, example = "10")
    @ApiResponse(responseCode = "200", description = "Bodegas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Bodega.class)))
    @ApiResponse(responseCode = "404", description = "Responsable no encontrado (si tu servicio lanza ResourceNotFoundException)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Bodega>> buscarPorResponsable(@PathVariable Long responsableId) {
        List<Bodega> bodegas = bodegaService.buscarBodegasPorResponsable(responsableId);
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }
}