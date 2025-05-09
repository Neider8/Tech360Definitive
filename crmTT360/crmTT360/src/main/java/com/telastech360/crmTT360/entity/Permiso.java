    package com.telastech360.crmTT360.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;

    import java.util.HashSet;
    import java.util.Set;

    @Entity
    @Table(name = "permiso", uniqueConstraints = {
            @UniqueConstraint(columnNames = "nombre")
    })
    public class Permiso {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "permiso_id")
        private Long permisoId;

        @NotBlank(message = "El nombre del permiso es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
        @Column(name = "nombre", nullable = false)
        private String nombre;

        @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
        @Column(name = "descripcion")
        private String descripcion;

        @ManyToMany(mappedBy = "permisos")
        private Set<Rol> roles = new HashSet<>();

        // Getters y Setters
        public Long getPermisoId() {
            return permisoId;
        }

        public void setPermisoId(Long permisoId) {
            this.permisoId = permisoId;
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

        public Set<Rol> getRoles() {
            return roles;
        }

        public void setRoles(Set<Rol> roles) {
            this.roles = roles;
        }
    }