package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Item.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByCodigo(String codigo);
    List<Item> findAllByOrderByNombreAsc();
    List<Item> findByNombreContainingIgnoreCase(String nombre);
    List<Item> findByTipoItem(TipoItem tipo);
    List<Item> findByCategoria(Categoria categoria);
    long countByProveedor(Proveedor proveedor);

    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.estado.estadoId = :estadoId")
    boolean existsByEstadoId(@Param("estadoId") Long estadoId);

    @Query("SELECT i FROM Item i WHERE i.stockDisponible < i.stockMinimo")
    List<Item> findItemsConStockBajo();

    @Query("SELECT i FROM Item i WHERE i.fechaVencimiento BETWEEN :hoy AND :fechaLimite")
    List<Item> findItemsPorVencer(@Param("hoy") Date hoy, @Param("fechaLimite") Date fechaLimite);

    @Modifying
    @Query("UPDATE Item i SET i.stockDisponible = i.stockDisponible + :cantidad WHERE i.itemId = :itemId")
    void actualizarStock(@Param("itemId") Long itemId, @Param("cantidad") Integer cantidad);

    @Query("SELECT i.tipoItem, COUNT(i), SUM(i.stockDisponible), SUM(i.precio * i.stockDisponible) FROM Item i GROUP BY i.tipoItem")
    List<Object[]> getResumenInventario();

    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM PedidoDetalle pd WHERE pd.producto.itemId = :itemId AND pd.pedido.estado.valor NOT IN ('COMPLETADO', 'CANCELADO')")
    boolean existeEnPedidosActivos(@Param("itemId") Long itemId);
}