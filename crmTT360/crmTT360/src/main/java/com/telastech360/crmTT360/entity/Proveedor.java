package com.telastech360.crmTT360.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedor", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proveedor_id")
    private Long proveedorId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres")
    @Column(name = "direccion")
    private String direccion;

    @Pattern(regexp = "^\\+?[0-9\\s-]{10,15}$", message = "Teléfono inválido")
    @Column(name = "telefono", length = 15)
    private String telefono;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    // Constructores
    public Proveedor() {}

    public Proveedor(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y Setters
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    @Override
    public String toString() {
        return "Proveedor{" +
                "proveedorId=" + proveedorId +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}