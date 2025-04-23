package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "bodega", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre", name = "uk_bodega_nombre")
})
public class Bodega {

    public enum TipoBodega {
        MATERIA_PRIMA, PRODUCTO_TERMINADO, TEMPORAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bodega_id")
    private Long bodegaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_bodega", nullable = false, length = 20)
    @NotNull(message = "El tipo de bodega es obligatorio")
    private TipoBodega tipoBodega;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private Estado estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Timestamp fechaCreacion = new Timestamp(System.currentTimeMillis());

    @OneToMany(mappedBy = "bodega", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    // Getters y Setters (completos)
    public Long getBodegaId() { return bodegaId; }
    public void setBodegaId(Long bodegaId) { this.bodegaId = bodegaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoBodega getTipoBodega() { return tipoBodega; }
    public void setTipoBodega(TipoBodega tipoBodega) { this.tipoBodega = tipoBodega; }
    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Usuario getResponsable() { return responsable; }
    public void setResponsable(Usuario responsable) { this.responsable = responsable; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}