// src/main/java/com/telastech360/crmTT360/dto/MateriaPrimaDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * DTO (Data Transfer Object) para la entidad MateriaPrima.
 * Extiende (conceptualmente) la información de {@link ItemDTO} con campos específicos
 * para materias primas, como tipo de material, dimensiones y proveedor de tela.
 */
public class MateriaPrimaDTO {

    private Long itemId; // ID del ítem base, útil para respuestas/actualizaciones

    // --- Campos heredados de ItemDTO (replicados aquí para claridad) ---
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no puede exceder 100 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    private Integer stockMaximo;

    private Date fechaVencimiento;

    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    @NotNull(message = "El proveedor general es obligatorio")
    private Long proveedorId; // Proveedor general del Item

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    // --- Campos específicos de MateriaPrima ---
    @NotNull(message = "El tipo de material es obligatorio")
    private String tipoMaterial; // Representación en String del enum TipoMaterial

    @Positive(message = "El ancho del rollo debe ser positivo")
    private BigDecimal anchoRollo; // Opcional, puede ser nulo si no aplica

    @Positive(message = "El peso por metro debe ser positivo")
    private BigDecimal pesoMetro; // Opcional, puede ser nulo si no aplica

    private Long proveedorTelaId; // ID del proveedor específico de tela (opcional)

    /**
     * Constructor por defecto.
     */
    public MateriaPrimaDTO() {
    }

    // --- Getters y Setters ---

    /**
     * Obtiene el ID del ítem base.
     * @return El ID del ítem.
     */
    public Long getItemId() { return itemId; }
    /**
     * Establece el ID del ítem base.
     * @param itemId El ID del ítem.
     */
    public void setItemId(Long itemId) { this.itemId = itemId; }

    /**
     * Obtiene el código de la materia prima.
     * @return El código.
     */
    public String getCodigo() { return codigo; }
    /**
     * Establece el código de la materia prima.
     * @param codigo El nuevo código.
     */
    public void setCodigo(String codigo) { this.codigo = codigo; }

    /**
     * Obtiene el nombre de la materia prima.
     * @return El nombre.
     */
    public String getNombre() { return nombre; }
    /**
     * Establece el nombre de la materia prima.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la descripción de la materia prima.
     * @return La descripción.
     */
    public String getDescripcion() { return descripcion; }
    /**
     * Establece la descripción de la materia prima.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Obtiene la unidad de medida (ej. "metros", "kg").
     * @return La unidad de medida.
     */
    public String getUnidadMedida() { return unidadMedida; }
    /**
     * Establece la unidad de medida.
     * @param unidadMedida La nueva unidad de medida.
     */
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    /**
     * Obtiene el precio unitario de la materia prima.
     * @return El precio.
     */
    public BigDecimal getPrecio() { return precio; }
    /**
     * Establece el precio unitario de la materia prima.
     * @param precio El nuevo precio.
     */
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    /**
     * Obtiene el stock disponible.
     * @return El stock disponible.
     */
    public Integer getStockDisponible() { return stockDisponible; }
    /**
     * Establece el stock disponible.
     * @param stockDisponible El nuevo stock.
     */
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }

    /**
     * Obtiene el stock mínimo permitido.
     * @return El stock mínimo.
     */
    public Integer getStockMinimo() { return stockMinimo; }
    /**
     * Establece el stock mínimo permitido.
     * @param stockMinimo El nuevo stock mínimo.
     */
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    /**
     * Obtiene el stock máximo permitido (opcional).
     * @return El stock máximo.
     */
    public Integer getStockMaximo() { return stockMaximo; }
    /**
     * Establece el stock máximo permitido (opcional).
     * @param stockMaximo El nuevo stock máximo.
     */
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }

    /**
     * Obtiene la fecha de vencimiento (opcional).
     * @return La fecha de vencimiento.
     */
    public Date getFechaVencimiento() { return fechaVencimiento; }
    /**
     * Establece la fecha de vencimiento (opcional).
     * @param fechaVencimiento La nueva fecha de vencimiento.
     */
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    /**
     * Obtiene el ID del estado asociado.
     * @return El ID del estado.
     */
    public Long getEstadoId() { return estadoId; }
    /**
     * Establece el ID del estado asociado.
     * @param estadoId El nuevo ID del estado.
     */
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }

    /**
     * Obtiene el ID del proveedor general asociado.
     * @return El ID del proveedor general.
     */
    public Long getProveedorId() { return proveedorId; }
    /**
     * Establece el ID del proveedor general asociado.
     * @param proveedorId El nuevo ID del proveedor general.
     */
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    /**
     * Obtiene el ID de la categoría asociada.
     * @return El ID de la categoría.
     */
    public Long getCategoriaId() { return categoriaId; }
    /**
     * Establece el ID de la categoría asociada.
     * @param categoriaId El nuevo ID de la categoría.
     */
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    /**
     * Obtiene el ID de la bodega asociada.
     * @return El ID de la bodega.
     */
    public Long getBodegaId() { return bodegaId; }
    /**
     * Establece el ID de la bodega asociada.
     * @param bodegaId El nuevo ID de la bodega.
     */
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }

    /**
     * Obtiene el ID del usuario responsable asociado.
     * @return El ID del usuario.
     */
    public Long getUsuarioId() { return usuarioId; }
    /**
     * Establece el ID del usuario responsable asociado.
     * @param usuarioId El nuevo ID del usuario.
     */
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    /**
     * Obtiene el tipo de material como String (ej. "TELA", "HILO").
     * @return El tipo de material.
     */
    public String getTipoMaterial() { return tipoMaterial; }
    /**
     * Establece el tipo de material.
     * @param tipoMaterial El nuevo tipo (debe coincidir con enum {@link com.telastech360.crmTT360.entity.MateriaPrima.TipoMaterial}).
     */
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    /**
     * Obtiene el ancho del rollo (opcional).
     * @return El ancho del rollo.
     */
    public BigDecimal getAnchoRollo() { return anchoRollo; }
    /**
     * Establece el ancho del rollo (opcional).
     * @param anchoRollo El nuevo ancho.
     */
    public void setAnchoRollo(BigDecimal anchoRollo) { this.anchoRollo = anchoRollo; }

    /**
     * Obtiene el peso por metro (opcional).
     * @return El peso por metro.
     */
    public BigDecimal getPesoMetro() { return pesoMetro; }
    /**
     * Establece el peso por metro (opcional).
     * @param pesoMetro El nuevo peso por metro.
     */
    public void setPesoMetro(BigDecimal pesoMetro) { this.pesoMetro = pesoMetro; }

    /**
     * Obtiene el ID del proveedor específico de tela (opcional).
     * @return El ID del proveedor de tela.
     */
    public Long getProveedorTelaId() { return proveedorTelaId; }
    /**
     * Establece el ID del proveedor específico de tela (opcional).
     * @param proveedorTelaId El nuevo ID del proveedor de tela.
     */
    public void setProveedorTelaId(Long proveedorTelaId) { this.proveedorTelaId = proveedorTelaId; }
}