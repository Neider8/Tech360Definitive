// src/main/java/com/telastech360/crmTT360/dto/ItemDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * DTO (Data Transfer Object) para la entidad base Item.
 * Utilizado para la transferencia de datos de ítems genéricos,
 * aplicando validaciones y exponiendo los campos comunes necesarios.
 * Este DTO es la base para DTOs más específicos como {@link ProductoDTO} y {@link MateriaPrimaDTO}.
 */
public class ItemDTO {

    // --- CAMBIO: Descomentado/Añadido itemId ---
    private Long itemId; // ID del ítem, útil para respuestas

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no puede exceder 100 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private String descripcion; // Descripción opcional

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio; // Precio unitario del ítem

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible; // Cantidad actual en inventario

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo; // Nivel mínimo de stock antes de alerta/reorden

    private Integer stockMaximo; // Nivel máximo de stock (opcional)

    private Date fechaVencimiento; // Fecha de vencimiento (opcional)

    @NotNull(message = "El estado es obligatorio")
    private Long estadoId; // ID del Estado asociado

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId; // ID del Proveedor asociado

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId; // ID de la Categoría asociada

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId; // ID de la Bodega donde se almacena

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId; // ID del Usuario que registra/modifica

    @NotBlank(message = "El tipo de item es obligatorio")
    private String tipoItem; // "MATERIA_PRIMA" o "PRODUCTO_TERMINADO" (String representation of enum)

    // --- Constructores ---
    /**
     * Constructor por defecto.
     */
    public ItemDTO() {
    }

    /**
     * Constructor con campos obligatorios principales.
     * @param codigo Código único del ítem.
     * @param nombre Nombre descriptivo del ítem.
     * @param unidadMedida Unidad en la que se mide el ítem (ej. "metros", "unidades").
     * @param precio Precio unitario.
     * @param stockDisponible Stock inicial o actual.
     * @param stockMinimo Nivel mínimo de stock.
     * @param estadoId ID del estado inicial.
     * @param proveedorId ID del proveedor.
     * @param categoriaId ID de la categoría.
     * @param bodegaId ID de la bodega.
     * @param usuarioId ID del usuario responsable.
     * @param tipoItem Tipo de ítem ("MATERIA_PRIMA" o "PRODUCTO_TERMINADO").
     */
    public ItemDTO(String codigo, String nombre, String unidadMedida, BigDecimal precio, Integer stockDisponible, Integer stockMinimo, Long estadoId, Long proveedorId, Long categoriaId, Long bodegaId, Long usuarioId, String tipoItem) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.stockMinimo = stockMinimo;
        this.estadoId = estadoId;
        this.proveedorId = proveedorId;
        this.categoriaId = categoriaId;
        this.bodegaId = bodegaId;
        this.usuarioId = usuarioId;
        this.tipoItem = tipoItem;
    }

    // --- Getters y Setters ---

    // --- CAMBIO: Añadido Getter y Setter para itemId ---
    /**
     * Obtiene el ID del ítem.
     * @return El ID del ítem.
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Establece el ID del ítem.
     * @param itemId El nuevo ID del ítem.
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    // ---------------------------------------------------

    /**
     * Obtiene el código único del ítem.
     * @return El código.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el código único del ítem.
     * @param codigo El nuevo código.
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el nombre del ítem.
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del ítem.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del ítem.
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del ítem.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la unidad de medida del ítem.
     * @return La unidad de medida.
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Establece la unidad de medida del ítem.
     * @param unidadMedida La nueva unidad de medida.
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    /**
     * Obtiene el precio unitario del ítem.
     * @return El precio.
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * Establece el precio unitario del ítem.
     * @param precio El nuevo precio.
     */
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el stock disponible actualmente.
     * @return El stock disponible.
     */
    public Integer getStockDisponible() {
        return stockDisponible;
    }

    /**
     * Establece el stock disponible actualmente.
     * @param stockDisponible El nuevo stock disponible.
     */
    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    /**
     * Obtiene el nivel mínimo de stock permitido.
     * @return El stock mínimo.
     */
    public Integer getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Establece el nivel mínimo de stock permitido.
     * @param stockMinimo El nuevo stock mínimo.
     */
    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * Obtiene el nivel máximo de stock permitido (opcional).
     * @return El stock máximo.
     */
    public Integer getStockMaximo() {
        return stockMaximo;
    }

    /**
     * Establece el nivel máximo de stock permitido (opcional).
     * @param stockMaximo El nuevo stock máximo.
     */
    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    /**
     * Obtiene la fecha de vencimiento del ítem (opcional).
     * @return La fecha de vencimiento.
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Establece la fecha de vencimiento del ítem (opcional).
     * @param fechaVencimiento La nueva fecha de vencimiento.
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Obtiene el ID del estado asociado al ítem.
     * @return El ID del estado.
     */
    public Long getEstadoId() {
        return estadoId;
    }

    /**
     * Establece el ID del estado asociado al ítem.
     * @param estadoId El nuevo ID del estado.
     */
    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    /**
     * Obtiene el ID del proveedor principal del ítem.
     * @return El ID del proveedor.
     */
    public Long getProveedorId() {
        return proveedorId;
    }

    /**
     * Establece el ID del proveedor principal del ítem.
     * @param proveedorId El nuevo ID del proveedor.
     */
    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    /**
     * Obtiene el ID de la categoría a la que pertenece el ítem.
     * @return El ID de la categoría.
     */
    public Long getCategoriaId() {
        return categoriaId;
    }

    /**
     * Establece el ID de la categoría a la que pertenece el ítem.
     * @param categoriaId El nuevo ID de la categoría.
     */
    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    /**
     * Obtiene el ID de la bodega donde se almacena el ítem.
     * @return El ID de la bodega.
     */
    public Long getBodegaId() {
        return bodegaId;
    }

    /**
     * Establece el ID de la bodega donde se almacena el ítem.
     * @param bodegaId El nuevo ID de la bodega.
     */
    public void setBodegaId(Long bodegaId) {
        this.bodegaId = bodegaId;
    }

    /**
     * Obtiene el ID del usuario responsable del registro o última modificación.
     * @return El ID del usuario.
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario responsable.
     * @param usuarioId El nuevo ID del usuario.
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el tipo de ítem como String.
     * @return "MATERIA_PRIMA" o "PRODUCTO_TERMINADO".
     */
    public String getTipoItem() {
        return tipoItem;
    }

    /**
     * Establece el tipo de ítem.
     * @param tipoItem El nuevo tipo ("MATERIA_PRIMA" o "PRODUCTO_TERMINADO").
     */
    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }
}