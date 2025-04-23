package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.service.MateriaPrimaService;
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
@RequestMapping("/api/materias-primas")
@Tag(name = "Materia Prima", description = "Gestión de Materia Prima en el sistema") // Anotación Tag
public class MateriaPrimaController {

    private final MateriaPrimaService materiaPrimaService;

    @Autowired
    public MateriaPrimaController(MateriaPrimaService materiaPrimaService) {
        this.materiaPrimaService = materiaPrimaService;
    }

    // ========== ENDPOINTS CRUD ========== //

    /**
     * Listar todas las materias primas.
     *
     * @return Lista de materias primas.
     */
    @GetMapping
    // Ejemplo: Permitir a todos los roles operativos listar materias primas
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Lista todas las materias primas", description = "Obtiene una lista de todas las materias primas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de materias primas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe la respuesta
    @ApiResponse(responseCode = "401", description = "No autenticado") // Describe posibles errores
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<MateriaPrima>> listarTodasLasMateriasPrimas() {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.listarTodasLasMateriasPrimas();
        return new ResponseEntity<>(materiasPrimas, HttpStatus.OK);
    }

    /**
     * Obtener una materia prima por su ID.
     *
     * @param id ID de la materia prima.
     * @return Materia prima encontrada.
     */
    @GetMapping("/{id}")
    // Ejemplo: Permitir a todos los roles operativos obtener una materia prima por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Obtiene una materia prima por ID", description = "Recupera los detalles de una materia prima específica usando su ID.")
    @Parameter(name = "id", description = "ID de la materia prima a obtener", required = true, example = "1") // Describe parámetro
    @ApiResponse(responseCode = "200", description = "Materia prima encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class)))
    @ApiResponse(responseCode = "404", description = "Materia prima no encontrada") // Asumo que el servicio lanza 404
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<MateriaPrima> obtenerMateriaPrimaPorId(@PathVariable Long id) {
        MateriaPrima materiaPrima = materiaPrimaService.obtenerMateriaPrimaPorId(id);
        return new ResponseEntity<>(materiaPrima, HttpStatus.OK);
    }

    /**
     * Crear una nueva materia prima.
     *
     * @param materiaPrima Datos de la materia prima a crear.
     * @return Materia prima creada.
     */
    @PostMapping
    // Ejemplo: Permitir a ADMIN, GERENTE y OPERARIO crear materia prima
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Crea una nueva materia prima", description = "Registra una nueva materia prima en el sistema.")
    @RequestBody(description = "Datos de la materia prima a crear", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe cuerpo solicitud (usando la entidad ya que el método recibe la entidad)
    @ApiResponse(responseCode = "201", description = "Materia prima creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<MateriaPrima> crearMateriaPrima(@RequestBody MateriaPrima materiaPrima) {
        MateriaPrima nuevaMateriaPrima = materiaPrimaService.crearMateriaPrima(materiaPrima);
        return new ResponseEntity<>(nuevaMateriaPrima, HttpStatus.CREATED);
    }

    /**
     * Actualizar una materia prima existente.
     *
     * @param id ID de la materia prima a actualizar.
     * @param materiaPrimaActualizada Datos actualizados de la materia prima.
     * @return Materia prima actualizada.
     */
    @PutMapping("/{id}")
    // Ejemplo: Permitir a ADMIN, GERENTE y OPERARIO actualizar materia prima
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO')")
    @Operation(summary = "Actualiza una materia prima existente", description = "Modifica los detalles de una materia prima usando su ID.")
    @Parameter(name = "id", description = "ID de la materia prima a actualizar", required = true, example = "1")
    @RequestBody(description = "Datos actualizados de la materia prima", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe cuerpo solicitud (usando la entidad)
    @ApiResponse(responseCode = "200", description = "Materia prima actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe respuesta
    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    @ApiResponse(responseCode = "404", description = "Materia prima no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<MateriaPrima> actualizarMateriaPrima(@PathVariable Long id, @RequestBody MateriaPrima materiaPrimaActualizada) {
        MateriaPrima materiaPrima = materiaPrimaService.actualizarMateriaPrima(id, materiaPrimaActualizada);
        return new ResponseEntity<>(materiaPrima, HttpStatus.OK);
    }

    /**
     * Eliminar una materia prima por su ID.
     *
     * @param id ID de la materia prima a eliminar.
     */
    @DeleteMapping("/{id}")
    // Ejemplo: Solo ADMIN puede eliminar materia prima
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina una materia prima", description = "Elimina una materia prima del sistema usando su ID.")
    @Parameter(name = "id", description = "ID de la materia prima a eliminar", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Materia prima eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Materia prima no encontrada")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Void> eliminarMateriaPrima(@PathVariable Long id) {
        materiaPrimaService.eliminarMateriaPrima(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS ADICIONALES (Opcionales) ========== //

    /**
     * Buscar materias primas por tipo de material.
     *
     * @param tipo Tipo de material.
     * @return Lista de materias primas filtradas.
     */
    @GetMapping("/tipo-material/{tipo}")
    // Ejemplo: Permitir a todos los roles operativos buscar por tipo de material
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Busca materias primas por tipo de material", description = "Obtiene una lista de materias primas filtradas por su tipo de material.")
    @Parameter(name = "tipo", description = "Tipo de material (enum)", required = true, example = "TELA",
            schema = @Schema(implementation = MateriaPrima.TipoMaterial.class)) // Documenta el enum como parámetro
    @ApiResponse(responseCode = "200", description = "Materias primas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe la respuesta
    @ApiResponse(responseCode = "400", description = "Tipo de material inválido")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<MateriaPrima>> buscarPorTipoMaterial(@PathVariable MateriaPrima.TipoMaterial tipo) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.buscarPorTipoMaterial(tipo);
        return new ResponseEntity<>(materiasPrimas, HttpStatus.OK);
    }

    /**
     * Buscar materias primas por proveedor.
     *
     * @param proveedorId ID del proveedor.
     * @return Lista de materias primas filtradas.
     */
    @GetMapping("/proveedor/{proveedorId}")
    // Ejemplo: Permitir a todos los roles operativos buscar por proveedor
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERARIO', 'CAJERO')")
    @Operation(summary = "Busca materias primas por proveedor", description = "Obtiene una lista de materias primas asociadas a un proveedor específico.")
    @Parameter(name = "proveedorId", description = "ID del proveedor", required = true, example = "5")
    @ApiResponse(responseCode = "200", description = "Materias primas encontradas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MateriaPrima.class))) // Describe la respuesta
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado (si aplica en tu servicio)")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<MateriaPrima>> buscarPorProveedor(@PathVariable Long proveedorId) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.buscarPorProveedor(proveedorId);
        return new ResponseEntity<>(materiasPrimas, HttpStatus.OK);
    }
}