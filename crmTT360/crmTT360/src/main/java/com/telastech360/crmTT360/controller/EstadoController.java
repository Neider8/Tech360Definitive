// src/main/java/com/telastech360/crmTT360/controller/EstadoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import com.telastech360.crmTT360.mapper.EstadoMapper;
import com.telastech360.crmTT360.service.EstadoService;
import jakarta.validation.Valid; // Importar @Valid
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Importaciones de Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// Ojo: @RequestBody de Swagger es io.swagger.v3.oas.annotations.parameters.RequestBody
//      @RequestBody de Spring es org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ArraySchema;

/**
 * Controlador REST para gestionar las operaciones CRUD y consultas de la entidad Estado.
 */
@RestController
@RequestMapping("/api/estados")
@Tag(name = "Estados", description = "Gestión de Estados para diferentes entidades (Pedidos, Items, Bodegas, etc.)")
public class EstadoController {

    // Logger para la clase
    private static final Logger log = LoggerFactory.getLogger(EstadoController.class);

    private final EstadoService estadoService;
    private final EstadoMapper estadoMapper;

    @Autowired
    public EstadoController(EstadoService estadoService, EstadoMapper estadoMapper) {
        this.estadoService = estadoService;
        this.estadoMapper = estadoMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_ESTADOS')")
    @Operation(summary = "Lista todos los estados", description = "Obtiene una lista de todos los estados disponibles en el sistema, sin filtrar por tipo.")
    @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EstadoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<EstadoDTO>> listarTodosLosEstados() {
        log.info("GET /api/estados - Solicitud para listar todos los estados");
        List<EstadoDTO> estados = estadoService.listarTodosLosEstados();
        log.info("GET /api/estados - Devolviendo {} estados", estados.size());
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_ESTADOS')")
    @Operation(summary = "Obtiene un estado por ID", description = "Recupera los detalles de un estado específico usando su ID único.")
    @Parameter(name = "id", description = "ID único del estado", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "200", description = "Estado encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<EstadoDTO> obtenerEstadoPorId(@PathVariable Long id) {
        log.info("GET /api/estados/{} - Solicitud para obtener estado por ID", id);
        EstadoDTO estado = estadoService.obtenerEstadoPorId(id);
        log.info("GET /api/estados/{} - Estado encontrado: Tipo={}, Valor='{}'", id, estado.getTipoEstado(), estado.getValor());
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_ESTADO')")
    @Operation(summary = "Crea un nuevo estado", description = "Registra un nuevo estado en el sistema, especificando su tipo y valor. La combinación tipo/valor debe ser única.")
    // Usar @RequestBody de Swagger para describir el cuerpo esperado
    @RequestBody(description = "Datos del estado a crear (tipoEstado y valor son obligatorios)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Estado creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. campos faltantes)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un estado con ese tipo y valor", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    // Usar @Valid de Jakarta y @RequestBody de Spring para el parámetro
    public ResponseEntity<EstadoDTO> crearEstado(@Valid @org.springframework.web.bind.annotation.RequestBody EstadoDTO estadoDTO) {

        // ----- INICIO: Log de Depuración Temporal -----
        // Mostramos el objeto DTO completo tal como lo recibe el controlador (usa el método toString() del DTO)
        log.info(">>> [DEBUG] EstadoDTO Recibido en Controller: {}", estadoDTO);
        // Verificamos explícitamente los valores de los campos problemáticos
        if (estadoDTO != null) {
            log.info(">>> [DEBUG] DTO.tipoEstado: {}", estadoDTO.getTipoEstado()); // Loguea el valor de tipoEstado
            log.info(">>> [DEBUG] DTO.valor: '{}'", estadoDTO.getValor()); // Loguea el valor de valor
        } else {
            log.info(">>> [DEBUG] EstadoDTO Recibido es NULL"); // Si el DTO completo es null
        }
        // ----- FIN: Log de Depuración Temporal -----

        // Lógica original del controlador
        log.info("POST /api/estados - Solicitud para crear estado: Tipo={}, Valor='{}'", estadoDTO.getTipoEstado(), estadoDTO.getValor());
        EstadoDTO nuevoEstado = estadoService.crearEstado(estadoDTO);
        log.info("POST /api/estados - Estado creado: Tipo={}, Valor='{}'", nuevoEstado.getTipoEstado(), nuevoEstado.getValor());
        return new ResponseEntity<>(nuevoEstado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_ESTADO')")
    @Operation(summary = "Actualiza un estado existente", description = "Modifica el tipo y/o valor de un estado existente.")
    @Parameter(name = "id", description = "ID del estado a actualizar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    // Usar @RequestBody de Swagger para describir el cuerpo esperado
    @RequestBody(description = "Datos actualizados del estado (tipoEstado y valor)", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe otro estado con ese tipo y valor", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    // Usar @Valid de Jakarta y @RequestBody de Spring para el parámetro
    public ResponseEntity<EstadoDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody EstadoDTO estadoDTO
    ) {
        // --- Aquí también podrías añadir logs de depuración si el error ocurre al actualizar ---
        log.info(">>> [DEBUG] EstadoDTO Recibido en PUT /api/estados/{}: {}", id, estadoDTO);
        if (estadoDTO != null) {
            log.info(">>> [DEBUG] PUT DTO.tipoEstado: {}", estadoDTO.getTipoEstado());
            log.info(">>> [DEBUG] PUT DTO.valor: '{}'", estadoDTO.getValor());
        }
        // ----------------------------------------------------------------------------------

        log.info("PUT /api/estados/{} - Solicitud para actualizar estado a: Tipo={}, Valor='{}'", id, estadoDTO.getTipoEstado(), estadoDTO.getValor());
        EstadoDTO estadoActualizado = estadoService.actualizarEstado(id, estadoDTO);
        log.info("PUT /api/estados/{} - Estado actualizado", id);
        return new ResponseEntity<>(estadoActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_ESTADO')")
    @Operation(summary = "Elimina un estado", description = "Elimina un estado del sistema. Falla si el estado está actualmente en uso.")
    @Parameter(name = "id", description = "ID del estado a eliminar", required = true, example = "1", schema = @Schema(type="integer", format="int64"))
    @ApiResponse(responseCode = "204", description = "Estado eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Estado no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El estado está en uso y no se puede eliminar", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarEstado(@PathVariable Long id) {
        log.info("DELETE /api/estados/{} - Solicitud para eliminar estado", id);
        estadoService.eliminarEstado(id);
        log.info("DELETE /api/estados/{} - Estado eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAuthority('BUSCAR_ESTADOS')")
    @Operation(summary = "Lista estados por tipo", description = "Obtiene una lista de estados filtrados por su tipo (ej. PEDIDO, ITEM).")
    @Parameter(name = "tipo", description = "Tipo de Estado a filtrar (ACTIVO, INACTIVO, PENDIENTE, CANCELADO, PEDIDO, ITEM)", required = true, example = "PEDIDO", schema = @Schema(implementation = TipoEstado.class))
    @ApiResponse(responseCode = "200", description = "Estados encontrados para el tipo especificado",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EstadoDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo de estado inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<EstadoDTO>> listarEstadosPorTipo(@PathVariable TipoEstado tipo) {
        log.info("GET /api/estados/tipo/{} - Solicitud para listar estados por tipo", tipo);
        List<Estado> estados = estadoService.listarEstadosPorTipo(tipo);
        List<EstadoDTO> dtos = estados.stream()
                .map(estadoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/estados/tipo/{} - Encontrados {} estados", tipo, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/tipos")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lista todos los tipos de estado disponibles", description = "Obtiene los nombres de todos los posibles valores del enum TipoEstado (ej. \"PEDIDO\", \"ITEM\").")
    @ApiResponse(responseCode = "200", description = "Tipos de estado obtenidos exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "PEDIDO"))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<String>> listarTiposDisponibles() {
        log.info("GET /api/estados/tipos - Solicitud para listar tipos de estado disponibles");
        List<TipoEstado> tiposEnum = estadoService.listarTiposEstadoDisponibles();
        List<String> tiposString = tiposEnum.stream().map(Enum::name).collect(Collectors.toList());
        log.info("GET /api/estados/tipos - Tipos disponibles: {}", tiposString);
        return new ResponseEntity<>(tiposString, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('BUSCAR_ESTADOS')")
    @Operation(summary = "Busca estados por valor", description = "Obtiene estados cuyo campo 'valor' contiene el texto de búsqueda (case-insensitive).")
    @Parameter(name = "valor", description = "Texto para buscar en el valor del estado", required = true, example = "Pend")
    @ApiResponse(responseCode = "200", description = "Estados encontrados que coinciden con la búsqueda",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EstadoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<EstadoDTO>> buscarEstadosPorValor(@RequestParam String valor) {
        log.info("GET /api/estados/buscar?valor={} - Solicitud para buscar estados por valor", valor);
        List<Estado> estados = estadoService.buscarEstadosPorValor(valor);
        List<EstadoDTO> dtos = estados.stream()
                .map(estadoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/estados/buscar?valor={} - Encontrados {} estados", valor, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}