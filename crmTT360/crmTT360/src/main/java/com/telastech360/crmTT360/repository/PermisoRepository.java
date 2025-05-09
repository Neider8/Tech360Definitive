package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Permiso;
// Importar Param para @Param
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <<< Asegúrate de tener esta importación
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // Búsqueda exacta por nombre (case-sensitive)
    Optional<Permiso> findByNombre(String nombre);
    // Búsqueda por nombre ignorando mayúsculas/minúsculas
    Optional<Permiso> findByNombreIgnoreCase(String nombre);
    // Búsqueda por fragmento de nombre
    List<Permiso> findByNombreContaining(String fragmento);
    // Búsqueda por fragmento de descripción (JPQL personalizado)
    @Query("SELECT p FROM Permiso p WHERE p.descripcion LIKE %:texto%")
    List<Permiso> buscarPorDescripcion(@Param("texto") String texto); // @Param añadido
    // Verificación de existencia por nombre
    boolean existsByNombre(String nombre);

    // Obtener permisos asociados a un rol específico
    // ===>>> CORRECCIÓN APLICADA (ya presente en tu código): Usar alias y @Param <<<===
    @Query("SELECT p FROM Permiso p JOIN p.roles rol WHERE rol.rolId = :rolId")
    List<Permiso> findPermisosByRolId(@Param("rolId") Long rolId);
    // ===================================================================================

    // Obtener todos los permisos ordenados
    List<Permiso> findAllByOrderByNombreAsc();
}