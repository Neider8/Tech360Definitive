package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.NotNull;

// DTO para la entidad de relación RolPermiso
public class RolPermisoDTO {

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    @NotNull(message = "El ID del permiso es obligatorio")
    private Long permisoId;

    // Constructores, Getters y Setters

    public RolPermisoDTO() {
    }

    public RolPermisoDTO(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }
}