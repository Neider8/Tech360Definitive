package com.telastech360.crmTT360.dto;

import com.telastech360.crmTT360.entity.Factura.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class FacturaDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser positivo")
    private BigDecimal total;

    @NotNull(message = "La fecha de la factura es obligatoria")
    private Timestamp fechaCreacion;

    // Constructores, Getters y Setters

    public FacturaDTO() {
    }

    public FacturaDTO(TipoMovimiento tipoMovimiento, BigDecimal total, Timestamp fechaCreacion) {
        this.tipoMovimiento = tipoMovimiento;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}