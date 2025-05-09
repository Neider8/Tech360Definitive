// src/main/java/com/telastech360/crmTT360/mapper/MateriaPrimaMapper.java
package com.telastech360.crmTT360.mapper;

import com.telastech360.crmTT360.dto.MateriaPrimaDTO;
import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.entity.MateriaPrima.TipoMaterial; // Importar el enum TipoMaterial
import com.telastech360.crmTT360.exception.InvalidDataException; // Importar InvalidDataException
import org.springframework.stereotype.Component;

/**
 * Componente Mapper responsable de convertir entre entidades {@link MateriaPrima}
 * y sus correspondientes DTOs ({@link MateriaPrimaDTO}).
 * Maneja tanto los campos comunes heredados de Item como los específicos de MateriaPrima.
 */
@Component
public class MateriaPrimaMapper {

    /**
     * Convierte una entidad {@link MateriaPrima} a un {@link MateriaPrimaDTO}.
     * Incluye campos base de Item y campos específicos de MateriaPrima.
     * Mapea IDs de entidades relacionadas.
     *
     * @param materiaPrima La entidad MateriaPrima a convertir. Si es null, retorna null.
     * @return El DTO {@link MateriaPrimaDTO} poblado, o null si la entrada fue null.
     */
    public MateriaPrimaDTO toDTO(MateriaPrima materiaPrima) {
        if (materiaPrima == null) {
            return null;
        }
        MateriaPrimaDTO dto = new MateriaPrimaDTO();

        // --- Mapeo de atributos heredados de Item ---
        dto.setItemId(materiaPrima.getItemId()); // Incluir ID base
        dto.setCodigo(materiaPrima.getCodigo());
        dto.setNombre(materiaPrima.getNombre());
        dto.setDescripcion(materiaPrima.getDescripcion());
        dto.setUnidadMedida(materiaPrima.getUnidadMedida());
        dto.setPrecio(materiaPrima.getPrecio());
        dto.setStockDisponible(materiaPrima.getStockDisponible());
        dto.setStockMinimo(materiaPrima.getStockMinimo());
        dto.setStockMaximo(materiaPrima.getStockMaximo());
        dto.setFechaVencimiento(materiaPrima.getFechaVencimiento());

        // Mapeo de IDs de relaciones heredadas
        if (materiaPrima.getEstado() != null) {
            dto.setEstadoId(materiaPrima.getEstado().getEstadoId());
        }
        if (materiaPrima.getProveedor() != null) { // Proveedor general
            dto.setProveedorId(materiaPrima.getProveedor().getProveedorId());
        }
        if (materiaPrima.getCategoria() != null) {
            dto.setCategoriaId(materiaPrima.getCategoria().getCategoriaId());
        }
        if (materiaPrima.getBodega() != null) {
            dto.setBodegaId(materiaPrima.getBodega().getBodegaId());
        }
        if (materiaPrima.getUsuario() != null) {
            dto.setUsuarioId(materiaPrima.getUsuario().getUsuarioId());
        }

        // --- Mapeo de atributos específicos de MateriaPrima ---
        // Mapeo del enum TipoMaterial a String
        if (materiaPrima.getTipoMaterial() != null) {
            dto.setTipoMaterial(materiaPrima.getTipoMaterial().name());
        } else {
            dto.setTipoMaterial(null);
        }

        dto.setAnchoRollo(materiaPrima.getAnchoRollo());
        dto.setPesoMetro(materiaPrima.getPesoMetro());

        // Mapeo del ID de proveedor específico de tela
        if (materiaPrima.getProveedorTela() != null) {
            dto.setProveedorTelaId(materiaPrima.getProveedorTela().getProveedorId());
        } else {
            dto.setProveedorTelaId(null);
        }

        // El TipoItem base ("MATERIA_PRIMA") no se incluye explícitamente en el DTO específico

        return dto;
    }

    /**
     * Convierte un {@link MateriaPrimaDTO} a una entidad {@link MateriaPrima}.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.,
     * incluyendo el Proveedor de Tela si se especifica) ya cargadas. Esta carga debe hacerse en el servicio.
     * Valida el valor de `tipoMaterial`.
     *
     * @param dto El DTO con los datos de entrada. Si es null, retorna null.
     * @param bodega La entidad {@link Bodega} asociada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada (ya cargada).
     * @param estado La entidad {@link Estado} asociada (ya cargada).
     * @param proveedorGeneral La entidad {@link Proveedor} general asociada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada (ya cargada).
     * @param proveedorTela La entidad {@link Proveedor} específica para tela (puede ser null si no aplica, ya cargada si se proporcionó ID).
     * @return Una entidad MateriaPrima poblada, o null si el DTO fue null.
     * @throws InvalidDataException si el valor de `tipoMaterial` en el DTO no es válido.
     * @throws NullPointerException si alguna de las entidades relacionadas obligatorias (no proveedorTela) es null.
     */
    public MateriaPrima toEntity(MateriaPrimaDTO dto, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedorGeneral, Usuario usuario, Proveedor proveedorTela) {
        if (dto == null) {
            return null;
        }
        // Validar relaciones base obligatorias
        if (bodega == null || categoria == null || estado == null || proveedorGeneral == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas base (Bodega, Categoria, Estado, Proveedor General, Usuario) no pueden ser null al mapear MateriaPrimaDTO a Entidad.");
        }

        MateriaPrima materiaPrima = new MateriaPrima();
        // El ID base (itemId) se genera en la BD

        // --- Mapeo de atributos heredados de Item ---
        materiaPrima.setCodigo(dto.getCodigo());
        materiaPrima.setNombre(dto.getNombre());
        materiaPrima.setDescripcion(dto.getDescripcion());
        materiaPrima.setUnidadMedida(dto.getUnidadMedida());
        materiaPrima.setPrecio(dto.getPrecio());
        materiaPrima.setStockDisponible(dto.getStockDisponible());
        materiaPrima.setStockMinimo(dto.getStockMinimo());
        materiaPrima.setStockMaximo(dto.getStockMaximo());
        materiaPrima.setFechaVencimiento(dto.getFechaVencimiento());

        // Establecer relaciones base con las entidades cargadas
        materiaPrima.setBodega(bodega);
        materiaPrima.setCategoria(categoria);
        materiaPrima.setEstado(estado);
        materiaPrima.setProveedor(proveedorGeneral); // Proveedor general
        materiaPrima.setUsuario(usuario);

        // --- Mapeo de atributos específicos de MateriaPrima ---
        // Mapeo del String a enum TipoMaterial con validación
        if (dto.getTipoMaterial() != null && !dto.getTipoMaterial().isEmpty()) {
            try {
                materiaPrima.setTipoMaterial(TipoMaterial.valueOf(dto.getTipoMaterial().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de material inválido: " + dto.getTipoMaterial());
            }
        } else {
            // Lanzar excepción si es obligatorio según la lógica de negocio
            throw new InvalidDataException("El tipo de material es obligatorio.");
        }

        materiaPrima.setAnchoRollo(dto.getAnchoRollo());
        materiaPrima.setPesoMetro(dto.getPesoMetro());

        // Establecer relación con proveedor específico de tela (puede ser null)
        materiaPrima.setProveedorTela(proveedorTela);

        // El tipo de item (MATERIA_PRIMA) se establece en el constructor de MateriaPrima
        // La fecha de ingreso se establece automáticamente

        return materiaPrima;
    }

    /**
     * Actualiza una entidad {@link MateriaPrima} existente a partir de un {@link MateriaPrimaDTO}.
     * <strong>Importante:</strong> Requiere las entidades relacionadas (Bodega, Categoria, etc.,
     * incluyendo el Proveedor de Tela si se especifica) ya cargadas. Esta carga debe hacerse en el servicio.
     * Valida el valor de `tipoMaterial`.
     *
     * @param dto El DTO {@link MateriaPrimaDTO} con los datos actualizados.
     * @param materiaPrima La entidad {@link MateriaPrima} a actualizar.
     * @param bodega La entidad {@link Bodega} asociada actualizada (ya cargada).
     * @param categoria La entidad {@link Categoria} asociada actualizada (ya cargada).
     * @param estado La entidad {@link Estado} asociada actualizada (ya cargada).
     * @param proveedorGeneral La entidad {@link Proveedor} general asociada actualizada (ya cargada).
     * @param usuario La entidad {@link Usuario} asociada actualizada (ya cargada).
     * @param proveedorTela La entidad {@link Proveedor} específica para tela actualizada (puede ser null, ya cargada si se proporcionó ID).
     * @throws InvalidDataException si el valor de `tipoMaterial` en el DTO no es válido.
     * @throws NullPointerException si alguna de las entidades relacionadas obligatorias (no proveedorTela) o el DTO/entidad son null.
     */
    public void updateEntityFromDTO(MateriaPrimaDTO dto, MateriaPrima materiaPrima, Bodega bodega, Categoria categoria, Estado estado, Proveedor proveedorGeneral, Usuario usuario, Proveedor proveedorTela) {
        if (dto == null || materiaPrima == null) {
            return; // No hacer nada
        }
        // Validar relaciones base obligatorias
        if (bodega == null || categoria == null || estado == null || proveedorGeneral == null || usuario == null) {
            throw new NullPointerException("Las entidades relacionadas base (Bodega, Categoria, Estado, Proveedor General, Usuario) no pueden ser null al actualizar MateriaPrima.");
        }

        // --- Actualizar atributos heredados de Item ---
        materiaPrima.setCodigo(dto.getCodigo());
        materiaPrima.setNombre(dto.getNombre());
        materiaPrima.setDescripcion(dto.getDescripcion());
        materiaPrima.setUnidadMedida(dto.getUnidadMedida());
        materiaPrima.setPrecio(dto.getPrecio());
        materiaPrima.setStockDisponible(dto.getStockDisponible());
        materiaPrima.setStockMinimo(dto.getStockMinimo());
        materiaPrima.setStockMaximo(dto.getStockMaximo());
        materiaPrima.setFechaVencimiento(dto.getFechaVencimiento());

        // Actualizar relaciones base
        materiaPrima.setBodega(bodega);
        materiaPrima.setCategoria(categoria);
        materiaPrima.setEstado(estado);
        materiaPrima.setProveedor(proveedorGeneral); // Proveedor general
        materiaPrima.setUsuario(usuario);

        // --- Actualizar atributos específicos de MateriaPrima ---
        // Actualizar el enum TipoMaterial con validación
        if (dto.getTipoMaterial() != null && !dto.getTipoMaterial().isEmpty()) {
            try {
                materiaPrima.setTipoMaterial(TipoMaterial.valueOf(dto.getTipoMaterial().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Tipo de material inválido: " + dto.getTipoMaterial());
            }
        } else {
            throw new InvalidDataException("El tipo de material es obligatorio durante la actualización.");
        }

        materiaPrima.setAnchoRollo(dto.getAnchoRollo());
        materiaPrima.setPesoMetro(dto.getPesoMetro());

        // Actualizar relación con proveedor específico de tela (puede ser null)
        materiaPrima.setProveedorTela(proveedorTela);

        // La fecha de ingreso y el ID no se actualizan
    }
}