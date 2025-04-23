package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    // ========== CONSULTAS BÁSICAS ========== //

    /**
     * Busca un rol por su nombre (case-sensitive).
     *
     * @param nombre Nombre del rol.
     * @return Rol encontrado (si existe).
     */
    Optional<Rol> findByNombre(String nombre);

    /**
     * Busca un rol por su nombre ignorando mayúsculas/minúsculas.
     *
     * @param nombre Nombre del rol.
     * @return Rol encontrado (si existe).
     */
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    /**
     * Verifica si existe un rol con el nombre especificado.
     *
     * @param nombre Nombre del rol.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByNombre(String nombre);

    /**
     * Lista todos los roles ordenados por nombre ascendente.
     *
     * @return Lista de roles ordenados.
     */
    List<Rol> findAllByOrderByNombreAsc();

    // ========== CONSULTAS PERSONALIZADAS ========== //

    /**
     * Busca roles cuya descripción contenga cierto texto (case-insensitive).
     *
     * @param texto Texto a buscar en la descripción.
     * @return Lista de roles que coinciden.
     */
    @Query("SELECT r FROM Rol r WHERE LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Rol> buscarPorDescripcion(@Param("texto") String texto);

    /**
     * Cuenta el número de permisos asignados a un rol específico.
     *
     * @param rolId ID del rol.
     * @return Número de permisos asociados al rol.
     */
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    long countPermisosForRol(@Param("rolId") Long rolId);

    @Query("SELECT rp.id.permisoId FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    List<Long> findPermisoIdsByRolId(@Param("rolId") Long rolId);

    // ========== OPERACIONES DE MODIFICACIÓN ========== //

    @Modifying
    @Query("DELETE FROM RolPermiso rp WHERE rp.id.rolId = :rolId AND rp.id.permisoId = :permisoId")
    void deleteByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);
}