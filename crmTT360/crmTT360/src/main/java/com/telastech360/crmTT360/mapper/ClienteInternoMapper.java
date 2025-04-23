package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ClienteInternoDTO;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Usuario; // Asegúrate de importar Usuario si lo necesitas para el responsable

import org.springframework.stereotype.Component; // <<<<<<<<<< AÑADIR esta importación

@Component // <<<<<<<<<< AÑADIR esta anotación
public class ClienteInternoMapper {

    // Asegura que los métodos no sean estáticos para que Spring pueda inyectar este Mapper
    public ClienteInterno toEntity(ClienteInternoDTO clienteInternoDTO) {
        if (clienteInternoDTO == null) {
            return null;
        }
        ClienteInterno clienteInterno = new ClienteInterno();
        // No establecer el ID aquí si es generado por la DB

        clienteInterno.setCodigoInterno(clienteInternoDTO.getCodigoInterno());
        clienteInterno.setNombre(clienteInternoDTO.getNombre());

        // Asumiendo que tipo es un enum y el DTO envía el nombre del enum como String
        if (clienteInternoDTO.getTipo() != null) {
            clienteInterno.setTipo(ClienteInterno.TipoCliente.valueOf(clienteInternoDTO.getTipo()));
        }

        clienteInterno.setUbicacion(clienteInternoDTO.getUbicacion());
        clienteInterno.setPresupuestoAnual(clienteInternoDTO.getPresupuestoAnual());

        // El mapeo del responsable (Usuario) por ID (ResponsableId en DTO) DEBE hacerse en el Servicio
        // buscando el Usuario en su repositorio y asignándolo a la entidad ClienteInterno.
        // Aquí solo usamos el ResponsableId del DTO si lo necesitas para alguna lógica temporal.
        // Long responsableId = clienteInternoDTO.getResponsableId(); // Getter de ClienteInternoDTO

        return clienteInterno;
    }

    // Asegura que los métodos no sean estáticos para que Spring pueda inyectar este Mapper
    public ClienteInternoDTO toDTO(ClienteInterno clienteInterno) {
        if (clienteInterno == null) {
            return null;
        }
        ClienteInternoDTO clienteInternoDTO = new ClienteInternoDTO();
        // Asumiendo que ClienteInterno entity tiene un getClienteInternoId() y ClienteInternoDTO un setClienteInternoId()
        // clienteInternoDTO.setClienteInternoId(clienteInterno.getClienteInternoId()); // Si ClienteInternoDTO tiene el campo ID

        clienteInternoDTO.setCodigoInterno(clienteInterno.getCodigoInterno());
        clienteInternoDTO.setNombre(clienteInterno.getNombre());

        // Asumiendo que tipo es un enum en la entidad
        if (clienteInterno.getTipo() != null) {
            clienteInternoDTO.setTipo(clienteInterno.getTipo().name()); // Convertir enum a String
        }

        clienteInternoDTO.setUbicacion(clienteInterno.getUbicacion());
        clienteInternoDTO.setPresupuestoAnual(clienteInterno.getPresupuestoAnual());

        // Asumiendo que ClienteInterno entity tiene un getResponsable() (de tipo Usuario) y Usuario tiene getUsuarioId()
        // Si ClienteInternoDTO tiene un campo responsableId y su setter setResponsableId(), lo mapeamos.
        // Según el código de ClienteInternoDTO en Errores.txt, SÍ tiene setResponsableId().
        if (clienteInterno.getResponsable() != null) {
            clienteInternoDTO.setResponsableId(clienteInterno.getResponsable().getUsuarioId());
        }

        return clienteInternoDTO;
    }
}