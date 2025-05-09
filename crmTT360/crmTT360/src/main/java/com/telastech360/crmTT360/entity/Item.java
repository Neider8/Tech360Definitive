package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "item", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo", name = "uk_item_codigo")
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_item", discriminatorType = DiscriminatorType.STRING)
public class Item {

    public enum TipoItem {
        MATERIA_PRIMA, PRODUCTO_TERMINADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no puede exceder 100 caracteres")
    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    // ----- CORRECCIÓN: Este es el campo correcto según tu schema V1 -----
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;
    // --------------------------------------------------------------------

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private Timestamp fechaIngreso = new Timestamp(System.currentTimeMillis());

    @Column(name = "fecha_vencimiento")
    private Date fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    @NotNull(message = "El proveedor es obligatorio")
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    @NotNull(message = "La bodega es obligatoria")
    private Bodega bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 20, insertable = false, updatable = false)
    @NotNull(message = "El tipo de item es obligatorio")
    private TipoItem tipoItem;

    public Item() {}

    public Item(String codigo, String nombre, String unidadMedida, BigDecimal precio,
                Integer stockDisponible, Integer stockMinimo, Estado estado,
                Proveedor proveedor, Categoria categoria, Bodega bodega, Usuario usuario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.stockMinimo = stockMinimo;
        this.estado = estado;
        this.proveedor = proveedor;
        this.categoria = categoria;
        this.bodega = bodega;
        this.usuario = usuario;
        this.fechaIngreso = new Timestamp(System.currentTimeMillis());
    }

    // --- Getters y Setters (sin cambios relevantes, solo asegurar que no haya refs a stock_actual) ---
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Bodega getBodega() { return bodega; }
    public void setBodega(Bodega bodega) { this.bodega = bodega; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoItem getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItem tipoItem) { this.tipoItem = tipoItem; }

    /**
     * Ajusta el stock disponible.
     * @param cantidad La cantidad a sumar (positivo) o restar (negativo).
     * @throws IllegalArgumentException Si el ajuste resultaría en stock negativo.
     */
    public void ajustarStock(int cantidad) {
        int nuevoStock = this.stockDisponible + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No se puede reducir más del stock disponible. Stock actual: " + this.stockDisponible + ", ajuste: " + cantidad);
        }
        this.stockDisponible = nuevoStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stockDisponible=" + stockDisponible +
                '}';
    }
}