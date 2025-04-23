package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre")
})
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long rolId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    @Column(name = "descripcion")
    private String descripcion;

    @ManyToMany
    @JoinTable(
            name = "rol_permiso",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    // Getters y Setters
    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    // Métodos para agregar y remover permisos
    public void addPermiso(Permiso permiso) {
        this.permisos.add(permiso);
        permiso.getRoles().add(this); // Asegura la relación bidireccional
    }

    public void removePermiso(Permiso permiso) {
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this); // Asegura la relación bidireccional
    }
}