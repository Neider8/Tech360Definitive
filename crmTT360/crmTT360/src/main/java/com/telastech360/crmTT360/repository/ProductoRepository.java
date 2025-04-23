package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Producto;
import com.telastech360.crmTT360.entity.Producto.TipoPrenda;
import com.telastech360.crmTT360.entity.Producto.Talla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda por tipo de prenda
    List<Producto> findByTipoPrenda(TipoPrenda tipoPrenda);

    // Búsqueda por talla
    List<Producto> findByTalla(Talla talla);

    // Búsqueda por color (contains, ignore case)
    List<Producto> findByColorContainingIgnoreCase(String color);

    // Búsqueda por temporada
    List<Producto> findByTemporada(String temporada);

    // Búsqueda de productos próximos a vencer
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento BETWEEN CURRENT_DATE AND :fechaLimite")
    List<Producto> findProductosPorVencer(@Param("fechaLimite") Date fechaLimite);

    // Búsqueda avanzada multicriterio
    @Query("SELECT p FROM Producto p WHERE " +
            "(:tipoPrenda IS NULL OR p.tipoPrenda = :tipoPrenda) AND " +
            "(:talla IS NULL OR p.talla = :talla) AND " +
            "(:color IS NULL OR p.color LIKE %:color%) AND " +
            "(:temporada IS NULL OR p.temporada = :temporada) AND " +
            "(:minPrecio IS NULL OR p.precio >= :minPrecio) AND " + // Corregido a p.precio
            "(:maxPrecio IS NULL OR p.precio <= :maxPrecio)") // Corregido a p.precio
    List<Producto> busquedaAvanzada(
            @Param("tipoPrenda") TipoPrenda tipoPrenda,
            @Param("talla") Talla talla,
            @Param("color") String color,
            @Param("temporada") String temporada,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio);

    // Productos más vendidos (para dashboard)
    @Query("SELECT p, SUM(pd.cantidad) as totalVendido " +
            "FROM Producto p JOIN p.pedidoDetalles pd " +
            "WHERE pd.pedido.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY p ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin);

    // Método para actualizar temporada en lote
    @Modifying
    @Query("UPDATE Producto p SET p.temporada = :temporada WHERE p.tipoPrenda = :tipoPrenda")
    int actualizarTemporadaPorTipo(
            @Param("tipoPrenda") TipoPrenda tipoPrenda,
            @Param("temporada") String temporada);

    // Obtener colores disponibles por tipo de prenda
    @Query("SELECT DISTINCT p.color FROM Producto p WHERE p.tipoPrenda = :tipoPrenda")
    List<String> findColoresDisponibles(@Param("tipoPrenda") TipoPrenda tipoPrenda);

    // Obtener tallas disponibles por tipo de prenda
    @Query("SELECT DISTINCT p.talla FROM Producto p WHERE p.tipoPrenda = :tipoPrenda")
    List<Talla> findTallasDisponibles(@Param("tipoPrenda") TipoPrenda tipoPrenda);
}