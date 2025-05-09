// src/main/java/com/telastech360/crmTT360/controller/ClienteInternoController.java
package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ClienteInternoDTO;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.mapper.ClienteInternoMapper;
import com.telastech360.crmTT360.service.ClienteInternoService;
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
 * Controlador REST para gestionar las operaciones CRUD y consultas relacionadas con los Clientes Internos.
 */
@RestController
@RequestMapping("/api/clientes-internos")
@Tag(name = "Clientes Internos", description = "Gestión de Clientes Internos")
public class ClienteInternoController {

    private static final Logger log = LoggerFactory.getLogger(ClienteInternoController.class);

    private final ClienteInternoService clienteService;
    private final ClienteInternoMapper clienteInternoMapper;

    @Autowired
    public ClienteInternoController(ClienteInternoService clienteService, ClienteInternoMapper clienteInternoMapper) {
        this.clienteService = clienteService;
        this.clienteInternoMapper = clienteInternoMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEER_CLIENTES')") // Modificado
    @Operation(summary = "Lista todos los clientes internos", description = "Obtiene una lista completa de todos los clientes internos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClienteInternoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ClienteInternoDTO>> listarTodosLosClientes() {
        log.info("GET /api/clientes-internos - Solicitud para listar todos los clientes");
        List<ClienteInterno> clientes = clienteService.listarTodosLosClientes();
        List<ClienteInternoDTO> dtos = clientes.stream()
                .map(clienteInternoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/clientes-internos - Devolviendo {} clientes", dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEER_CLIENTES')") // Modificado
    @Operation(summary = "Obtiene un cliente interno por ID", description = "Recupera los detalles completos de un cliente interno específico usando su ID.")
    @Parameter(name = "id", description = "ID único del cliente interno", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ClienteInternoDTO> obtenerClientePorId(@PathVariable Long id) {
        log.info("GET /api/clientes-internos/{} - Solicitud para obtener cliente por ID", id);
        ClienteInterno cliente = clienteService.obtenerClientePorId(id);
        ClienteInternoDTO dto = clienteInternoMapper.toDTO(cliente);
        log.info("GET /api/clientes-internos/{} - Cliente encontrado: {}", id, dto.getNombre());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_CLIENTE')") // Modificado
    @Operation(summary = "Crea un nuevo cliente interno", description = "Registra un nuevo cliente interno con su información básica y responsable.")
    @RequestBody(description = "Datos del cliente interno a crear. Código, nombre, tipo y ID del responsable son obligatorios.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos (fallo de validación DTO o tipo inválido)", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código interno ya existe", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Usuario responsable no existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ClienteInternoDTO> crearCliente(@Valid @RequestBody ClienteInternoDTO clienteDto) {
        log.info("POST /api/clientes-internos - Solicitud para crear cliente: {}", clienteDto.getCodigoInterno());
        ClienteInterno clienteACrear = clienteInternoMapper.toEntity(clienteDto);
        ClienteInterno nuevoCliente = clienteService.crearCliente(clienteACrear);
        ClienteInternoDTO responseDto = clienteInternoMapper.toDTO(nuevoCliente);
        log.info("POST /api/clientes-internos - Cliente '{}' creado con ID: {}", responseDto.getNombre(), nuevoCliente.getClienteId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTE')") // Modificado
    @Operation(summary = "Actualiza un cliente interno existente", description = "Modifica los detalles de un cliente interno, incluyendo su responsable.")
    @Parameter(name = "id", description = "ID del cliente a actualizar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @RequestBody(description = "Datos actualizados del cliente interno", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No encontrado - Cliente o Usuario responsable no existe", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - Código interno ya existe", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<ClienteInternoDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteInternoDTO clienteDto
    ) {
        log.info("PUT /api/clientes-internos/{} - Solicitud para actualizar cliente", id);
        ClienteInterno clienteActualizado = clienteInternoMapper.toEntity(clienteDto);
        ClienteInterno clienteGuardado = clienteService.actualizarCliente(id, clienteActualizado);
        ClienteInternoDTO responseDto = clienteInternoMapper.toDTO(clienteGuardado);
        log.info("PUT /api/clientes-internos/{} - Cliente actualizado", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_CLIENTE')") // Modificado
    @Operation(summary = "Elimina un cliente interno", description = "Elimina un cliente interno del sistema. Falla si tiene pedidos asociados.")
    @Parameter(name = "id", description = "ID del cliente a eliminar", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto - El cliente tiene pedidos asociados y no puede ser eliminado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("DELETE /api/clientes-internos/{} - Solicitud para eliminar cliente", id);
        clienteService.eliminarCliente(id);
        log.info("DELETE /api/clientes-internos/{} - Cliente eliminado", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints Adicionales ---

    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasAuthority('BUSCAR_CLIENTES')") // Modificado (o LEER_CLIENTES)
    @Operation(summary = "Busca clientes internos por nombre", description = "Obtiene una lista de clientes internos cuyo nombre contiene el texto de búsqueda (case-insensitive).")
    @Parameter(name = "q", description = "Texto a buscar en el nombre del cliente", required = true, example = "Perez")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClienteInternoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ClienteInternoDTO>> buscarPorNombre(@RequestParam("q") String nombre) {
        log.info("GET /api/clientes-internos/buscar/nombre?q={} - Buscando por nombre", nombre);
        List<ClienteInterno> clientes = clienteService.buscarPorNombre(nombre);
        List<ClienteInternoDTO> dtos = clientes.stream()
                .map(clienteInternoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/clientes-internos/buscar/nombre?q={} - Encontrados {} clientes", nombre, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAuthority('BUSCAR_CLIENTES')") // Modificado (o LEER_CLIENTES)
    @Operation(summary = "Busca clientes internos por tipo", description = "Obtiene una lista de clientes internos filtrados por su tipo (INTERNO o EXTERNO).")
    @Parameter(name = "tipo", description = "Tipo de cliente (INTERNO, EXTERNO)", required = true, example = "EXTERNO", schema = @Schema(implementation = ClienteInterno.TipoCliente.class))
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClienteInternoDTO.class))))
    @ApiResponse(responseCode = "400", description = "Tipo inválido", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ClienteInternoDTO>> buscarPorTipo(@PathVariable ClienteInterno.TipoCliente tipo) {
        log.info("GET /api/clientes-internos/tipo/{} - Buscando por tipo", tipo);
        List<ClienteInterno> clientes = clienteService.buscarPorTipo(tipo);
        List<ClienteInternoDTO> dtos = clientes.stream()
                .map(clienteInternoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/clientes-internos/tipo/{} - Encontrados {} clientes", tipo, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/responsable/{responsableId}")
    @PreAuthorize("hasAuthority('BUSCAR_CLIENTES')") // Modificado (o LEER_CLIENTES)
    @Operation(summary = "Busca clientes internos por responsable", description = "Obtiene una lista de clientes internos asignados a un usuario responsable específico.")
    @Parameter(name = "responsableId", description = "ID del usuario responsable", required = true, example = "10", schema = @Schema(type = "integer", format = "int64"))
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClienteInternoDTO.class))))
    @ApiResponse(responseCode = "404", description = "Usuario responsable no encontrado", content = @Content)
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ClienteInternoDTO>> buscarPorResponsable(@PathVariable Long responsableId) {
        log.info("GET /api/clientes-internos/responsable/{} - Buscando por responsable", responsableId);
        List<ClienteInterno> clientes = clienteService.buscarPorResponsable(responsableId);
        List<ClienteInternoDTO> dtos = clientes.stream()
                .map(clienteInternoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/clientes-internos/responsable/{} - Encontrados {} clientes", responsableId, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/presupuesto/mayor-que")
    @PreAuthorize("hasAuthority('BUSCAR_CLIENTES_POR_PRESUPUESTO')") // Modificado (permiso específico)
    @Operation(summary = "Busca clientes con presupuesto mayor o igual que", description = "Obtiene clientes internos cuyo presupuesto anual es mayor o igual al monto especificado.")
    @Parameter(name = "monto", description = "Monto mínimo del presupuesto anual a buscar", required = true, example = "10000.00", schema = @Schema(type = "number", format = "double"))
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClienteInternoDTO.class))))
    @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    public ResponseEntity<List<ClienteInternoDTO>> buscarPorPresupuestoMayorQue(@RequestParam BigDecimal monto) {
        log.info("GET /api/clientes-internos/presupuesto/mayor-que?monto={} - Buscando por presupuesto", monto);
        List<ClienteInterno> clientes = clienteService.buscarPorPresupuestoMayorQue(monto);
        List<ClienteInternoDTO> dtos = clientes.stream()
                .map(clienteInternoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("GET /api/clientes-internos/presupuesto/mayor-que?monto={} - Encontrados {} clientes", monto, dtos.size());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}