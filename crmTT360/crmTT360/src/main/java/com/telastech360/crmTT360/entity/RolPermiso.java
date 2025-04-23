package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rol_permiso")
public class RolPermiso {

    @EmbeddedId
    private RolPermisoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id", foreignKey = @ForeignKey(foreignKeyDefinition = "ON DELETE CASCADE"))
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permisoId")
    @JoinColumn(name = "permiso_id", foreignKey = @ForeignKey(foreignKeyDefinition = "ON DELETE CASCADE"))
    private Permiso permiso;

    // Getters y Setters
    public RolPermisoId getId() {
        return id;
    }

    public void setId(RolPermisoId id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }
}