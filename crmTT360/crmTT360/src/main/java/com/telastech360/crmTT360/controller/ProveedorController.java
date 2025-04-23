package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Gestión de Proveedores en el sistema") // Anotación Tag
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar proveedores
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Lista todos los proveedores", description = "Obtiene una lista de todos los proveedores registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Proveedor>> listarTodosLosProveedores() {
        List<Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
        return new ResponseEntity<>(proveedores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener un proveedor por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Obtiene un proveedor por ID", description = "Recupera los detalles de un proveedor específico usando su ID.")
    @Parameter(name = "id", description = "ID del proveedor a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Proveedor> obtenerProveedorPorId(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
        return new ResponseEntity<>(proveedor, HttpStatus.OK);
    }

    @PostMapping
    // Ejemplo: Permitir a ADMIN y GERENTE crear proveedores
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crea un nuevo proveedor", description = "Registra un nuevo proveedor en el sistema.")
    @RequestBody(description = "Datos del proveedor a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody Proveedor proveedor) {
        Proveedor nuevoProveedor = proveedorService.crearProveedor(proveedor);
        return new ResponseEntity<>(nuevoProveedor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN y GERENTE actualizar proveedores
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualiza un proveedor existente", description = "Modifica los detalles de un proveedor usando su ID.")
    @Parameter(name = "id", description = "ID del proveedor a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados del proveedor", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Proveedor> actualizarProveedor(
            @PathVariable Long id,
            @RequestBody Proveedor proveedorActualizado
    ) {
        Proveedor proveedor = proveedorService.actualizarProveedor(id, proveedorActualizado);
        return new ResponseEntity<>(proveedor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar proveedores
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un proveedor", description = "Elimina un proveedor del sistema usando su ID.")
    @Parameter(name = "id", description = "ID del proveedor a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado (Solo ADMIN)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/buscar")
    // Ejemplo: Permitir a todos los roles operativos buscar proveedores
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'OPERARIO')")
    @Operation(summary = "Busca proveedores por nombre y/o ubicación", description = "Obtiene una lista de proveedores filtrados opcionalmente por nombre y ubicación.")
    @Parameter(name = "nombre", description = "Nombre o parte del nombre del proveedor", required = false, example = "Textiles")
    @Parameter(name = "ubicacion", description = "Ubicación o parte de la ubicación del proveedor", required = false, example = "Bogotá")
    @ApiResponse(responseCode = "200", description = "Proveedores encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe la respuesta (Lista de entidad)
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<Proveedor>> buscarProveedores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ubicacion
    ) {
        List<Proveedor> proveedores;
        if (nombre != null && ubicacion != null) {
            proveedores = proveedorService.buscarProveedoresPorNombreYUbicacion(nombre, ubicacion);
        } else if (nombre != null) {
            proveedores = proveedorService.buscarProveedoresPorNombre(nombre);
        } else if (ubicacion != null) {
            proveedores = proveedorService.buscarProveedoresPorUbicacion(ubicacion);
        } else {
            proveedores = proveedorService.listarTodosLosProveedores(); // Devuelve todos si no hay parámetros
        }
        return new ResponseEntity<>(proveedores, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    // Ejemplo: Permitir a ADMIN, GERENTE y CAJERO buscar por email (podría contener info sensible)
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Busca un proveedor por email", description = "Obtiene un proveedor específico usando su dirección de correo electrónico.")
    @Parameter(name = "email", description = "Dirección de email del proveedor", required = true, example = "contacto@proveedorA.com")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Proveedor.class))) // Describe la respuesta (Entidad individual)
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado con ese email")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Proveedor> buscarPorEmail(@PathVariable String email) {
        Proveedor proveedor = proveedorService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con email: " + email));
        return new ResponseEntity<>(proveedor, HttpStatus.OK);
    }
}