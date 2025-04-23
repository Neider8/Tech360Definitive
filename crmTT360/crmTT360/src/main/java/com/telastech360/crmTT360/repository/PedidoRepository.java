package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado_Valor(String valor);
    List<Pedido> findByCliente(ClienteInterno cliente);
    List<Pedido> findByEstado(Estado estado);
    List<Pedido> findByFechaPedidoBetween(Timestamp inicio, Timestamp fin);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.responsable.usuarioId = :responsableId")
    List<Pedido> findByResponsableId(@Param("responsableId") Long responsableId);

    @Query("SELECT p FROM Pedido p WHERE " +
            "p.fechaPedido >= FUNCTION('DATE_SUB', CURRENT_TIMESTAMP, 1, 'DAY') AND " +
            "p.estado.valor = 'PENDIENTE'")
    List<Pedido> findPedidosUrgentes();

    @Query("SELECT p FROM Pedido p WHERE " +
            "(:clienteId IS NULL OR p.cliente.clienteId = :clienteId) AND " +
            "(:estadoId IS NULL OR p.estado.estadoId = :estadoId) AND " +
            "(:fechaInicio IS NULL OR p.fechaPedido >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR p.fechaFin <= :fechaFin) AND " +
            "(:responsableId IS NULL OR p.cliente.responsable.usuarioId = :responsableId)")
    List<Pedido> busquedaAvanzada(
            @Param("clienteId") Long clienteId,
            @Param("estadoId") Long estadoId,
            @Param("fechaInicio") Timestamp fechaInicio,
            @Param("fechaFin") Timestamp fechaFin,
            @Param("responsableId") Long responsableId);

    @Query("SELECT e.valor, COUNT(p) FROM Pedido p JOIN p.estado e GROUP BY e.valor")
    List<Object[]> getEstadisticasPorEstado();

    // Corregido: Cambiado 'pd.item.itemId' a 'pd.producto.itemId'
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.detalles pd WHERE pd.producto.itemId = :itemId")
    List<Pedido> findPedidosConteniendoItem(@Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE Pedido p SET p.estado = :estado WHERE p.pedidoId IN :ids")
    int actualizarEstadoPedidos(
            @Param("ids") List<Long> ids,
            @Param("estado") Estado estado);

    @Query(value = "SELECT p FROM Pedido p ORDER BY p.fechaPedido DESC")
    List<Pedido> findTop10ByOrderByFechaPedidoDesc();

    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.estado.estadoId = :estadoId")
    boolean existsByEstadoId(@Param("estadoId") Long estadoId);
}