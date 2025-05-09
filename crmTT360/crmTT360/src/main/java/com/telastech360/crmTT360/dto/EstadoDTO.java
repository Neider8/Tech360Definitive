package com.telastech360.crmTT360.dto;

import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
// Importar JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty; // <<< Asegúrate de esta importación

/**
 * DTO (Data Transfer Object) para representar la información de un Estado.
 */
public class EstadoDTO {

    @NotNull(message = "El tipo de estado es obligatorio")
    @JsonProperty("tipoEstado") // <-- Verifica esta anotación y el nombre "tipoEstado"
    private TipoEstado tipoEstado;

    @NotBlank(message = "El valor no puede estar vacío")
    @Size(max = 50, message = "El valor no puede exceder los 50 caracteres")
    @JsonProperty("valor") // <-- Verifica esta anotación y el nombre "valor"
    private String valor;

    /**
     * Constructor público por defecto.
     * ¡OBLIGATORIO para Jackson!
     */
    public EstadoDTO() { // <---- ¡ESTE CONSTRUCTOR VACÍO ES ESENCIAL!
    }

    /**
     * Constructor con parámetros (opcional pero útil).
     * @param tipoEstado El tipo de estado.
     * @param valor El valor descriptivo del estado.
     */
    public EstadoDTO(TipoEstado tipoEstado, String valor) {
        this.tipoEstado = tipoEstado;
        this.valor = valor;
    }

    // Getters y Setters
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

    // --- Añade este método toString para depurar ---
    @Override
    public String toString() {
        return "EstadoDTO{" +
                "tipoEstado=" + tipoEstado +
                ", valor='" + valor + '\'' +
                '}';
    }
    // ----------------------------------------------
}