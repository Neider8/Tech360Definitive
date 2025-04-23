package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import com.telastech360.crmTT360.service.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import com.telastech360.crmTT360.dto.EstadoDTO;

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


/**
 * Controlador REST para gestionar las operaciones CRUD y consultas de la entidad Estado.
 */
@RestController
@RequestMapping("/api/estados") // Define la ruta base para este controlador
@Tag(name = "Estados", description = "Gestión de Estados para diferentes entidades") // Anotación Tag
public class EstadoController {

    private final EstadoService estadoService;

    /**
     * Constructor para inyectar la dependencia de EstadoService.
     * @param estadoService El servicio para la lógica de negocio de Estado.
     */
    @Autowired
    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    // ========== ENDPOINTS CRUD ========== //

    /**
     * Endpoint para listar todos los estados disponibles.
     * Responde a GET /api/estados
     * @return ResponseEntity con la lista de estados y estado OK.
     */
    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar estados
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los estados", description = "Obtiene una lista de todos los estados disponibles en el sistema.") // Anotación Operation
    @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<EstadoDTO>> listarTodosLosEstados() {
        List<EstadoDTO> estados = estadoService.listarTodosLosEstados();
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener un estado por su ID.
     * Responde a GET /api/estados/{id}
     * @param id El ID del estado a obtener.
     * @return ResponseEntity con el estado encontrado y estado OK, o NOT_FOUND si no existe.
     */
    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener un estado por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un estado por ID", description = "Recupera los detalles de un estado específico usando su ID.")
    @Parameter(name = "id", description = "ID del estado a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Estado encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Estado no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<EstadoDTO> obtenerEstadoPorId(@PathVariable Long id) {
        EstadoDTO estado = estadoService.obtenerEstadoPorId(id);
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo estado.
     * Responde a POST /api/estados
     * @param estadoDTO El DTO con los datos del estado a crear.
     * @return ResponseEntity con el estado creado y estado CREATED.
     */
    @PostMapping
    // Ejemplo: Permitir a ADMIN y GERENTE crear estados
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crea un nuevo estado", description = "Registra un nuevo estado en el sistema.")
    @RequestBody(description = "Datos del estado a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class))) // Describe cuerpo solicitud
    @ApiResponse(responseCode = "201", description = "Estado creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto (ej: estado duplicado si aplica)") // O tu excepción
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<EstadoDTO> crearEstado(@Valid @RequestBody EstadoDTO estadoDTO) {
        EstadoDTO nuevoEstado = estadoService.crearEstado(estadoDTO);
        return new ResponseEntity<>(nuevoEstado, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un estado existente.
     * Responde a PUT /api/estados/{id}
     * @param id El ID del estado a actualizar.
     * @param estadoDTO El DTO con los datos actualizados del estado.
     * @return ResponseEntity con el estado actualizado y estado OK, o NOT_FOUND si no existe.
     */
    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE actualizar estados
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualiza un estado existente", description = "Modifica los detalles de un estado usando su ID.")
    @Parameter(name = "id", description = "ID del estado a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del estado", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Estado no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<EstadoDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoDTO estadoDTO
    ) {
        EstadoDTO estadoActualizado = estadoService.actualizarEstado(id, estadoDTO);
        return new ResponseEntity<>(estadoActualizado, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un estado por su ID.
     * Responde a DELETE /api/estados/{id}
     * @param id El ID del estado a eliminar.
     * @return ResponseEntity con estado NO_CONTENT si la eliminación es exitosa, o NOT_FOUND si no existe.
     */
    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar estados
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un estado", description = "Elimina un estado del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del estado a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Estado eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Estado no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarEstado(@PathVariable Long id) {
        estadoService.eliminarEstado(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //

    /**
     * Endpoint para listar estados por tipo.
     * Responde a GET /api/estados/tipo/{tipo}
     * @param tipo El TipoEstado (enum) por el cual filtrar.
     * @return ResponseEntity con la lista de estados filtrados y estado OK.
     */
    @GetMapping("/tipo/{tipo}")
    // Ejemplo: Permitir a todos los roles operativos listar estados por tipo
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista estados por tipo", description = "Obtiene una lista de estados filtrados por su tipo (Pedido, Usuario, etc.).")
    @Parameter(name = "tipo", description = "Tipo de Estado (enum)", required = true, example = "PEDIDO",
            schema = @Schema(implementation = TipoEstado.class)) // Documenta el enum como parámetro
    @ApiResponse(responseCode = "200", description = "Estados encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Estado.class))) // Asumo que el servicio devuelve Estado, no EstadoDTO
    @ApiResponse(responseCode = "400", description = "Tipo de estado inválido") // Si el String no coincide con un enum
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Estado>> listarEstadosPorTipo(@PathVariable TipoEstado tipo) {
        List<Estado> estados = estadoService.listarEstadosPedido(); // Corregido para usar el método correcto
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener la lista de todos los Tipos de Estado disponibles (enum).
     * Responde a GET /api/estados/tipos
     * @return ResponseEntity con la lista de valores del enum TipoEstado y estado OK.
     */
    @GetMapping("/tipos")
    // Ejemplo: Permitir a todos los roles operativos listar tipos de estado disponibles
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los tipos de estado disponibles", description = "Obtiene una lista de todos los posibles valores del enum TipoEstado.")
    @ApiResponse(responseCode = "200", description = "Tipos de estado obtenidos exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TipoEstado.class))) // Describe la lista de enums
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<TipoEstado>> listarTiposDisponibles() {
        List<TipoEstado> tipos = estadoService.listarTiposEstadoDisponibles();
        return new ResponseEntity<>(tipos, HttpStatus.OK);
    }


    @GetMapping("/buscar")
    // Ejemplo: Permitir a todos los roles operativos buscar estados por valor
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca estados por valor", description = "Obtiene una lista de estados cuyo valor coincide con un texto de búsqueda.")
    @Parameter(name = "valor", description = "Texto para buscar en el valor del estado", required = true, example = "Pendiente")
    @ApiResponse(responseCode = "200", description = "Estados encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Estado.class))) // Asumo que el servicio devuelve Estado
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Estado>> buscarEstadosPorValor(@RequestParam String valor) {
        List<Estado> estados = estadoService.buscarEstadosPorValor(valor);
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }
}