package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.dto.ClienteInternoDTO;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.mapper.ClienteInternoMapper;
import com.telastech360.crmTT360.service.ClienteInternoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
@RequestMapping("/api/clientes-internos")
@Tag(name = "Clientes Internos", description = "Gestión de Clientes Internos en el sistema") // Anotación Tag para el controlador
public class ClienteInternoController {

    private final ClienteInternoService clienteService;
    private final ClienteInternoMapper clienteInternoMapper; // Inyectar el mapper si se usa directamente

    @Autowired
    public ClienteInternoController(ClienteInternoService clienteService, ClienteInternoMapper clienteInternoMapper) {
        this.clienteService = clienteService;
        this.clienteInternoMapper = clienteInternoMapper; // Inyectar mapper
    }

    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar clientes internos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los clientes internos", description = "Obtiene una lista de todos los clientes internos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ClienteInterno>> listarTodosLosClientes() {
        List<ClienteInterno> clientes = clienteService.listarTodosLosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener detalles de un cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un cliente interno por ID", description = "Recupera los detalles de un cliente interno específico usando su ID.")
    @Parameter(name = "id", description = "ID del cliente interno a obtener", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Cliente interno encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "404", description = "Cliente interno no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ClienteInterno> obtenerClientePorId(@PathVariable Long id) {
        ClienteInterno cliente = clienteService.obtenerClientePorId(id); // Asumo que el servicio lanza 404
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO crear clientes internos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Crea un nuevo cliente interno", description = "Registra un nuevo cliente interno en el sistema.")
    @RequestBody(description = "Datos del cliente interno a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "201", description = "Cliente interno creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto (ej: cliente duplicado si aplica)") // O tu excepción de duplicado
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ClienteInterno> crearCliente(@Valid @RequestBody ClienteInternoDTO clienteDTO) {
        ClienteInterno nuevoCliente = clienteService.crearCliente(clienteInternoMapper.toEntity(clienteDTO)); // Usar mapper
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO actualizar clientes internos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Actualiza un cliente interno existente", description = "Modifica los detalles de un cliente interno usando su ID.")
    @Parameter(name = "id", description = "ID del cliente interno a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del cliente interno", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInternoDTO.class)))
    @ApiResponse(responseCode = "200", description = "Cliente interno actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Cliente interno no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ClienteInterno> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteInternoDTO clienteDTO
    ) {
        ClienteInterno clienteActualizado = clienteService.actualizarCliente(id, clienteInternoMapper.toEntity(clienteDTO)); // Usar mapper
        return new ResponseEntity<>(clienteActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE eliminar clientes internos
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Elimina un cliente interno", description = "Elimina un cliente interno del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del cliente interno a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Cliente interno eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Cliente interno no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoints adicionales para buscar por nombre, tipo, etc.
    @GetMapping("/buscar/nombre")
    // Ejemplo: Permitir a todos los roles operativos buscar por nombre
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca clientes internos por nombre", description = "Obtiene una lista de clientes internos cuyo nombre coincide con un texto de búsqueda.")
    @Parameter(name = "q", description = "Texto para buscar en el nombre del cliente", required = true, example = "Juan Perez")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ClienteInterno>> buscarPorNombre(@RequestParam("q") String nombre) {
        List<ClienteInterno> clientes = clienteService.buscarPorNombre(nombre);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/tipo/{tipo}")
    // Ejemplo: Permitir a todos los roles operativos buscar por tipo
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca clientes internos por tipo", description = "Obtiene una lista de clientes internos filtrados por su tipo.")
    @Parameter(name = "tipo", description = "Tipo de cliente (enum)", required = true, example = "DISTRIBUIDOR") // Ajusta el ejemplo según tu enum
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ClienteInterno>> buscarPorTipo(@PathVariable String tipo) {
        // Necesitas convertir el String tipo a ClienteInterno en el servicio o aquí
        List<ClienteInterno> clientes = clienteService.buscarPorTipo(ClienteInterno.TipoCliente.valueOf(tipo));
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/responsable/{responsableId}")
    // Ejemplo: Permitir a todos los roles operativos buscar por responsable
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca clientes internos por responsable", description = "Obtiene una lista de clientes internos asociados a un responsable específico.")
    @Parameter(name = "responsableId", description = "ID del usuario responsable del cliente", required = true, example = "10")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "404", description = "Responsable no encontrado (si tu servicio lanza ResourceNotFoundException)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ClienteInterno>> buscarPorResponsable(@PathVariable Long responsableId) {
        List<ClienteInterno> clientes = clienteService.buscarPorResponsable(responsableId);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/presupuesto/mayor-que")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar por presupuesto mayor que
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca clientes internos con presupuesto mayor que", description = "Obtiene clientes internos cuyo presupuesto excede un monto especificado.")
    @Parameter(name = "monto", description = "Monto mínimo del presupuesto", required = true, example = "10000.00")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteInterno.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<ClienteInterno>> buscarPorPresupuestoMayorQue(@RequestParam BigDecimal monto) {
        List<ClienteInterno> clientes = clienteService.buscarPorPresupuestoMayorQue(monto);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
}