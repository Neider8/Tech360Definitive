// src/main/java/com/telastech360/crmTT360/dto/RolPermisoDTO.java
package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para representar la relación entre un Rol y un Permiso.
 * Se utiliza principalmente para identificar qué permiso se asigna o desasigna a qué rol.
 */
public class RolPermisoDTO {

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId; // ID del Rol

    @NotNull(message = "El ID del permiso es obligatorio")
    private Long permisoId; // ID del Permiso

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto.
     */
    public RolPermisoDTO() {
    }

    /**
     * Constructor con parámetros.
     * @param rolId ID del Rol.
     * @param permisoId ID del Permiso.
     */
    public RolPermisoDTO(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    /**
     * Obtiene el ID del Rol.
     * @return El ID del Rol.
     */
    public Long getRolId() {
        return rolId;
    }

    /**
     * Establece el ID del Rol.
     * @param rolId El nuevo ID del Rol.
     */
    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    /**
     * Obtiene el ID del Permiso.
     * @return El ID del Permiso.
     */
    public Long getPermisoId() {
        return permisoId;
    }

    /**
     * Establece el ID del Permiso.
     * @param permisoId El nuevo ID del Permiso.
     */
    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }
}