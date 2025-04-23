package com.telastech360.crmTT360.dto;

import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EstadoDTO {

    @NotNull(message = "El tipo de estado es obligatorio")
    private TipoEstado tipoEstado;

    @NotBlank(message = "El valor no puede estar vacío")
    @Size(max = 50, message = "El valor no puede exceder los 50 caracteres")
    private String valor;

    public EstadoDTO() {
    }

    public EstadoDTO(TipoEstado tipoEstado, String valor) {
        this.tipoEstado = tipoEstado;
        this.valor = valor;
    }

    public TipoEstado getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(TipoEstado tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}