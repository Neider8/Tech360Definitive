package com.telastech360.crmTT360.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para solicitudes de creación o actualización de {@link com.telastech360.crmTT360.entity.Usuario}.
 * Contiene los datos necesarios y las validaciones de entrada asociadas.
 * La contraseña es opcional en la actualización (si no se envía, no se modifica en el servicio).
 */
public class UsuarioRequestDTO {

    /**
     * Nombre completo del usuario.
     * Es obligatorio y no puede exceder los 255 caracteres.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    /**
     * Dirección de correo electrónico del usuario.
     * Se utiliza como identificador único (username) para el login.
     * Debe ser un formato de email válido, es obligatorio y no puede exceder los 255 caracteres.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres")
    private String email;

    /**
     * Contraseña del usuario (en texto plano).
     * Es opcional durante la actualización. Si se proporciona, debe tener entre 8 y 255 caracteres.
     * Es obligatoria durante la creación (validado en el servicio).
     * El servicio se encargará de codificarla antes de guardarla.
     */
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;

    /**
     * ID del {@link com.telastech360.crmTT360.entity.Rol} que se asignará al usuario.
     * Es obligatorio tanto en la creación como en la actualización.
     */
    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    // Constructores, Getters y Setters con Javadoc

    /**
     * Constructor por defecto. Requerido para frameworks como Jackson.
     */
    public UsuarioRequestDTO() {
    }

    /**
     * Constructor con parámetros para facilitar la creación de instancias, especialmente en pruebas.
     * @param nombre Nombre completo del usuario.
     * @param email Dirección de correo electrónico (login).
     * @param password Contraseña en texto plano.
     * @param rolId ID del rol a asignar.
     */
    public UsuarioRequestDTO(String nombre, String email, String password, Long rolId) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el email del usuario.
     * @return El email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario.
     * @param email El nuevo email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña proporcionada (sin cifrar).
     * Puede ser null si no se desea cambiar la contraseña durante una actualización.
     * @return La contraseña en texto plano, o null.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña (sin cifrar).
     * @param password La nueva contraseña en texto plano.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el ID del rol a asignar al usuario.
     * @return El ID del rol.
     */
    public Long getRolId() {
        return rolId;
    }

    /**
     * Establece el ID del rol a asignar al usuario.
     * @param rolId El nuevo ID del rol.
     */
    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}