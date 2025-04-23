package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Método nuevo para búsqueda combinada
    @Query("SELECT p FROM Proveedor p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND " +
            "LOWER(p.direccion) LIKE LOWER(CONCAT('%', :direccion, '%'))")
    List<Proveedor> findByNombreAndDireccionContainingIgnoreCase(
            @Param("nombre") String nombre,
            @Param("direccion") String direccion
    );

    // Resto de métodos existentes...
    @Query("SELECT DISTINCT p FROM Proveedor p JOIN p.items i WHERE TYPE(i) = com.telastech360.crmTT360.entity.MateriaPrima AND i.tipoMaterial = :tipoMaterial")
    List<Proveedor> findByTipoMaterial(@Param("tipoMaterial") MateriaPrima.TipoMaterial tipoMaterial);

    Optional<Proveedor> findByEmail(String email);
    Optional<Proveedor> findByNombre(String nombre);
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    Optional<Proveedor> findByTelefono(String telefono);

    @Query("SELECT p FROM Proveedor p WHERE p.direccion LIKE %:ubicacion%")
    List<Proveedor> buscarPorUbicacion(@Param("ubicacion") String ubicacion);

    boolean existsByEmail(String email);
    List<Proveedor> findAllByOrderByNombreAsc();

    @Query("SELECT p, COUNT(i) as totalProductos FROM Proveedor p LEFT JOIN p.items i GROUP BY p")
    List<Object[]> findProveedoresWithProductCount();
}