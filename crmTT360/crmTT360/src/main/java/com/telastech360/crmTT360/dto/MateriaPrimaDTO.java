package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

// DTO (Data Transfer Object) para la entidad MateriaPrima.
// Incluye campos heredados de Item y campos específicos de MateriaPrima.
public class MateriaPrimaDTO {

    // Campos heredados de Item (seleccionados para el DTO)
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

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId; // Proveedor general del Item

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    // Campos específicos de MateriaPrima
    @NotNull(message = "El tipo de material es obligatorio")
    private String tipoMaterial; // Representación en String del enum TipoMaterial

    @Positive(message = "El ancho del rollo debe ser positivo")
    private BigDecimal anchoRollo;

    @Positive(message = "El peso por metro debe ser positivo")
    private BigDecimal pesoMetro;

    private Long proveedorTelaId;

    // --- Constructores, Getters y Setters ---
    // (Se generan automáticamente o se añaden aquí)

    public MateriaPrimaDTO() {
    }

    // Getters y Setters (omitidos aquí para brevedad, deben ser generados)

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

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public BigDecimal getAnchoRollo() {
        return anchoRollo;
    }

    public void setAnchoRollo(BigDecimal anchoRollo) {
        this.anchoRollo = anchoRollo;
    }

    public BigDecimal getPesoMetro() {
        return pesoMetro;
    }

    public void setPesoMetro(BigDecimal pesoMetro) {
        this.pesoMetro = pesoMetro;
    }

    public Long getProveedorTelaId() {
        return proveedorTelaId;
    }

    public void setProveedorTelaId(Long proveedorTelaId) {
        this.proveedorTelaId = proveedorTelaId;
    }
}