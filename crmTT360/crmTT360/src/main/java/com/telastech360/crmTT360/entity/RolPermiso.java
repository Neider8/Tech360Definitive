package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rol_permiso")
public class RolPermiso {

    @EmbeddedId
    private RolPermisoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    // Corrección: Usar foreignKeyDefinition para ON DELETE CASCADE
    @JoinColumn(name = "rol_id", foreignKey = @ForeignKey(name = "fk_rolpermiso_rol", foreignKeyDefinition = "FOREIGN KEY (rol_id) REFERENCES rol(rol_id) ON DELETE CASCADE"))
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permisoId")
    // Corrección: Usar foreignKeyDefinition para ON DELETE CASCADE
    @JoinColumn(name = "permiso_id", foreignKey = @ForeignKey(name = "fk_rolpermiso_permiso", foreignKeyDefinition = "FOREIGN KEY (permiso_id) REFERENCES permiso(permiso_id) ON DELETE CASCADE"))
    private Permiso permiso;

    // Getters y Setters (sin cambios)
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