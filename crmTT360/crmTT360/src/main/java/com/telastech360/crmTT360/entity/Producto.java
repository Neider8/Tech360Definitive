package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
@PrimaryKeyJoinColumn(name = "item_id")
@DiscriminatorValue("PRODUCTO_TERMINADO")
public class Producto extends Item {

    public enum TipoPrenda {
        CAMISA, PANTALON, VESTIDO, CHAQUETA, OTROS
    }

    public enum Talla {
        XS, S, M, L, XL, XXL, UNICA
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_prenda", nullable = false, length = 20)
    @NotNull(message = "El tipo de prenda es obligatorio")
    private TipoPrenda tipoPrenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "talla", nullable = false, length = 10)
    @NotNull(message = "La talla es obligatoria")
    private Talla talla;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "temporada", length = 50)
    private String temporada;

    @NotBlank(message = "La composición es obligatoria")
    @Size(max = 100, message = "La composición no puede exceder 100 caracteres")
    @Column(name = "composicion", nullable = false)
    private String composicion;

    @Column(name = "fecha_fabricacion", nullable = false)
    @NotNull(message = "La fecha de fabricación es obligatoria")
    private Date fechaFabricacion;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> pedidoDetalles = new ArrayList<>();

    // Constructor vacío
    public Producto() {
        super();
        this.setTipoItem(Item.TipoItem.PRODUCTO_TERMINADO);
    }

    // Constructor completo
    public Producto(String codigo, String nombre, String unidadMedida,
                    BigDecimal precio, Integer stockMinimo, Integer stockMaximo,
                    Bodega bodega, Usuario usuario, TipoPrenda tipoPrenda,
                    Talla talla, String color, String composicion, Date fechaFabricacion) {
        super();
        this.setCodigo(codigo);
        this.setNombre(nombre);
        this.setUnidadMedida(unidadMedida);
        this.setPrecio(precio);
        this.setStockMinimo(stockMinimo);
        this.setStockMaximo(stockMaximo);
        this.setStockDisponible(stockMinimo);
        this.setBodega(bodega);
        this.setUsuario(usuario);
        this.tipoPrenda = tipoPrenda;
        this.talla = talla;
        this.color = color;
        this.composicion = composicion;
        this.fechaFabricacion = fechaFabricacion;
        this.setTipoItem(Item.TipoItem.PRODUCTO_TERMINADO);
    }

    // Getters y setters
    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
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

    public String getComposicion() {
        return composicion;
    }

    public void setComposicion(String composicion) {
        this.composicion = composicion;
    }

    public Date getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(Date fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }

    public List<PedidoDetalle> getPedidoDetalles() {
        return pedidoDetalles;
    }

    public void setPedidoDetalles(List<PedidoDetalle> pedidoDetalles) {
        this.pedidoDetalles = pedidoDetalles;
    }
}
