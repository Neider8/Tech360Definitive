package com.telastech360.crmTT360.dto;

/**
 * DTO (Data Transfer Object) para representar la información de un Usuario en las respuestas de la API.
 * Excluye información sensible como la contraseña.
 * Contiene los datos básicos y el ID del rol asignado.
 */
public class UsuarioResponseDTO {

    private Long usuarioId; // ID único del usuario

    private String nombre; // Nombre del usuario

    private String email; // Email del usuario (usado como username)

    private Long rolId; // ID del Rol asignado

    private String estado; // Estado actual del usuario (ej. "ACTIVO", "INACTIVO")

    // Constructores, Getters y Setters

    /**
     * Constructor por defecto. Requerido para frameworks como Jackson.
     */
    public UsuarioResponseDTO() {
    }

    /**
     * Constructor con todos los parámetros para facilitar la creación de instancias.
     * @param usuarioId ID único del usuario.
     * @param nombre Nombre completo del usuario.
     * @param email Dirección de correo electrónico del usuario (login).
     * @param rolId ID del rol {@link com.telastech360.crmTT360.entity.Rol} asignado al usuario.
     * @param estado Estado actual del usuario (e.g., "ACTIVO", "INACTIVO").
     */
    public UsuarioResponseDTO(Long usuarioId, String nombre, String email, Long rolId, String estado) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.email = email;
        this.rolId = rolId;
        this.estado = estado;
    }

    // Getters y Setters con Javadoc

    /**
     * Obtiene el ID único del usuario.
     * @return El ID del usuario.
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID único del usuario.
     * @param usuarioId El nuevo ID del usuario.
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el nombre completo del usuario.
     * @return El nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del usuario.
     * @param nombre El nuevo nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección de correo electrónico del usuario.
     * @return El email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece la dirección de correo electrónico del usuario.
     * @param email El nuevo email del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el ID del rol asignado al usuario.
     * @return El ID del rol.
     */
    public Long getRolId() {
        return rolId;
    }

    /**
     * Establece el ID del rol asignado al usuario.
     * @param rolId El nuevo ID del rol.
     */
    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    /**
     * Obtiene el estado actual del usuario (ej. "ACTIVO").
     * @return El estado del usuario como String.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual del usuario.
     * @param estado El nuevo estado (ej. "ACTIVO", "INACTIVO").
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}