package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param; // <<<<<<<<<< AÑADIR esta importación

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // --- Corrección aplicada (ya presente en tu código) ---
    @Query("SELECT u FROM Usuario u JOIN u.rol r WHERE r.nombre = :rolNombre")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);
    // -------------------------------------------------------

    List<Usuario> findByRol_Nombre(String rolNombre);

    List<Usuario> findByEstado(String estado);

    // --- Corrección aplicada (ya presente en tu código) ---
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:termino% OR u.email LIKE %:termino%")
    List<Usuario> buscarPorNombreOEmail(@Param("termino") String termino);
    // -------------------------------------------------------

    List<Usuario> findByNombreContainingOrEmailContaining(String nombre, String email);

    List<Usuario> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombreTerm, String emailTerm);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u ORDER BY u.nombre ASC")
    List<Usuario> findAllOrderedByName();
    List<Usuario> findAllByOrderByNombreAsc();

    // --- Corrección aplicada (ya presente en tu código) ---
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmailForAuthentication(@Param("email") String email);
    // -------------------------------------------------------

    // --- Corrección aplicada (ya presente en tu código) ---
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    long countByRolNombre(@Param("rolNombre") String rolNombre);
    // -------------------------------------------------------
    long countByRol_Nombre(String rolNombre);
}