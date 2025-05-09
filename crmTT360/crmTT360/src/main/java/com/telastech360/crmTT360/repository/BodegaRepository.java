package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Bodega;
import com.telastech360.crmTT360.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    // <<<--- MÉTODO AÑADIDO --- >>>
    /**
     * Busca una bodega por su nombre exacto (case-sensitive por defecto).
     * Si necesitas case-insensitive, usa findByNombreIgnoreCase.
     * @param nombre El nombre de la bodega a buscar.
     * @return Un Optional que contiene la Bodega si se encuentra, o vacío si no.
     */
    Optional<Bodega> findByNombre(String nombre);
    // <<<---------------------- >>>

    boolean existsByNombre(String nombre);
    List<Bodega> findAllByOrderByNombreAsc();

    @Query("SELECT COUNT(b) > 0 FROM Bodega b WHERE b.estado.estadoId = :estadoId")
    boolean existsByEstadoId(@Param("estadoId") Long estadoId);

    List<Bodega> findByTipoBodega(Bodega.TipoBodega tipo);
    List<Bodega> findByResponsable(Usuario responsable);
    List<Bodega> findByUbicacionContaining(String ubicacion);

    @Query("SELECT b FROM Bodega b WHERE b.capacidadMaxima > (SELECT COALESCE(SUM(i.stockDisponible), 0) FROM Item i WHERE i.bodega = b)")
    List<Bodega> findBodegasConCapacidadDisponible();

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Item i WHERE i.bodega.bodegaId = :bodegaId")
    boolean existsBodegaWithItems(@Param("bodegaId") Long bodegaId);

    @Query("SELECT b FROM Bodega b WHERE (:nombre IS NULL OR b.nombre LIKE %:nombre%) AND (:tipo IS NULL OR b.tipoBodega = :tipo) AND (:estadoId IS NULL OR b.estado.estadoId = :estadoId)")
    List<Bodega> buscarBodegasFiltradas(@Param("nombre") String nombre, @Param("tipo") Bodega.TipoBodega tipo, @Param("estadoId") Long estadoId);
}