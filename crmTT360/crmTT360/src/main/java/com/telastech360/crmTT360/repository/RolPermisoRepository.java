package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.RolPermiso;
import com.telastech360.crmTT360.entity.RolPermisoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    // ========== CONSULTAS BÁSICAS ========== //

    /**
     * Busca permisos por ID de rol.
     *
     * @param rolId ID del rol.
     * @return Lista de permisos.
     */
    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    List<Permiso> findPermisosByRolId(Long rolId);

    @Query("SELECT rp.rol FROM RolPermiso rp WHERE rp.id.permisoId = :permisoId")
    List<Rol> findRolesByPermisoId(Long permisoId);

    boolean existsById_RolIdAndId_PermisoId(Long rolId, Long permisoId);

    // ========== OPERACIONES DE ELIMINACIÓN ========== //

    /**
     * Elimina todas las relaciones de un rol.
     *
     * @param rolId ID del rol.
     */
    @Modifying
    @Query("DELETE FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    void deleteAllByRolId(Long rolId);

    @Modifying
    @Query("DELETE FROM RolPermiso rp WHERE rp.id.permisoId = :permisoId")
    void deleteAllByPermisoId(Long permisoId);

    @Modifying
    @Query("DELETE FROM RolPermiso rp WHERE rp.id.rolId = :rolId AND rp.id.permisoId = :permisoId")
    void deleteByRolIdAndPermisoId(Long rolId, Long permisoId);

    // ========== CONSULTAS ADICIONALES ========== //

    @Query("SELECT rp.id.permisoId FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    List<Long> findPermisoIdsByRolId(Long rolId);

    @Query("SELECT rp.id.rolId FROM RolPermiso rp WHERE rp.id.permisoId = :permisoId")
    List<Long> findRolIdsByPermisoId(Long permisoId);

    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.id.permisoId = :permisoId")
    long countRolesWithPermiso(Long permisoId);

    /**
     * Cuenta cuántos permisos tiene un rol específico.
     *
     * @param rolId ID del rol.
     * @return Número de permisos asociados al rol.
     */
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.id.rolId = :rolId")
    long countPermisosForRol(Long rolId);
}