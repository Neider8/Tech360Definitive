package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

// DTO (Data Transfer Object) para la entidad Producto.
// Incluye campos heredados de Item y campos específicos de Producto.
public class ProductoDTO {

    private Long itemId; // Incluir ID para respuestas y operaciones de actualización

    // Campos heredados de Item (seleccionados para el DTO)
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no puede exceder 100 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private String descripcion; // La descripción puede ser opcional

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

    private Integer stockMaximo; // El stock máximo puede ser opcional

    private Date fechaVencimiento; // La fecha de vencimiento puede ser opcional


    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId; // Proveedor general del Item

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    // Campos específicos de Producto
    @NotNull(message = "El tipo de prenda es obligatorio")
    private String tipoPrenda; // Representación en String del enum TipoPrenda

    @NotNull(message = "La talla es obligatoria")
    private String talla; // Representación en String del enum Talla

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    private String color;

    private String temporada; // La temporada puede ser opcional

    @NotNull(message = "La fecha de fabricación es obligatoria")
    private Date fechaFabricacion;

    // --- Constructores, Getters y Setters ---
    // (Se generan automáticamente o se añaden aquí)

    public ProductoDTO() {
    }

    // Getters y Setters (omitidos aquí para brevedad, deben ser generados)

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getBodegaId() {
        return bodegaId;
    }

    public void setBodegaId(Long bodegaId) {
        this.bodegaId = bodegaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(String tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public Date getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(Date fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }
}