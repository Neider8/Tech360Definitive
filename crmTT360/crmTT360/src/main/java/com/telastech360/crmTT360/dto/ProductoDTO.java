// src/main/java/com/telastech360/crmTT360/dto/ProductoDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * DTO (Data Transfer Object) para la entidad Producto.
 * Extiende (conceptualmente) la información de {@link ItemDTO} con campos específicos
 * para productos terminados, como tipo de prenda, talla, color, etc.
 */
public class ProductoDTO {

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

    private Date fechaVencimiento; // Puede no aplicar a productos terminados

    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId; // Proveedor general del Item (quizás no relevante para Producto)

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    // --- Campos específicos de Producto ---
    @NotNull(message = "El tipo de prenda es obligatorio")
    private String tipoPrenda; // Representación en String del enum TipoPrenda

    @NotNull(message = "La talla es obligatoria")
    private String talla; // Representación en String del enum Talla

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    private String color;

    private String temporada; // Opcional

    // Podría faltar 'composicion' si es relevante
    // @NotBlank(message = "La composición es obligatoria")
    // private String composicion;

    @NotNull(message = "La fecha de fabricación es obligatoria")
    private Date fechaFabricacion;

    /**
     * Constructor por defecto.
     */
    public ProductoDTO() {
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
     * Obtiene el código del producto.
     * @return El código.
     */
    public String getCodigo() { return codigo; }
    /**
     * Establece el código del producto.
     * @param codigo El nuevo código.
     */
    public void setCodigo(String codigo) { this.codigo = codigo; }

    /**
     * Obtiene el nombre del producto.
     * @return El nombre.
     */
    public String getNombre() { return nombre; }
    /**
     * Establece el nombre del producto.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la descripción del producto.
     * @return La descripción.
     */
    public String getDescripcion() { return descripcion; }
    /**
     * Establece la descripción del producto.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Obtiene la unidad de medida (ej. "unidad").
     * @return La unidad de medida.
     */
    public String getUnidadMedida() { return unidadMedida; }
    /**
     * Establece la unidad de medida.
     * @param unidadMedida La nueva unidad de medida.
     */
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    /**
     * Obtiene el precio de venta del producto.
     * @return El precio.
     */
    public BigDecimal getPrecio() { return precio; }
    /**
     * Establece el precio de venta del producto.
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
     * Obtiene la fecha de vencimiento (si aplica).
     * @return La fecha de vencimiento.
     */
    public Date getFechaVencimiento() { return fechaVencimiento; }
    /**
     * Establece la fecha de vencimiento (si aplica).
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
     * Obtiene el ID del proveedor general asociado (puede no ser relevante aquí).
     * @return El ID del proveedor.
     */
    public Long getProveedorId() { return proveedorId; }
    /**
     * Establece el ID del proveedor general asociado.
     * @param proveedorId El nuevo ID del proveedor.
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
     * Obtiene el tipo de prenda como String (ej. "CAMISA", "PANTALON").
     * @return El tipo de prenda.
     */
    public String getTipoPrenda() { return tipoPrenda; }
    /**
     * Establece el tipo de prenda.
     * @param tipoPrenda El nuevo tipo (debe coincidir con enum {@link com.telastech360.crmTT360.entity.Producto.TipoPrenda}).
     */
    public void setTipoPrenda(String tipoPrenda) { this.tipoPrenda = tipoPrenda; }

    /**
     * Obtiene la talla como String (ej. "S", "M", "L").
     * @return La talla.
     */
    public String getTalla() { return talla; }
    /**
     * Establece la talla.
     * @param talla La nueva talla (debe coincidir con enum {@link com.telastech360.crmTT360.entity.Producto.Talla}).
     */
    public void setTalla(String talla) { this.talla = talla; }

    /**
     * Obtiene el color del producto.
     * @return El color.
     */
    public String getColor() { return color; }
    /**
     * Establece el color del producto.
     * @param color El nuevo color.
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Obtiene la temporada a la que pertenece el producto (opcional).
     * @return La temporada.
     */
    public String getTemporada() { return temporada; }
    /**
     * Establece la temporada a la que pertenece el producto (opcional).
     * @param temporada La nueva temporada.
     */
    public void setTemporada(String temporada) { this.temporada = temporada; }

    /**
     * Obtiene la fecha de fabricación del producto.
     * @return La fecha de fabricación.
     */
    public Date getFechaFabricacion() { return fechaFabricacion; }
    /**
     * Establece la fecha de fabricación del producto.
     * @param fechaFabricacion La nueva fecha de fabricación.
     */
    public void setFechaFabricacion(Date fechaFabricacion) { this.fechaFabricacion = fechaFabricacion; }
}