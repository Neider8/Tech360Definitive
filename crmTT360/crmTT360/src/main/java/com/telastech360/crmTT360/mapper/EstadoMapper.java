package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.entity.Estado;

public class EstadoMapper {

    public static EstadoDTO toDTO(Estado estado) {
        EstadoDTO dto = new EstadoDTO();
        dto.setTipoEstado(estado.getTipoEstado());
        dto.setValor(estado.getValor());
        return dto;
    }

    public static Estado toEntity(EstadoDTO dto) {
        Estado estado = new Estado();
        estado.setTipoEstado(dto.getTipoEstado());
        estado.setValor(dto.getValor());
        return estado;
    }

    public static void updateEntityFromDTO(EstadoDTO dto, Estado estado) {
        estado.setTipoEstado(dto.getTipoEstado());
        estado.setValor(dto.getValor());
    }
}