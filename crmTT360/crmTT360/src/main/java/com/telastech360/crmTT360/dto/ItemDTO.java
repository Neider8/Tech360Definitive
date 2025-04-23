package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;

// DTO (Data Transfer Object) para la entidad Item.
// Utilizado para la transferencia de datos entre la capa web (controlador) y la capa de servicio,
// exponiendo solo los campos necesarios y aplicando validaciones.
public class ItemDTO {

    // Atributos del DTO con validaciones
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
    private BigDecimal precio; // Atributo 'precio' según tu entidad Item

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible; // Atributo 'stockDisponible' según tu entidad Item

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    private Integer stockMaximo; // El stock máximo puede ser opcional

    private Date fechaVencimiento; // La fecha de vencimiento puede ser opcional


    @NotNull(message = "El estado es obligatorio")
    private Long estadoId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El tipo de item es obligatorio")
    private String tipoItem; // Representación en String del enum TipoItem

    // --- Constructores ---
    public ItemDTO() {
    }

    // Constructor con campos obligatorios para facilitar la creación
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
    // (Se generan automáticamente o se añaden aquí)

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

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }
}