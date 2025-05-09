// src/main/java/com/telastech360/crmTT360/mapper/ClienteInternoMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.ClienteInternoDTO;
import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Usuario; // Necesario si se mapean relaciones completas
import com.telastech360.crmTT360.exception.InvalidDataException; // Para manejar errores de enum

import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link ClienteInterno}
 * y sus correspondientes DTOs ({@link ClienteInternoDTO}).
 */
@Component
public class ClienteInternoMapper {

    /**
     * Convierte un {@link ClienteInternoDTO} a una entidad {@link ClienteInterno} básica.
     * <strong>Importante:</strong> No asigna la entidad Usuario responsable.
     * La lógica de buscar el Usuario por el responsableId del DTO debe realizarse en el servicio.
     *
     * @param clienteInternoDTO El DTO con los datos de entrada. Si es null, retorna null.
     * @return Una entidad ClienteInterno parcialmente poblada (sin el objeto Responsable),
     * o null si la entrada fue null.
     * @throws InvalidDataException si el valor de tipo en el DTO no es válido.
     */
    public ClienteInterno toEntity(ClienteInternoDTO clienteInternoDTO) {
        if (clienteInternoDTO == null) {
            return null;
        }
        ClienteInterno clienteInterno = new ClienteInterno();
        // No establecer el ID aquí si es generado por la DB

        clienteInterno.setCodigoInterno(clienteInternoDTO.getCodigoInterno());
        clienteInterno.setNombre(clienteInternoDTO.getNombre());

        // Mapea el String del DTO al Enum de la entidad con validación
        if (clienteInternoDTO.getTipo() != null) {
            try {
                clienteInterno.setTipo(ClienteInterno.TipoCliente.valueOf(clienteInternoDTO.getTipo().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de cliente inválido: " + clienteInternoDTO.getTipo() +
                        ". Valores permitidos: INTERNO, EXTERNO");
            }
        } // Si es null, requiere validación @NotNull en DTO

        clienteInterno.setUbicacion(clienteInternoDTO.getUbicacion());
        clienteInterno.setPresupuestoAnual(clienteInternoDTO.getPresupuestoAnual());

        // El mapeo del responsable (Usuario) por ID (ResponsableId en DTO) DEBE hacerse en el Servicio.
        // Aquí no se asigna clienteInterno.setResponsable(usuarioEncontrado);

        // La fecha de registro se establece automáticamente en la entidad

        return clienteInterno;
    }

    /**
     * Convierte una entidad {@link ClienteInterno} a un {@link ClienteInternoDTO}.
     * Mapea el ID del Usuario responsable si existe.
     *
     * @param clienteInterno La entidad ClienteInterno a convertir. Si es null, retorna null.
     * @return El DTO {@link ClienteInternoDTO} poblado, o null si la entrada fue null.
     */
    public ClienteInternoDTO toDTO(ClienteInterno clienteInterno) {
        if (clienteInterno == null) {
            return null;
        }
        ClienteInternoDTO clienteInternoDTO = new ClienteInternoDTO();
        // Considera añadir el ID del cliente al DTO si es necesario
        // clienteInternoDTO.setClienteId(clienteInterno.getClienteId()); // Asumiendo getters/setters

        clienteInternoDTO.setCodigoInterno(clienteInterno.getCodigoInterno());
        clienteInternoDTO.setNombre(clienteInterno.getNombre());

        // Mapea el Enum de la entidad al String del DTO
        if (clienteInterno.getTipo() != null) {
            clienteInternoDTO.setTipo(clienteInterno.getTipo().name()); // .name() devuelve el nombre del enum como String
        }

        clienteInternoDTO.setUbicacion(clienteInterno.getUbicacion());
        clienteInternoDTO.setPresupuestoAnual(clienteInterno.getPresupuestoAnual());

        // Mapea el ID del responsable si existe
        if (clienteInterno.getResponsable() != null) {
            clienteInternoDTO.setResponsableId(clienteInterno.getResponsable().getUsuarioId());
        } else {
            clienteInternoDTO.setResponsableId(null);
        }

        // La fecha de registro generalmente no se incluye en el DTO

        return clienteInternoDTO;
    }

    /**
     * Actualiza los campos básicos de una entidad {@link ClienteInterno} existente desde un {@link ClienteInternoDTO}.
     * <strong>Importante:</strong> No actualiza la relación con el Usuario responsable.
     * Esta lógica debe realizarse en el servicio.
     *
     * @param dto El DTO {@link ClienteInternoDTO} con los datos actualizados.
     * @param cliente La entidad {@link ClienteInterno} a actualizar. No realiza ninguna acción si alguno es null.
     * @throws InvalidDataException si el valor de tipo en el DTO no es válido.
     */
    public void updateEntityFromDTO(ClienteInternoDTO dto, ClienteInterno cliente) {
        if (dto == null || cliente == null) {
            return;
        }

        // Actualiza campos directos
        cliente.setCodigoInterno(dto.getCodigoInterno());
        cliente.setNombre(dto.getNombre());
        cliente.setUbicacion(dto.getUbicacion());
        cliente.setPresupuestoAnual(dto.getPresupuestoAnual());

        // Actualiza el tipo con validación
        if (dto.getTipo() != null) {
            try {
                cliente.setTipo(ClienteInterno.TipoCliente.valueOf(dto.getTipo().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de cliente inválido: " + dto.getTipo() +
                        ". Valores permitidos: INTERNO, EXTERNO");
            }
        }

        // La actualización del responsable se hace en el servicio
    }
}