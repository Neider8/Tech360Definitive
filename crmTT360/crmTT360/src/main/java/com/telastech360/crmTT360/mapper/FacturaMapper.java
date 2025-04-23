package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.entity.Factura;

public class FacturaMapper {

    public static FacturaDTO toDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setTipoMovimiento(factura.getTipoMovimiento());
        dto.setTotal(factura.getTotal());
        dto.setFechaCreacion(factura.getFechaCreacion());
        return dto;
    }

    public static Factura toEntity(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setTipoMovimiento(dto.getTipoMovimiento());
        factura.setTotal(dto.getTotal());
        factura.setFechaCreacion(dto.getFechaCreacion());
        return factura;
    }

    public static void updateEntityFromDTO(FacturaDTO dto, Factura factura) {
        factura.setTipoMovimiento(dto.getTipoMovimiento());
        factura.setTotal(dto.getTotal());
        factura.setFechaCreacion(dto.getFechaCreacion());
    }
}