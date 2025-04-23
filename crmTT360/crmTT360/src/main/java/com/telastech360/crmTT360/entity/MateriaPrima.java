package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "materia_prima")
@DiscriminatorValue("MATERIA_PRIMA")
@PrimaryKeyJoinColumn(name = "item_id")
public class MateriaPrima extends Item {

    public enum TipoMaterial {
        TELA, HILO, BOTON, CIERRE, ETIQUETA, OTROS
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false, length = 20)
    @NotNull(message = "El tipo de material es obligatorio")
    private TipoMaterial tipoMaterial;

    @DecimalMin(value = "0.01", message = "El ancho debe ser positivo")
    @Column(name = "ancho_rollo", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal anchoRollo;

    @DecimalMin(value = "0.01", message = "El peso debe ser positivo")
    @Column(name = "peso_metro", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal pesoMetro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_tela_id")
    private Proveedor proveedorTela;

    // Constructor
    public MateriaPrima() {
        super();
        super.setTipoItem(TipoItem.MATERIA_PRIMA);
    }

    // Getters y Setters
    public TipoMaterial getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(TipoMaterial tipoMaterial) { this.tipoMaterial = tipoMaterial; }
    public BigDecimal getAnchoRollo() { return anchoRollo; }
    public void setAnchoRollo(BigDecimal anchoRollo) { this.anchoRollo = anchoRollo; }
    public BigDecimal getPesoMetro() { return pesoMetro; }
    public void setPesoMetro(BigDecimal pesoMetro) { this.pesoMetro = pesoMetro; }
    public Proveedor getProveedorTela() { return proveedorTela; }
    public void setProveedorTela(Proveedor proveedorTela) { this.proveedorTela = proveedorTela; }
}