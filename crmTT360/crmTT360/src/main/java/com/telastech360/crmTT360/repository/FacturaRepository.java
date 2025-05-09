package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.entity.Factura.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // ========== MÉTODOS CRUD BÁSICOS ========== //
    List<Factura> findByTipoMovimiento(TipoMovimiento tipo);

    @Query("SELECT f FROM Factura f WHERE f.fechaCreacion BETWEEN :inicio AND :fin")
    List<Factura> findByFechaBetween(@Param("inicio") Timestamp inicio, @Param("fin") Timestamp fin);

    // --- Corrección aplicada (ya presente en tu código) ---
    // Asume que la entidad Factura tiene un campo booleano 'estadoPago'
    @Query("SELECT f FROM Factura f WHERE f.estadoPago = false") // Consulta para facturas pendientes
    List<Factura> findFacturasPendientesDePago();
    // -------------------------------------------------------


    @Query("SELECT FUNCTION('DATE', f.fechaCreacion), SUM(f.total) " +
            "FROM Factura f " +
            "WHERE f.fechaCreacion BETWEEN :inicio AND :fin " +
            "GROUP BY FUNCTION('DATE', f.fechaCreacion)")
    List<Object[]> getTotalFacturadoPorDia(@Param("inicio") Timestamp inicio, @Param("fin") Timestamp fin);

    @Query("SELECT f FROM Factura f WHERE f.total > :montoMinimo ORDER BY f.total DESC")
    List<Factura> findFacturasConTotalMayorA(@Param("montoMinimo") BigDecimal montoMinimo);

    // ========== MÉTODOS ADICIONALES (OPCIONALES) ========== //
    @Query("SELECT f FROM Factura f WHERE f.total BETWEEN :minTotal AND :maxTotal")
    List<Factura> findByTotalBetween(@Param("minTotal") BigDecimal minTotal, @Param("maxTotal") BigDecimal maxTotal);

    // --- Corrección aplicada (ya presente en tu código) ---
    // Asumo que el ID de Pedido es 'pedidoId' y la relación en Factura se llama 'pedido'
    @Query("SELECT COUNT(f) > 0 FROM Factura f WHERE f.pedido.pedidoId = :pedidoId")
    boolean existsByPedidoId(@Param("pedidoId") Long pedidoId);
    // -------------------------------------------------------

    @Modifying
    @Transactional
    // --- Corrección aplicada (ya presente en tu código) ---
    // Corregido: Usar el nombre real de la propiedad ID en Factura (facturaId) y añadir @Param
    @Query("UPDATE Factura f SET f.estadoPago = :estadoPago WHERE f.facturaId = :id")
    int actualizarEstadoPago(@Param("id") Long id, @Param("estadoPago") boolean estadoPago);
    // -------------------------------------------------------

    @Query("SELECT f FROM Factura f WHERE " +
            // Corregido: Usar el nombre real de la propiedad ID en Pedido (pedidoId) y añadir @Param
            "(:pedidoId IS NULL OR f.pedido.pedidoId = :pedidoId) AND " +
            "(:tipoMovimiento IS NULL OR f.tipoMovimiento = :tipoMovimiento) AND " +
            "(:minTotal IS NULL OR f.total >= :minTotal) AND " +
            "(:maxTotal IS NULL OR f.total <= :maxTotal) AND " +
            // Asegúrate que el nombre de la propiedad sea 'estadoPago' si es un boolean
            "(:estadoPago IS NULL OR f.estadoPago = :estadoPago)")
    List<Factura> busquedaAvanzada(
            @Param("pedidoId") Long pedidoId,
            @Param("tipoMovimiento") TipoMovimiento tipoMovimiento,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal,
            @Param("estadoPago") Boolean estadoPago); // Usar Boolean para permitir NULL en el filtro

    // Si necesitas buscar por ID de Factura, usa el método findById(Long id) de JpaRepository
    // Asumo que el ID de Factura es Long y se llama 'facturaId'
    // Optional<Factura> findByFacturaId(Long facturaId); // Si necesitas buscar por el nombre real del ID
}