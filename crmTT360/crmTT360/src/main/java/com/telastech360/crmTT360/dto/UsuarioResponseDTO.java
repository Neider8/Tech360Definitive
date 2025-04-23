package com.telastech360.crmTT360.dto;

// No se importa java.sql.Date ya que fechaRegistro no está en la entidad Usuario actual.

// DTO para respuestas de Usuario
public class UsuarioResponseDTO {

    private Long usuarioId;

    private String nombre;

    private String email;

    private Long rolId; // Solo el ID del rol para simplificar

    // Se remueve fechaRegistro
    // private Date fechaRegistro;

    private String estado; // Corregido: Tipo String y nombre 'estado'

    // Constructores, Getters y Setters

    public UsuarioResponseDTO() {
    }

    // Constructor ajustado
    public UsuarioResponseDTO(Long usuarioId, String nombre, String email, Long rolId, String estado) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.email = email;
        this.rolId = rolId;
        this.estado = estado;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    // Getters y Setters corregidos para 'estado'
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Se remueven Getters y Setters de fechaRegistro
    /*
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    */
}