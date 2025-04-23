package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.BodegaDTO;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Usuario;
import org.springframework.stereotype.Component; // <<<<<<<<<< AÑADIR esta importación

@Component // <<<<<<<<<< AÑADIR esta anotación
public class BodegaMapper {

    // Asegura que los métodos no sean estáticos para que Spring pueda inyectar este Mapper
    public Bodega toEntity(BodegaDTO bodegaDTO) {
        if (bodegaDTO == null) {
            return null;
        }
        Bodega bodega = new Bodega();

        bodega.setNombre(bodegaDTO.getNombre());

        if (bodegaDTO.getTipoBodega() != null) {
            bodega.setTipoBodega(Bodega.TipoBodega.valueOf(bodegaDTO.getTipoBodega()));
        }

        bodega.setCapacidadMaxima(bodegaDTO.getCapacidadMaxima());

        bodega.setUbicacion(bodegaDTO.getUbicacion());

        // El mapeo de entidades relacionadas (Estado, Responsable) debe hacerse en el Servicio
        return bodega;
    }

    // Asegura que los métodos no sean estáticos para que Spring pueda inyectar este Mapper
    public BodegaDTO toDTO(Bodega bodega) {
        if (bodega == null) {
            return null;
        }
        BodegaDTO bodegaDTO = new BodegaDTO();

        // Si BodegaDTO tiene el campo ID y su setter, descomenta la línea
        // bodegaDTO.setBodegaId(bodega.getBodegaId());

        bodegaDTO.setNombre(bodega.getNombre());

        if (bodega.getTipoBodega() != null) {
            bodegaDTO.setTipoBodega(bodega.getTipoBodega().name());
        }

        bodegaDTO.setCapacidadMaxima(bodega.getCapacidadMaxima());

        bodegaDTO.setUbicacion(bodega.getUbicacion());

        // Asume que Bodega tiene getEstado(), que Estado tiene getEstadoId(), y BodegaDTO tiene setEstadoId()
        if (bodega.getEstado() != null) {
            bodegaDTO.setEstadoId(bodega.getEstado().getEstadoId());
        }

        /*
        if (bodega.getResponsable() != null) {
            bodegaDTO.setResponsableId(bodega.getResponsable().getUsuarioId()); // Asumiendo getUsuarioId() en Usuario
        }
        */
        return bodegaDTO;
    }
}