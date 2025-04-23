package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param; // <<<<<<<<<< AÑADIR esta importación

import java.util.List;
import java.util.Optional;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    boolean existsByNombre(String nombre);
    List<Bodega> findAllByOrderByNombreAsc();

    // Añadido @Param
    @Query("SELECT COUNT(b) > 0 FROM Bodega b WHERE b.estado.estadoId = :estadoId")
    boolean existsByEstadoId(@Param("estadoId") Long estadoId);

    List<Bodega> findByTipoBodega(Bodega.TipoBodega tipo);
    List<Bodega> findByResponsable(Usuario responsable);
    List<Bodega> findByUbicacionContaining(String ubicacion);

    // Asegurado stockDisponible
    @Query("SELECT b FROM Bodega b WHERE b.capacidadMaxima > (SELECT COALESCE(SUM(i.stockDisponible), 0) FROM Item i WHERE i.bodega = b)")
    List<Bodega> findBodegasConCapacidadDisponible();

    // Añadido @Param
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Item i WHERE i.bodega.bodegaId = :bodegaId") // Asumo Bodega ID es 'bodegaId'
    boolean existsBodegaWithItems(@Param("bodegaId") Long bodegaId);

    // Añadido @Param
    @Query("SELECT b FROM Bodega b WHERE (:nombre IS NULL OR b.nombre LIKE %:nombre%) AND (:tipo IS NULL OR b.tipoBodega = :tipo) AND (:estadoId IS NULL OR b.estado.estadoId = :estadoId)")
    List<Bodega> buscarBodegasFiltradas(@Param("nombre") String nombre, @Param("tipo") Bodega.TipoBodega tipo, @Param("estadoId") Long estadoId);
}