package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, PedidoDetalle.PedidoDetalleId> {

    // Métodos corregidos usando IDs directamente
    List<PedidoDetalle> findByPedido_PedidoId(Long pedidoId);

    Optional<PedidoDetalle> findByPedido_PedidoIdAndProducto_ItemId(Long pedidoId, Long itemId);

    List<PedidoDetalle> findByProducto(Producto producto);

    @Modifying
    @Query("DELETE FROM PedidoDetalle pd WHERE pd.pedido = :pedido")
    void deleteByPedido(@Param("pedido") Pedido pedido);

    @Modifying
    @Query("UPDATE PedidoDetalle pd SET pd.precioUnitario = :nuevoPrecio " +
            "WHERE pd.producto.itemId = :itemId AND pd.pedido.estado.valor = 'PENDIENTE'")
    int actualizarPreciosEnPedidosPendientes(Long itemId, BigDecimal nuevoPrecio);

    @Query("SELECT pd.producto, SUM(pd.cantidad) as totalPedido " +
            "FROM PedidoDetalle pd " +
            "WHERE pd.pedido.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY pd.producto " +
            "ORDER BY totalPedido DESC")
    List<Object[]> findProductosMasPedidos(Timestamp fechaInicio, Timestamp fechaFin);

    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END " +
            "FROM PedidoDetalle pd " +
            "WHERE pd.producto.itemId = :itemId " +
            "AND pd.pedido.estado.valor NOT IN ('COMPLETADO', 'CANCELADO')")
    boolean existeEnPedidosActivos(Long itemId);

}